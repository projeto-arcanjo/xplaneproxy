package br.com.cmabreu.managers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hla.rti1516e.AttributeHandle;
import hla.rti1516e.AttributeHandleSet;
import hla.rti1516e.ObjectClassHandle;
import hla.rti1516e.ObjectInstanceHandle;
import hla.rti1516e.RTIambassador;

public class PlatformSurfaceManager implements IManager {
	private RTIambassador rtiAmb;
	
	protected AttributeHandleSet attributes;
	protected ObjectClassHandle entityHandle;
	protected AttributeHandle entityTypeHandle;
	protected AttributeHandle spatialHandle;
	protected AttributeHandle forceIdentifierHandle;
	protected AttributeHandle markingHandle;	
	protected AttributeHandle isConcealedHandle;
	protected AttributeHandle entityIdentifierHandle;
	protected AttributeHandle damageStateHandle;
	private Logger logger = LoggerFactory.getLogger( PlatformSurfaceManager.class );
	private static PlatformSurfaceManager instance;
	
	public static void startInstance( RTIambassador rtiAmb ) throws Exception {
		instance = new PlatformSurfaceManager( rtiAmb );
	}

	
	private PlatformSurfaceManager( RTIambassador rtiAmb ) throws Exception {
		logger.info("SurfaceVessel Manager ativo");
		this.rtiAmb = rtiAmb;
		this.entityHandle = rtiAmb.getObjectClassHandle("BaseEntity.PhysicalEntity.Platform.SurfaceVessel");
		
		this.entityTypeHandle = this.rtiAmb.getAttributeHandle( this.entityHandle, "EntityType");        
		this.entityIdentifierHandle = this.rtiAmb.getAttributeHandle( this.entityHandle, "EntityIdentifier");  
		this.spatialHandle = this.rtiAmb.getAttributeHandle( this.entityHandle, "Spatial");  
		this.forceIdentifierHandle = this.rtiAmb.getAttributeHandle( this.entityHandle, "ForceIdentifier");  
		this.isConcealedHandle = this.rtiAmb.getAttributeHandle( this.entityHandle, "IsConcealed");   
		this.markingHandle = this.rtiAmb.getAttributeHandle( this.entityHandle, "Marking");  	                   
		this.damageStateHandle = this.rtiAmb.getAttributeHandle(entityHandle, "DamageState");
		
		this.attributes = this.rtiAmb.getAttributeHandleSetFactory().create();
		this.attributes.add( this.entityTypeHandle );
		this.attributes.add( this.spatialHandle );
		this.attributes.add( this.forceIdentifierHandle );
		this.attributes.add( this.markingHandle );
		this.attributes.add( this.isConcealedHandle );
		this.attributes.add( this.entityIdentifierHandle );
		this.attributes.add( this.damageStateHandle );
        
        this.rtiAmb.subscribeObjectClassAttributes( this.entityHandle, attributes );   
        
	}

	
	public static PlatformSurfaceManager getInstance() {
		return instance;
	}

	public void requestUpdateAll(ObjectInstanceHandle theObject) throws Exception {
		this.rtiAmb.requestAttributeValueUpdate( theObject, this.attributes, "XPLANE_ATTR_REQ".getBytes() );
	}


	public AttributeHandle getIsConcealedHandle() {
		return isConcealedHandle;
	}

	public AttributeHandle getDamageStateHandle() {
		return damageStateHandle;
	}
	
	public AttributeHandle getEntityIdentifierHandle() {
		return entityIdentifierHandle;
	}

	public ObjectClassHandle getEntityHandle() {
		return entityHandle;
	}

	public AttributeHandle getEntityTypeHandle() {
		return entityTypeHandle;
	}

	public AttributeHandle getSpatialHandle() {
		return spatialHandle;
	}

	public AttributeHandle getForceIdentifierHandle() {
		return forceIdentifierHandle;
	}

	public AttributeHandle getMarkingHandle() {
		return markingHandle;
	}	
	
	
	
}
