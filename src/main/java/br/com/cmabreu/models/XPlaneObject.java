package br.com.cmabreu.models;

import java.io.Serializable;

import br.com.cmabreu.codec.Codec;
import br.com.cmabreu.codec.SpatialVariant;
import br.com.cmabreu.managers.IManager;
import br.com.cmabreu.misc.EncoderDecoder;
import br.com.cmabreu.misc.Environment;
import hla.rti1516e.AttributeHandle;
import hla.rti1516e.AttributeHandleValueMap;
import hla.rti1516e.ObjectInstanceHandle;
import hla.rti1516e.RtiFactoryFactory;

public class XPlaneObject implements Serializable {
	private ObjectInstanceHandle instanceHandle;
	private String className;
	private String objectName;
	private IManager manager;
	private Codec codec;
	private Environment env;
	
	private static final long serialVersionUID = 1L;
	private double lat;
	private double lon;
	private double ele;
	
	private float psi;
	private float the;
	private float phi;
	
	private int index;
	private String objectPath;
	private int ground;
	private float smoke;
	
	private boolean active;
	
	public boolean isActive() {
		return active;
	}
	
	public void deactivate() {
		this.active = false;
	}
	
	public void activate() {
		this.active = true;
	}

	
	public XPlaneObject( int index, String objectPath, ObjectInstanceHandle instanceHandle, String className, IManager manager, String objectName ) throws Exception {
		this.index = index;
		this.objectPath = objectPath;
		this.ele = 0;
		this.lat = -23.0;
		this.lon = -48.0;
		this.psi = 0.0f;
		this.the = 0.0f;
		this.phi = 0.0f;
		this.ground = 0;
		this.smoke = 0.0f;
		this.active = false;
		this.instanceHandle = instanceHandle;
		this.className = className;
		this.manager = manager;
		this.codec = new Codec( RtiFactoryFactory.getRtiFactory().getEncoderFactory() );
		this.env = new Environment();
		this.objectName = objectName;
	}
	
	public XPlaneObject( int index, double lat, double lon, double ele, float psi, float the, float phi, int ground, float smoke, String objectPath, String objectName ) {
		this.index = index;
		this.objectPath = objectPath;
		this.ele = ele;
		this.lat = lat;
		this.lon = lon;
		this.psi = psi;
		this.the = the;
		this.phi = phi;
		this.ground = ground;
		this.smoke = smoke;
		this.active = true;
		this.objectName = objectName;
		this.className = "RestInterfaceRequest";
	}

	public double[] getLatLonEle( ) {
		double[] latLonEle = new double[3];
		latLonEle[0] = this.lat;
		latLonEle[1] = this.lon;
		latLonEle[2] = this.ele;
		return latLonEle;
	}
	
	public float[] getPsiThePhi( ) {
		float[] psiThePhi = new float[3];
		psiThePhi[0] = this.psi;
		psiThePhi[1] = this.the;
		psiThePhi[2] = this.phi;
		return psiThePhi;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLon() {
		return lon;
	}

	public void setLon(double lon) {
		this.lon = lon;
	}

	public double getEle() {
		return ele;
	}

	public void setEle(double ele) {
		this.ele = ele;
	}

	public float getPsi() {
		return psi;
	}

	public void setPsi(float psi) {
		this.psi = psi;
	}

	public float getThe() {
		return the;
	}

	public void setThe(float the) {
		this.the = the;
	}

	public float getPhi() {
		return phi;
	}

	public void setPhi(float phi) {
		this.phi = phi;
	}

	public int getGround() {
		return ground;
	}

	public void setGround(int ground) {
		this.ground = ground;
	}

	public float getSmoke() {
		return smoke;
	}

	public void setSmoke(float smoke) {
		this.smoke = smoke;
	}

	public int getIndex() {
		return index;
	}

	public String getObjectPath() {
		return objectPath;
	}

	public String getClassName() {
		return className;
	}
	
	public ObjectInstanceHandle getInstanceHandle() {
		return instanceHandle;
	}
	
	public int getHandle() throws Exception {
		EncoderDecoder enc = new EncoderDecoder();
		return enc.getObjectHandle( instanceHandle );
	}
	
	// Atualizo os atributos internos do objeto
	// eh sincronizado para nao colocar o objeto num estado inconsistente
	// por acesso multiplo ao metodo. Torna a atualizacao atomica.
	public synchronized void updateAttributes(AttributeHandleValueMap theAttributes ) throws Exception {
		 
		for (AttributeHandle attributeHandle : theAttributes.keySet() ) {
			// Guarda os valores do atributo 
			byte[] attributeData = theAttributes.get(attributeHandle);
			
			// Procura que atributo eh esse
			// Para manter generico so vou processar os dados de orientação e posição
			if( attributeHandle.equals( this.manager.getSpatialHandle() ) ) {
				processaSpatial( attributeData );
			}
			
		}
	}
	
	/**
	 * 
	 * 		PROCESSA CADA ATRIBUTO E ATUALIZA ESTE OBJETO
	 * 
	 */

	private void processaSpatial( byte[] bytes ) throws Exception {
		SpatialVariant spatialVariant = this.codec.decodeSpatialVariant( bytes );
		double[] geo = this.env.getGeodesicLocation( spatialVariant.getWorldLocation() ); 
		float[] orientation = spatialVariant.getOrientation();
		
		this.lat = geo[ Environment.LAT ];
		this.lon = geo[ Environment.LON ];
		this.ele = geo[ Environment.ALT ];
		
		this.phi = orientation[ SpatialVariant.PHI ]; 
		this.the = orientation[ SpatialVariant.THETA ]; 
		this.psi = orientation[ SpatialVariant.PSI ];
		
		System.out.println(" > Recebi " + this.getObjectName() + " " + this.getClassName() );
		System.out.println(" > (" + this.lat + "," + this.lon + "," + this.ele + ") (" + this.phi + "," + this.the + "," + this.psi + ")" );
	}

	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}
	
	public String getObjectName() {
		return objectName;
	}
	
}
