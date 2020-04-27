package br.com.cmabreu.udp;

import gov.nasa.xpc.XPlaneConnect;

public class TestNasaPlugin {

	// TESTADO. Funciona!
	private static void testText() throws Exception {
        String msg = "Teste de mensagem via Java.\nMagno está testando o plugin!\rE deu certo...\r\nAinda bem!";
        XPlaneConnect xpc = new XPlaneConnect( "127.0.0.1", 49009, 49007 );
        xpc.sendTEXT(msg, 200, 400);
        xpc.close();
	}
	
	
	// TESTADO. Funciona!
	private static void testContinuousReading() throws Exception {
		XPlaneConnect xpc = new XPlaneConnect( "127.0.0.1", 49009, 49007 ); 
        int aircraft = 0;
        
        for( int x=0; x < 100; x++ ) {
        	double[] posi = xpc.getPOSI(aircraft);
        	float[]  ctrl = xpc.getCTRL(aircraft);
        	System.out.format("Loc: (%4f, %4f, %4f) Ori:(%4f, %4f, %4f) Aileron:%2f Elevator:%2f Rudder:%2f\n",
        			posi[0], posi[1], posi[2], posi[3], posi[4], posi[5], ctrl[1], ctrl[0], ctrl[2]);
        }
		
        xpc.close();
	}
	
	
	// TESTADO. Funciona!
	private static void testSendOBJN() throws Exception {
		XPlaneConnect xpc = new XPlaneConnect( "127.0.0.1", 49009, 49007 );
		xpc.sendOBJN( 66, "Resources/default scenery/sim objects/apt_aircraft/fighter/F4/F4_static.obj" );
		xpc.close();
	}
	// TESTADO. Funciona!
	private static void testSendOBJL() throws Exception {
		XPlaneConnect xpc = new XPlaneConnect( "127.0.0.1", 49009, 49007 );
		
		double[] latLonEle = new double[3];
		latLonEle[0] = -22.80103;
		latLonEle[1] = -43.22729;
		latLonEle[2] = 5.0;
		
		float[] psiThePhi = new float[3];
		psiThePhi[0] = (float)0.00;
		psiThePhi[1] = (float)0.0;
		psiThePhi[2] = (float)0.0;		
		
		xpc.sendOBJL( 66, latLonEle, psiThePhi, 0, 50000.0f );
		xpc.close();
	}
	
	private static void testUpdateObject( float heading ) throws Exception {
		XPlaneConnect xpc = new XPlaneConnect( "127.0.0.1", 49009, 49007 );

		double[] latLonEle = new double[3];
		latLonEle[0] = -22.80103;
		latLonEle[1] = -43.22729;
		latLonEle[2] = 2.0;
		
		float[] psiThePhi = new float[3];
		psiThePhi[0] = heading;
		psiThePhi[1] = (float)0.0;
		psiThePhi[2] = (float)0.0;		
		
		xpc.sendOBJL( 66, latLonEle, psiThePhi, 0, 0 );
		
		xpc.close();
	}
	
	
	public static void main(String[] args) {
		
		try {
			//testSendOBJN();
			//testSendOBJL();
			testUpdateObject( (float)100);
			//testUpdateObject( (float)0.0 );
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
}
