package br.com.cmabreu;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.cmabreu.services.FederateService;
import hla.rti1516e.AttributeHandleSet;
import hla.rti1516e.AttributeHandleValueMap;
import hla.rti1516e.InteractionClassHandle;
import hla.rti1516e.NullFederateAmbassador;
import hla.rti1516e.ObjectClassHandle;
import hla.rti1516e.ObjectInstanceHandle;
import hla.rti1516e.OrderType;
import hla.rti1516e.ParameterHandleValueMap;
import hla.rti1516e.TransportationTypeHandle;
import hla.rti1516e.exceptions.FederateInternalError;

public class FederateAmbassador extends NullFederateAmbassador {
	private FederateService federateService;
	private Logger logger = LoggerFactory.getLogger( FederateAmbassador.class );


	public FederateAmbassador( FederateService federateService ){
		this.federateService = federateService;
	}

	
	@Override
	public void receiveInteraction(InteractionClassHandle arg0, ParameterHandleValueMap arg1, byte[] arg2,
			OrderType arg3, TransportationTypeHandle arg4, SupplementalReceiveInfo arg5 ) throws FederateInternalError {
		// 
	}	

	@Override
	public void provideAttributeValueUpdate(ObjectInstanceHandle theObject,	AttributeHandleSet theAttributes, byte[] userSuppliedTag) throws FederateInternalError {
		federateService.provideAttributeValueUpdate( theObject, theAttributes, userSuppliedTag );
	}	
	
	@Override
	public void discoverObjectInstance( ObjectInstanceHandle theObject, ObjectClassHandle theObjectClass, String objectName ) throws FederateInternalError {
		federateService.discoverObjectInstance(theObject, theObjectClass, objectName);
	}

	@Override
	public void reflectAttributeValues( ObjectInstanceHandle theObject, AttributeHandleValueMap theAttributes, byte[] tag, OrderType sentOrder,
			TransportationTypeHandle transport, SupplementalReflectInfo reflectInfo ) throws FederateInternalError {
		federateService.reflectAttributeValues( theObject, theAttributes, tag, sentOrder );
	}

	
	@Override
	public void removeObjectInstance(ObjectInstanceHandle theObject, byte[] tag, OrderType orderType, SupplementalRemoveInfo supInfo) throws FederateInternalError {
		federateService.removeObjectInstance( theObject, tag, orderType, supInfo );
	}
	

}
