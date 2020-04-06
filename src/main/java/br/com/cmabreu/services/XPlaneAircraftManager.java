package br.com.cmabreu.services;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.cmabreu.models.XPlaneAircraft;
import br.com.cmabreu.udp.XPlaneDataPacket;
import hla.rti1516e.AttributeHandle;
import hla.rti1516e.AttributeHandleSet;
import hla.rti1516e.InteractionClassHandle;
import hla.rti1516e.ObjectClassHandle;
import hla.rti1516e.RTIambassador;

public class XPlaneAircraftManager {
	private RTIambassador rtiAmb;
	
	private InteractionClassHandle interactionHandle;   

	// caches of handle types - set once we join a federation
	protected AttributeHandleSet attributes;
	protected ObjectClassHandle entityHandle;
	protected AttributeHandle entityTypeHandle;
	protected AttributeHandle spatialHandle;
	protected AttributeHandle forceIdentifierHandle;
	protected AttributeHandle markingHandle;	
	protected AttributeHandle isConcealedHandle;	
	
	
	private List<XPlaneAircraft> aircrafts;
	private static XPlaneAircraftManager instance;
	private Logger logger = LoggerFactory.getLogger( XPlaneAircraftManager.class );
	
	public static XPlaneAircraftManager getInstance() {
		return instance;
	}
	
	public static void startInstance( RTIambassador rtiAmb ) throws Exception {
		instance = new XPlaneAircraftManager( rtiAmb );
	}
	
	private XPlaneAircraftManager( RTIambassador rtiAmb ) throws Exception {
		logger.info("X-Plane Aircraft Manager ativo");
		this.aircrafts = new ArrayList<XPlaneAircraft>();
		this.rtiAmb = rtiAmb;
		this.publish();
	}
	
	
	private void publish() throws Exception {
		// get all the handle information for the attributes
		this.entityHandle = this.rtiAmb.getObjectClassHandle("HLAobjectRoot.BaseEntity.PhysicalEntity.Platform.Aircraft");
		this.entityTypeHandle = this.rtiAmb.getAttributeHandle(entityHandle, "EntityType");
		this.spatialHandle = this.rtiAmb.getAttributeHandle(entityHandle, "Spatial");
		this.forceIdentifierHandle = this.rtiAmb.getAttributeHandle(entityHandle, "ForceIdentifier");
		this.markingHandle = this.rtiAmb.getAttributeHandle(entityHandle, "Marking");
		this.isConcealedHandle = this.rtiAmb.getAttributeHandle(entityHandle, "IsConcealed");
		
		
		// package the information into a handle set
		attributes = this.rtiAmb.getAttributeHandleSetFactory().create();
		attributes.add(entityTypeHandle);
		attributes.add(spatialHandle);
		attributes.add(forceIdentifierHandle);
		attributes.add(markingHandle);
		attributes.add(isConcealedHandle);
        this.rtiAmb.publishObjectClassAttributes( this.entityHandle, attributes );   
        
        this.interactionHandle = this.rtiAmb.getInteractionClassHandle("Acknowledge");
        this.rtiAmb.publishInteractionClass(interactionHandle);
	}

	/* GETTERS e SETTERS */
	
	public RTIambassador getRtiAmb() {
		return rtiAmb;
	}


	public void setRtiAmb(RTIambassador rtiAmb) {
		this.rtiAmb = rtiAmb;
	}

	public InteractionClassHandle getInteractionHandle() {
		return interactionHandle;
	}


	public void setInteractionHandle(InteractionClassHandle interactionHandle) {
		this.interactionHandle = interactionHandle;
	}


	public ObjectClassHandle getEntityHandle() {
		return entityHandle;
	}

	public void setEntityHandle(ObjectClassHandle entityHandle) {
		this.entityHandle = entityHandle;
	}

	public AttributeHandle getEntityTypeHandle() {
		return entityTypeHandle;
	}

	public void setEntityTypeHandle(AttributeHandle entityTypeHandle) {
		this.entityTypeHandle = entityTypeHandle;
	}

	public AttributeHandle getSpatialHandle() {
		return spatialHandle;
	}

	public void setSpatialHandle(AttributeHandle spatialHandle) {
		this.spatialHandle = spatialHandle;
	}

	public AttributeHandle getForceIdentifierHandle() {
		return forceIdentifierHandle;
	}

	public void setForceIdentifierHandle(AttributeHandle forceIdentifierHandle) {
		this.forceIdentifierHandle = forceIdentifierHandle;
	}

	public AttributeHandle getMarkingHandle() {
		return markingHandle;
	}

	public void setMarkingHandle(AttributeHandle markingHandle) {
		this.markingHandle = markingHandle;
	}

	public List<XPlaneAircraft> getAircrafts() {
		return aircrafts;
	}

	public void setAircrafts(List<XPlaneAircraft> aircrafts) {
		this.aircrafts = aircrafts;
	}

	public AttributeHandle getIsConcealedHandle() {
		return isConcealedHandle;
	}

	public void setIsConcealedHandle(AttributeHandle isConcealedHandle) {
		this.isConcealedHandle = isConcealedHandle;
	}

	public void update( XPlaneDataPacket dataPacket ) throws Exception {
		// Identifica o dado pelo nome do computador que enviou
		String identificador = dataPacket.getHostName() ;
		
		// Procura na minha lista pra ver se eh uma aeronave nova
		for( XPlaneAircraft ac : aircrafts  ) {
			
			if( ac.isMe(identificador) ) {
				// Ja tenho essa cadastrada. So atualiza os seus dados e sai
				ac.update(dataPacket);
				return;
			}
			
		}
		
		// Se eu cheguei aqui eh porque nao tenho essa aeronave ainda.
		// Preciso criar uma nova.
		XPlaneAircraft temp = new XPlaneAircraft( this, identificador );
		this.aircrafts.add( temp );
		
		// E ja atualizo suas informacoes!
		temp.update(dataPacket);
		
	}
	
	
}
