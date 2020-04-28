package br.com.cmabreu.udp;

import java.io.Serializable;

public class ObjectData implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private double lat;
	private double lon;
	private double ele;
	
	private float psi;
	private float the;
	private float phi;
	
	private long index;
	private String objectPath;
	private long ground;
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
	
	public ObjectData( int index, double lat, double lon, double ele, float psi, float the, float phi, long ground, float smoke, String objectPath ) {
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

	public long getGround() {
		return ground;
	}

	public void setGround(long ground) {
		this.ground = ground;
	}

	public float getSmoke() {
		return smoke;
	}

	public void setSmoke(float smoke) {
		this.smoke = smoke;
	}

	public long getIndex() {
		return index;
	}

	public String getObjectPath() {
		return objectPath;
	}

	
	
}
