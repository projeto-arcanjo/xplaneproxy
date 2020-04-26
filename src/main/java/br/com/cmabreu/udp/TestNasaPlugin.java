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
	
	
	private static void testSendOBJN() throws Exception {
		XPlaneConnect xpc = new XPlaneConnect( "127.0.0.1", 49009, 49007 );
		xpc.sendOBJN( 48, "Resources/default scenery/sim objects/apt_aircraft/fighter/F4/F4_static.obj" );
		xpc.close();
	}

	
	private static void test
	
	
	public static void main(String[] args) {
		
		try {
			testSendOBJN();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
}
