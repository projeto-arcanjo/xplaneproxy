package br.com.cmabreu.udp;

import java.io.Serializable;

public class TesteOBJLStruct implements Serializable {
	private static final long serialVersionUID = 1L;
	private int index;
	private double[] latLonEle;
	private float[] psiThePhi;
	private int ground;
	private float smoke;
	
	public TesteOBJLStruct(int index, double[] latLonEle, float[] psiThePhi, int ground, float smoke) {
		super();
		this.index = index;
		this.latLonEle = latLonEle;
		this.psiThePhi = psiThePhi;
		this.ground = ground;
		this.smoke = smoke;
	}
	
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	public double[] getLatLonEle() {
		return latLonEle;
	}
	public void setLatLonEle(double[] latLonEle) {
		this.latLonEle = latLonEle;
	}
	public float[] getPsiThePhi() {
		return psiThePhi;
	}
	public void setPsiThePhi(float[] psiThePhi) {
		this.psiThePhi = psiThePhi;
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

}
