package br.com.cmabreu.udp;

import java.io.Serializable;

public class AircraftData implements Serializable {
	private static final long serialVersionUID = 1L;
	private double lat;
	private double lon;
	private double ele;
	
	private double pitch;
	private double roll;
	private double heading;
	
	private float aileron;
	private float elevator;
	private float rudder;
	private float throttle;
	private float gear;
	private float flaps;
	private float speedBrakes;
	private float aircraftNumber;
	
	public AircraftData( double[] posi, float[] ctrl ) {
    	this.lat = posi[0];
    	this.lon = posi[1];
    	this.ele = posi[2];
    	this.pitch = posi[3];
    	this.roll = posi[4];
    	this.heading = posi[5];
    	
    	this.elevator = ctrl[0];
    	this.aileron = ctrl[1];
    	this.rudder = ctrl[2];
    	this.throttle = ctrl[3];
    	this.gear = ctrl[4];
    	this.flaps = ctrl[5];
    	this.aircraftNumber = ctrl[6];
    	//this.speedBrakes = ctrl[7];
    	
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
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

	public double getPitch() {
		return pitch;
	}

	public double getRoll() {
		return roll;
	}

	public double getHeading() {
		return heading;
	}

	public float getAileron() {
		return aileron;
	}

	public float getElevator() {
		return elevator;
	}

	public float getRudder() {
		return rudder;
	}

	public float getThrottle() {
		return throttle;
	}

	public float getGear() {
		return gear;
	}

	public float getFlaps() {
		return flaps;
	}

	public float getSpeedBrakes() {
		return speedBrakes;
	}

	public float getAircraftNumber() {
		return aircraftNumber;
	}
	
	

}
