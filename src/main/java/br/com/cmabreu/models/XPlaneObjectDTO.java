package br.com.cmabreu.models;

import java.io.Serializable;

public class XPlaneObjectDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	private String className;
	private String objectName;
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
	
	public XPlaneObjectDTO( XPlaneObject object )  {
		this.index = object.getIndex();
		this.objectPath = object.getObjectPath();
		this.ele = object.getEle();
		this.lat = object.getLat();
		this.lon = object.getLon();
		this.psi = object.getPsi();
		this.the = object.getThe();
		this.phi = object.getPhi();
		this.ground = object.getGround();
		this.smoke = object.getSmoke();
		this.active = object.isActive();
		this.className = object.getClassName();
		this.objectName = object.getObjectName();
	}

	public String getClassName() {
		return className;
	}

	public String getObjectName() {
		return objectName;
	}

	public double getLat() {
		return lat;
	}

	public double getLon() {
		return lon;
	}

	public double getEle() {
		return ele;
	}

	public float getPsi() {
		return psi;
	}

	public float getThe() {
		return the;
	}

	public float getPhi() {
		return phi;
	}

	public int getIndex() {
		return index;
	}

	public String getObjectPath() {
		return objectPath;
	}

	public int getGround() {
		return ground;
	}

	public float getSmoke() {
		return smoke;
	}

	public boolean isActive() {
		return active;
	}
	
	
}
