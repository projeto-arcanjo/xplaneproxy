package br.com.cmabreu;

import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import br.com.cmabreu.misc.EncoderDecoder;
import hla.rti1516e.AttributeHandle;
import hla.rti1516e.AttributeHandleSet;
import hla.rti1516e.AttributeHandleValueMap;
import hla.rti1516e.InteractionClassHandle;
import hla.rti1516e.ObjectClassHandle;
import hla.rti1516e.ObjectInstanceHandle;
import hla.rti1516e.ParameterHandleValueMap;
import hla.rti1516e.RTIambassador;
import hla.rti1516e.ResignAction;
import hla.rti1516e.encoding.HLAinteger16BE;
import hla.rti1516e.encoding.HLAinteger32BE;
import hla.rti1516e.exceptions.RTIexception;
import hla.rti1516e.time.HLAfloat64Interval;
import hla.rti1516e.time.HLAfloat64Time;
import hla.rti1516e.time.HLAfloat64TimeFactory;

@Service
public class FederateManager {
	
	// these variables are accessible in the package
	protected double federateTime = 0.0;
	protected double federateLookahead = 1.0;

	protected boolean isRegulating = false;
	protected boolean isConstrained = false;
	protected boolean isAdvancing = false;

	protected boolean isAnnounced = false;
	protected boolean isReadyToRun = false;
	private HLAfloat64TimeFactory timeFactory;		// set when we join
	
	
	private Logger logger = LoggerFactory.getLogger( FederateManager.class );
	private EncoderDecoder encoder;	
	private RTIambassador rtiamb;
	
	
	// caches of handle types - set once we join a federation
	protected ObjectClassHandle sodaHandle;
	protected AttributeHandle cupsHandle;
	protected AttributeHandle flavHandle;
	protected InteractionClassHandle servedHandle;	
	
	public void initialize( RTIambassador rtiamb )  {
		this.rtiamb = rtiamb;
		try {
			encoder = new EncoderDecoder();
		} catch ( Exception e ) {
			logger.error( e.getMessage() );
		}
    }
	
	public EncoderDecoder getEncoder() {
		return encoder;
	}
	

	/**
	 * This method will attempt to enable the various time related properties for
	 * the federate
	 */
	public void enableTimePolicy() throws Exception{
		// NOTE: Unfortunately, the LogicalTime/LogicalTimeInterval create code is
		//       Portico specific. You will have to alter this if you move to a
		//       different RTI implementation. As such, we've isolated it into a
		//       method so that any change only needs to happen in a couple of spots 
		HLAfloat64Interval lookahead = timeFactory.makeInterval( this.federateLookahead );
		
		////////////////////////////
		// enable time regulation //
		////////////////////////////
		this.rtiamb.enableTimeRegulation( lookahead );

		// tick until we get the callback
		while( this.isRegulating == false ) {
			rtiamb.evokeMultipleCallbacks( 0.1, 0.2 );
		}
		
		/////////////////////////////
		// enable time constrained //
		/////////////////////////////
		this.rtiamb.enableTimeConstrained();
		
		// tick until we get the callback
		while( this.isConstrained == false )	{
			rtiamb.evokeMultipleCallbacks( 0.1, 0.2 );
		}
		
	}

	public void setTimeFactory() throws Exception {
		 timeFactory = (HLAfloat64TimeFactory)rtiamb.getTimeFactory();
	}	
	
	
	/**
	 * This method will update all the values of the given object instance. It will
	 * set the flavour of the soda to a random value from the options specified in
	 * the FOM (Cola - 101, Orange - 102, RootBeer - 103, Cream - 104) and it will set
	 * the number of cups to the same value as the current time.
	 * <p/>
	 * Note that we don't actually have to update all the attributes at once, we
	 * could update them individually, in groups or not at all!
	 */
	public void updateAttributeValues( ObjectInstanceHandle objectHandle ) throws RTIexception	{
		///////////////////////////////////////////////
		// create the necessary container and values //
		///////////////////////////////////////////////
		// create a new map with an initial capacity - this will grow as required
		AttributeHandleValueMap attributes = rtiamb.getAttributeHandleValueMapFactory().create(2);
		
		// create the collection to store the values in, as you can see
		// this is quite a lot of work. You don't have to use the encoding
		// helpers if you don't want. The RTI just wants an arbitrary byte[]

		// generate the value for the number of cups (same as the timestep)
		HLAinteger16BE cupsValue = encoder.createHLAinteger16BE( getTimeAsShort() );
		attributes.put( cupsHandle, cupsValue.toByteArray() );
		
		// generate the value for the flavour on our magically flavour changing drink
		// the values for the enum are defined in the FOM
		int randomValue = 101 + new Random().nextInt(3);
		HLAinteger32BE flavValue = encoder.createHLAinteger32BE( randomValue );
		attributes.put( flavHandle, flavValue.toByteArray() );

		//////////////////////////
		// do the actual update //
		//////////////////////////
		rtiamb.updateAttributeValues( objectHandle, attributes, generateTag() );
		
		// note that if you want to associate a particular timestamp with the
		// update. here we send another update, this time with a timestamp:
		HLAfloat64Time time = timeFactory.makeTime( this.federateTime + this.federateLookahead );
		rtiamb.updateAttributeValues( objectHandle, attributes, generateTag(), time );
	}
		
	
	/**
	 * This method will request a time advance to the current time, plus the given
	 * timestep. It will then wait until a notification of the time advance grant
	 * has been received.
	 */
	public void advanceTime( double timestep ) throws RTIexception	{
		// request the advance
		this.isAdvancing = true;
		HLAfloat64Time time = timeFactory.makeTime( this.federateTime + timestep );
		rtiamb.timeAdvanceRequest( time );
		
		// wait for the time advance to be granted. ticking will tell the
		// LRC to start delivering callbacks to the federate
		while( this.isAdvancing ) {
			rtiamb.evokeMultipleCallbacks( 0.0001, 0.2 );
		}
	}	
	
	/**
	 * This method will send out an interaction of the type FoodServed.DrinkServed. Any
	 * federates which are subscribed to it will receive a notification the next time
	 * they tick(). This particular interaction has no parameters, so you pass an empty
	 * map, but the process of encoding them is the same as for attributes.
	 */
	public void sendInteraction() throws RTIexception	{
		//////////////////////////
		// send the interaction //
		//////////////////////////
		ParameterHandleValueMap parameters = rtiamb.getParameterHandleValueMapFactory().create(0);
		rtiamb.sendInteraction( servedHandle, parameters, generateTag() );
		
		// if you want to associate a particular timestamp with the
		// interaction, you will have to supply it to the RTI. Here
		// we send another interaction, this time with a timestamp:
		HLAfloat64Time time = timeFactory.makeTime( this.federateTime + this.federateLookahead );
		rtiamb.sendInteraction( servedHandle, parameters, generateTag(), time );
	}	
	
	/**
	 * This method will attempt to delete the object instance of the given
	 * handle. We can only delete objects we created, or for which we own the
	 * privilegeToDelete attribute.
	 */
	public void deleteObjectInstance( int handle ) throws RTIexception {
		rtiamb.deleteObjectInstance( encoder.getObjectHandle( handle ), generateTag() );
	}

	private short getTimeAsShort() {
		return (short)this.federateTime;
	}

	private byte[] generateTag() {
		return ("(timestamp) " + System.currentTimeMillis()).getBytes();
	}	
	
	
	public void publishAndSubscribe() throws RTIexception {
		///////////////////////////////////////////////
		// publish all attributes of Food.Drink.Soda //
		///////////////////////////////////////////////
		// before we can register instance of the object class Food.Drink.Soda and
		// update the values of the various attributes, we need to tell the RTI
		// that we intend to publish this information

		// get all the handle information for the attributes of Food.Drink.Soda
		this.sodaHandle = rtiamb.getObjectClassHandle( "HLAobjectRoot.Food.Drink.Soda" );
		this.cupsHandle = rtiamb.getAttributeHandle( sodaHandle, "NumberCups" );
		this.flavHandle = rtiamb.getAttributeHandle( sodaHandle, "Flavor" );
		// package the information into a handle set
		AttributeHandleSet attributes = rtiamb.getAttributeHandleSetFactory().create();
		attributes.add( cupsHandle );
		attributes.add( flavHandle );
		
		// do the actual publication
		rtiamb.publishObjectClassAttributes( sodaHandle, attributes );

		////////////////////////////////////////////////////
		// subscribe to all attributes of Food.Drink.Soda //
		////////////////////////////////////////////////////
		// we also want to hear about the same sort of information as it is
		// created and altered in other federates, so we need to subscribe to it
		rtiamb.subscribeObjectClassAttributes( sodaHandle, attributes );

		//////////////////////////////////////////////////////////
		// publish the interaction class FoodServed.DrinkServed //
		//////////////////////////////////////////////////////////
		// we want to send interactions of type FoodServed.DrinkServed, so we need
		// to tell the RTI that we're publishing it first. We don't need to
		// inform it of the parameters, only the class, making it much simpler
		String iname = "HLAinteractionRoot.CustomerTransactions.FoodServed.DrinkServed";
		servedHandle = rtiamb.getInteractionClassHandle( iname );
		
		// do the publication
		rtiamb.publishInteractionClass( servedHandle );

		/////////////////////////////////////////////////////////
		// subscribe to the FoodServed.DrinkServed interaction //
		/////////////////////////////////////////////////////////
		// we also want to receive other interaction of the same type that are
		// sent out by other federates, so we have to subscribe to it first
		rtiamb.subscribeInteractionClass( servedHandle );
	}

	
	public ObjectInstanceHandle registerObjectInstance() throws Exception {
		return rtiamb.registerObjectInstance( sodaHandle );
	}

	public void resignFederationExecution() throws Exception {
		rtiamb.resignFederationExecution( ResignAction.DELETE_OBJECTS );
	}	
	
}
