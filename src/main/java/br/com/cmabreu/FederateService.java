package br.com.cmabreu;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import br.com.cmabreu.misc.Constants;
import br.com.cmabreu.udp.UDPServer;
import hla.rti1516e.CallbackModel;
import hla.rti1516e.ObjectInstanceHandle;
import hla.rti1516e.RTIambassador;
import hla.rti1516e.RtiFactoryFactory;
import hla.rti1516e.exceptions.FederatesCurrentlyJoined;
import hla.rti1516e.exceptions.FederationExecutionAlreadyExists;
import hla.rti1516e.exceptions.FederationExecutionDoesNotExist;
import hla.rti1516e.exceptions.NotConnected;
import hla.rti1516e.exceptions.RTIinternalError;

@Service
public class FederateService {
	private RTIambassador rtiamb;
	private FederateAmbassador fedamb;  			
	private Logger logger = LoggerFactory.getLogger( FederateService.class );
	
	
    @Value("${federation.fomfolder}")
    String fomFolder;	

    @Value("${federation.name}")
    String federationName;	
    
    @Value("${federation.federateName}")
    String federateName;	
    
    @Autowired
    private FederateManager federateManager;
    
    
    public void startUPDListener() {
    	int port = 49003;
    	new UDPServer( port ).start();
    }
    
    public void startRti() throws Exception {
    	// Inicia o servidor UDP para ouvir o X-Plane
    	startUPDListener();
    	
    	if( !fomFolder.endsWith("/") ) fomFolder = fomFolder + "/";
    	
		/////////////////////////////////////////////////
		// 1 & 2. create the RTIambassador and Connect //
		/////////////////////////////////////////////////    	
		createRtiAmbassador();
		connect();
		
		//////////////////////////////
		// 3. create the federation //
		//////////////////////////////		
		createFederation( federationName );
		
		////////////////////////////
		// 4. join the federation //
		////////////////////////////		
		joinFederation( federationName, federateName);
		
		
		////////////////////////////////
		// 5. announce the sync point //
		////////////////////////////////
		// announce a sync point to get everyone on the same page. if the point
		// has already been registered, we'll get a callback saying it failed,
		// but we don't care about that, as long as someone registered it
		rtiamb.registerFederationSynchronizationPoint( Constants.READY_TO_RUN, null );		
		
		// wait until the point is announced
		while( federateManager.isAnnounced == false ) {
			rtiamb.evokeMultipleCallbacks( 0.1, 0.2 );
			logger.info("Still waiting for announce");
		}
		
    }
    
	public void kickOff() throws Exception {
		
		///////////////////////////////////////////////////////
		// 6. achieve the point and wait for synchronization //
		///////////////////////////////////////////////////////
		// tell the RTI we are ready to move past the sync point and then wait
		// until the federation has synchronized on
		rtiamb.synchronizationPointAchieved( Constants.READY_TO_RUN );
		logger.info( "Achieved sync point: " + Constants.READY_TO_RUN + ", waiting for federation..." );
		while( federateManager.isReadyToRun == false ) {
			rtiamb.evokeMultipleCallbacks( 0.1, 0.2 );
		}
		
		/////////////////////////////
		// 7. enable time policies //
		/////////////////////////////
		// in this section we enable/disable all time policies
		// note that this step is optional!
		federateManager.enableTimePolicy();
		logger.info( "Time Policy Enabled" );		
		
		
		//////////////////////////////
		// 8. publish and subscribe //
		//////////////////////////////
		// in this section we tell the RTI of all the data we are going to
		// produce, and all the data we want to know about
		federateManager.publishAndSubscribe();
		logger.info( "Published and Subscribed" );		
		
		/////////////////////////////////////
		// 9. register an object to update //
		/////////////////////////////////////		
		ObjectInstanceHandle objectHandle = federateManager.registerObjectInstance();
		logger.info( "Registered Object, handle=" + objectHandle );
		
		
		/////////////////////////////////////
		// 10. do the main simulation loop //
		/////////////////////////////////////
		// here is where we do the meat of our work. in each iteration, we will
		// update the attribute values of the object we registered, and will
		// send an interaction.
		for( int i = 0; i < Constants.ITERATIONS; i++ ) {
			// 9.1 update the attribute values of the instance //
			federateManager.updateAttributeValues( objectHandle );
			
			// 9.2 send an interaction
			federateManager.sendInteraction();
			
			// 9.3 request a time advance and wait until we get it
			federateManager.advanceTime( 1.0 );
			logger.info( "Time Advanced to " + federateManager.federateTime );
		}

		
		//////////////////////////////////////
		// 11. delete the object we created //
		//////////////////////////////////////
		//deleteObjectInstance( HLA1516eHandle.fromHandle( objectHandle ) );  
		
		
		////////////////////////////////////
		// 12. resign from the federation //
		////////////////////////////////////
		//resignFederationExecution();
		
	}
	
	// This now can be called by a REST endpoint. See FederateController.resignFederationExecution()
	public void resignFederationExecution() throws Exception {
		federateManager.resignFederationExecution();
		logger.info( "Resigned from Federation" );		
	}
	
	
	// This now can be called by a REST endpoint. See FederateController.deleteObjectInstance()
	public void deleteObjectInstance( int objectHandle ) throws Exception {
		federateManager.deleteObjectInstance( objectHandle );
		logger.info( "Deleted Object, handle=" + objectHandle );
	}

	
	// This method now will be fired by a REST endpoint. See FederateController.destroyFederation()
	public void destroyFederation() {
		////////////////////////////////////////
		// 13. try and destroy the federation //
		////////////////////////////////////////
		// NOTE: we won't die if we can't do this because other federates
		//       remain. in that case we'll leave it for them to clean up
		try	{
			rtiamb.destroyFederationExecution( federationName );
			logger.info( "Destroyed Federation" );
		} catch( FederationExecutionDoesNotExist dne ) {
			logger.warn( "No need to destroy federation, it doesn't exist" );
		} catch( FederatesCurrentlyJoined fcj ) 	{
			logger.warn( "Didn't destroy federation, federates still joined" );
		} catch (NotConnected e) {
			logger.error( "Not Connected" );
		} catch (RTIinternalError e) {
			logger.error( e.getMessage() + " : " + e.getCause() );
		}
		
		// disconnect
		try {
			rtiamb.disconnect();
			logger.info( "Disconnected" );
		} catch( Exception e ) {
			logger.error( e.getMessage() + " : " + e.getCause() );
			e.printStackTrace();
		}			
		
	}
    
    
	private void createRtiAmbassador() throws Exception {
		logger.info( "Creating RTIambassador." );
		this.rtiamb = RtiFactoryFactory.getRtiFactory().getRtiAmbassador();
		logger.info( "Initializing the Manager." );
		federateManager.initialize( this.rtiamb );
	}   
	
	private void connect() throws Exception {
		logger.info( "Connecting..." );
		fedamb = new FederateAmbassador( federateManager );
		rtiamb.connect( fedamb, CallbackModel.HLA_IMMEDIATE );
	}	
	
	private void createFederation( String federationName ) throws Exception {
		// We attempt to create a new federation with the first three of the
		// restaurant FOM modules covering processes, food and drink		
		logger.info( "Creating Federation " + federationName );
		try	{
			URL[] modules = new URL[]{
				// The MIM file MUST be present	
				(new File( fomFolder + "HLAstandardMIM.xml")).toURI().toURL(),
			};
			rtiamb.createFederationExecution( federationName, modules );
			logger.info( "Created Federation. HLA Version " + rtiamb.getHLAversion() );
		} catch( FederationExecutionAlreadyExists exists ) {
			logger.error( "Didn't create federation, it already existed." );
		} catch( MalformedURLException urle )	{
			logger.error( "Exception loading one of the FOM modules from disk: " + urle.getMessage() );
			urle.printStackTrace();
			return;
		}
	}	
	
	private void joinFederation( String federationName, String federateName ) throws Exception  {
		URL[] joinModules = new URL[]{
			( new File( fomFolder + "RestaurantSoup.xml" ) ).toURI().toURL()
		};
		rtiamb.joinFederationExecution( federateName, "ExampleFederateType", federationName, joinModules );   
		logger.info( "Joined Federation as " + federateName );
		
		// cache the time factory for easy access
		federateManager.setTimeFactory();
		
	}	

	
}
