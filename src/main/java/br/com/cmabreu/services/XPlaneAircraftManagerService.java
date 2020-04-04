package br.com.cmabreu.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import br.com.cmabreu.models.XPlaneAircraft;
import br.com.cmabreu.udp.XPlaneDataPacket;
import hla.rti1516e.AttributeHandle;
import hla.rti1516e.AttributeHandleSet;
import hla.rti1516e.InteractionClassHandle;
import hla.rti1516e.ObjectClassHandle;
import hla.rti1516e.RTIambassador;

@Service
public class XPlaneAircraftManagerService {
	private RTIambassador rtiAmb;
	private ObjectClassHandle classHandle;
	private InteractionClassHandle interactionHandle;   
	private AttributeHandle attributeEntityType;
	private AttributeHandle attributeSpatial;   
	private AttributeHandle attributeEntityId; 
	private AttributeHandle attributeDamageState; 
	private AttributeHandle attributeForceId; 
	private AttributeHandle attributeIsConcealed; 
	private AttributeHandle attributeMarking; 
	private boolean initialized = false;
	private List<XPlaneAircraft> aircrafts;
	
	public void init( RTIambassador rtiAmb ) throws Exception {
		if( isInitialized() ) return;
		this.aircrafts = new ArrayList<XPlaneAircraft>();
		this.rtiAmb = rtiAmb;
		this.classHandle = rtiAmb.getObjectClassHandle("BaseEntity.PhysicalEntity.Platform.Aircraft");
		this.publish();
		this.initialized = true;
	}
	
	private void publish() throws Exception {
		this.attributeEntityType = this.rtiAmb.getAttributeHandle( this.classHandle, "EntityType");        
		this.attributeEntityId = this.rtiAmb.getAttributeHandle( this.classHandle, "EntityIdentifier");  
		this.attributeSpatial = this.rtiAmb.getAttributeHandle( this.classHandle, "Spatial");  
		this.attributeDamageState = this.rtiAmb.getAttributeHandle( this.classHandle, "DamageState");   
		this.attributeForceId = this.rtiAmb.getAttributeHandle( this.classHandle, "ForceIdentifier");  
		this.attributeIsConcealed = this.rtiAmb.getAttributeHandle( this.classHandle, "IsConcealed");   
		this.attributeMarking = this.rtiAmb.getAttributeHandle( this.classHandle, "Marking");  	                   
        
        AttributeHandleSet attributeSet = this.rtiAmb.getAttributeHandleSetFactory().create();
        attributeSet.add( this.attributeEntityType );
        attributeSet.add( this.attributeSpatial );
        attributeSet.add( this.attributeEntityId );
        attributeSet.add( this.attributeDamageState );
        attributeSet.add( this.attributeForceId );
        attributeSet.add( this.attributeIsConcealed );
        attributeSet.add( this.attributeMarking );
        this.rtiAmb.publishObjectClassAttributes( this.classHandle, attributeSet );   
        
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


	public ObjectClassHandle getClassHandle() {
		return classHandle;
	}


	public void setClassHandle(ObjectClassHandle classHandle) {
		this.classHandle = classHandle;
	}


	public InteractionClassHandle getInteractionHandle() {
		return interactionHandle;
	}


	public void setInteractionHandle(InteractionClassHandle interactionHandle) {
		this.interactionHandle = interactionHandle;
	}


	public AttributeHandle getAttributeEntityType() {
		return attributeEntityType;
	}


	public void setAttributeEntityType(AttributeHandle attributeEntityType) {
		this.attributeEntityType = attributeEntityType;
	}


	public AttributeHandle getAttributeSpatial() {
		return attributeSpatial;
	}


	public void setAttributeSpatial(AttributeHandle attributeSpatial) {
		this.attributeSpatial = attributeSpatial;
	}


	public AttributeHandle getAttributeEntityId() {
		return attributeEntityId;
	}


	public void setAttributeEntityId(AttributeHandle attributeEntityId) {
		this.attributeEntityId = attributeEntityId;
	}


	public AttributeHandle getAttributeDamageState() {
		return attributeDamageState;
	}


	public void setAttributeDamageState(AttributeHandle attributeDamageState) {
		this.attributeDamageState = attributeDamageState;
	}


	public AttributeHandle getAttributeForceId() {
		return attributeForceId;
	}


	public void setAttributeForceId(AttributeHandle attributeForceId) {
		this.attributeForceId = attributeForceId;
	}


	public AttributeHandle getAttributeIsConcealed() {
		return attributeIsConcealed;
	}


	public void setAttributeIsConcealed(AttributeHandle attributeIsConcealed) {
		this.attributeIsConcealed = attributeIsConcealed;
	}


	public AttributeHandle getAttributeMarking() {
		return attributeMarking;
	}


	public void setAttributeMarking(AttributeHandle attributeMarking) {
		this.attributeMarking = attributeMarking;
	}
	
	public boolean isInitialized() {
		return initialized;
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
