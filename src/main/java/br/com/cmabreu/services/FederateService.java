package br.com.cmabreu.services;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import br.com.cmabreu.FederateAmbassador;
import br.com.cmabreu.managers.PlatformSurfaceManager;
import br.com.cmabreu.managers.XPlaneAircraftManager;
import br.com.cmabreu.misc.EncoderDecoder;
import br.com.cmabreu.models.XPlaneAircraft;
import br.com.cmabreu.models.XPlaneObject;
import br.com.cmabreu.threads.FederateExecutorThread;
import br.com.cmabreu.threads.UDPServerThread;
import hla.rti1516e.AttributeHandleSet;
import hla.rti1516e.AttributeHandleValueMap;
import hla.rti1516e.CallbackModel;
import hla.rti1516e.FederateAmbassador.SupplementalRemoveInfo;
import hla.rti1516e.ObjectClassHandle;
import hla.rti1516e.ObjectInstanceHandle;
import hla.rti1516e.OrderType;
import hla.rti1516e.RTIambassador;
import hla.rti1516e.ResignAction;
import hla.rti1516e.RtiFactoryFactory;
import hla.rti1516e.exceptions.CallNotAllowedFromWithinCallback;
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
	private EncoderDecoder encoder;	
	private Runnable udpServerThread;
	private Runnable federateExecutorThread;
	
	@Autowired
	private ObjectService objectService;
	
    @Value("${federation.fomfolder}")
    String fomFolder;	

    @Value("${federation.name}")
    String federationName;	
    
    @Value("${federation.federateName}")
    String federateName;	

    @Value("${udpserver.port}")
    int udpServerPort; 
    
	@PreDestroy
	public void onExit() {
		logger.info("Encerando Federado...");
		this.quit();
	}
    
    public void startRti() throws Exception {
    	if( !fomFolder.endsWith("/") ) fomFolder = fomFolder + "/";
    	
		createRtiAmbassador();
		connect();
		createFederation( federationName );
		
		joinFederation( federationName, federateName);
		
		XPlaneAircraftManager.startInstance( rtiamb );

		// Me interessa saber sobre navios... 
		PlatformSurfaceManager.startInstance( rtiamb ); 

		
		// Do not block the web browser interface!
		this.federateExecutorThread = new FederateExecutorThread( this );
		new Thread( this.federateExecutorThread ).start();
		
    	// Inicia o servidor UDP para ouvir o X-Plane
    	this.udpServerThread = new UDPServerThread( udpServerPort );
    	new Thread( this.udpServerThread ).start(); 
		
    }

    
    public void evokeCallBacks() {
    	try {
			rtiamb.evokeMultipleCallbacks( 0.1, 0.2 );
		} catch ( CallNotAllowedFromWithinCallback e ) {
			e.printStackTrace();
		} catch ( RTIinternalError e ) {
			e.printStackTrace();
		}
    }
    
    public void quit()  {
	
    	( (FederateExecutorThread)this.federateExecutorThread ).finish();
    	( (UDPServerThread)this.udpServerThread ).finish();
    	
    	
		try {
			resignFederationExecution();
			destroyFederation();
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
    }
    
	public void deleteObjectInstance( int objectHandle ) throws Exception {
		rtiamb.deleteObjectInstance( encoder.getObjectHandle( objectHandle ), generateTag() );
		logger.info( "Deleted Object, handle=" + objectHandle );
	}

	public void resignFederationExecution() throws Exception {
		rtiamb.resignFederationExecution( ResignAction.DELETE_OBJECTS );
	}	

	
	public void destroyFederation() {
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
		encoder = new EncoderDecoder();
	}   
	
	private void connect() throws Exception {
		logger.info( "Connecting..." );
		fedamb = new FederateAmbassador( this );
		rtiamb.connect( fedamb, CallbackModel.HLA_IMMEDIATE );
	}	
	
	private void createFederation( String federationName ) throws Exception {
		logger.info( "Creating Federation " + federationName );
		try	{
			URL[] modules = new URL[]{
				// The MIM file MUST be present	
				(new File( fomFolder + "HLAstandardMIM.xml")).toURI().toURL(),
				(new File( fomFolder + "RPR_FOM_v2.0_1516-2010.xml")).toURI().toURL(),
				(new File( fomFolder + "NETN-Base_v1.0.2.xml")).toURI().toURL(),
				(new File( fomFolder + "NETN-Aggregate_v1.0.4.xml")).toURI().toURL(),
				(new File( fomFolder + "NETN-Physical_v1.1.2.xml")).toURI().toURL(),
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
		rtiamb.joinFederationExecution( federateName, "ExampleFederateType", federationName );   
		logger.info( "Joined Federation as " + federateName );
	}	

	
	public EncoderDecoder getEncoder() {
		return encoder;
	}
	
	private byte[] generateTag() {
		return ( "XPLANE_" + System.currentTimeMillis()).getBytes();
	}	
	
	public XPlaneAircraft spawn( String identificador ) throws Exception {
		XPlaneAircraft aircraft = XPlaneAircraftManager.getInstance().spawn( identificador );
		return aircraft;
		
	}

	// Teste de atualizacao de aeronave. Vem pelo Controller e nao pelo X-Plane. 
	// So para testar o codigo
	public XPlaneAircraft updateTest( String identificador, float lat, float lon, float alt, float head, float pitch, float roll ) {
		return XPlaneAircraftManager.getInstance().updateTest( identificador, lat, lon, alt, head, pitch, roll);
	}
	
	
	/*
	 * 		METODOS QUE RESPONDEM PELO FEDERATE AMBASSADOR
	 * 
	 */
	

	public void provideAttributeValueUpdate(ObjectInstanceHandle theObject, AttributeHandleSet theAttributes, byte[] userSuppliedTag) {
		logger.warn("A RTI esta solicitando atualizacao de atributos");
		try {
			XPlaneAircraftManager.getInstance().sendAircraftToRTI( theObject );
		} catch ( Exception e ) {
			logger.error( e.getMessage() );
		}
	}

	public void discoverObjectInstance( ObjectInstanceHandle theObject, ObjectClassHandle theObjectClass, String objectName ) {
		
		String aircraftObjFile = "Resources/default scenery/sim objects/apt_aircraft/fighter/F4/F4_static.obj";
		String vesselObjFile = "Custom Scenery/OpenSceneryX/objects/vehicles/boats_ships/container/2/object.obj";
		
		
		try {
			
			XPlaneObject newObj = null;
			
			// O que eh isso que esta chegando?
			if( theObjectClass.equals( XPlaneAircraftManager.getInstance().getEntityHandle()  ) ) {
				// Eh um BaseEntity.PhysicalEntity.Platform.Aircraft
				// Guardo na lista
				newObj = objectService.prepare(theObject, aircraftObjFile, "Aircraft", XPlaneAircraftManager.getInstance(), objectName );
				// Peco seus atributos ao dono
				XPlaneAircraftManager.getInstance().requestUpdateAll( theObject );
			}

			
			if( theObjectClass.equals( PlatformSurfaceManager.getInstance().getEntityHandle()  ) ) {
				// Eh um BaseEntity.PhysicalEntity.Platform.SurfaceVessel
				// Guardo na lista
				newObj = objectService.prepare(theObject, vesselObjFile, "SurfaceVessel", PlatformSurfaceManager.getInstance(), objectName );
				// Peco seus atributos ao dono
				PlatformSurfaceManager.getInstance().requestUpdateAll( theObject );
			}

			if( newObj == null ) {
				logger.error("recebi um objeto novo " + objectName + " mas nao sei o que eh");
			}
			
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}

	// Os atributos do objeto chegaram! 
	public void reflectAttributeValues( ObjectInstanceHandle theObject, AttributeHandleValueMap theAttributes, byte[] tag, OrderType sentOrder ) {
		try {
			objectService.updateAttributes( theObject, theAttributes );
		} catch ( Exception e) {
			e.printStackTrace();
		}
	}

	public void removeObjectInstance(ObjectInstanceHandle theObject, byte[] tag, OrderType orderType, SupplementalRemoveInfo supInfo) {
		//
	}
	
	
}
