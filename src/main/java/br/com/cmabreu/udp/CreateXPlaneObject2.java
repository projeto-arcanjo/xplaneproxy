package br.com.cmabreu.udp;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

public class CreateXPlaneObject2 {

	private static void sendObjn( int index, String objectPath ) throws Exception {
	    //Convert data to bytes
		ByteBuffer bb = ByteBuffer.allocate( 504 ); 
		bb.order(ByteOrder.LITTLE_ENDIAN);
		bb.putInt( index );
		bb.put( objectPath.getBytes( StandardCharsets.UTF_8 ) );
		
	    //Build and send message
	    ByteArrayOutputStream os = new ByteArrayOutputStream();
	    os.write( "OBJN".getBytes(StandardCharsets.UTF_8) );
	    os.write( 0xFF ); //Placeholder for message length
	    os.write(bb.array());
	    sendData(os.toByteArray());
	}

	
	private static void sendObjl( int index) throws Exception {
	    //Convert data to bytes
		ByteBuffer bb = ByteBuffer.allocate( 56 ); 
		bb.order(ByteOrder.LITTLE_ENDIAN);
		
		bb.putLong( index );

		double lat = -22.80103;
		double lon = -43.22729;
		double ele = 0.0;
		
		float psi = (float)0.0;
		float the = (float)0.0;
		float phi = (float)0.0;
		
		float smoke = (float)5.0;		
		
		bb.putDouble( lat );
		bb.putDouble( lon );
		bb.putDouble( ele );
		bb.putFloat( psi );
		bb.putFloat( the );
		bb.putFloat( phi );
		bb.putLong( 0 );			
		bb.putFloat( smoke );	
		
	    //Build and send message
	    ByteArrayOutputStream os = new ByteArrayOutputStream();
	    os.write( "OBJL".getBytes(StandardCharsets.UTF_8) );
	    os.write( 0xFF ); //Placeholder for message length
	    os.write( bb.array() );
	    sendData( os.toByteArray() );
	    
	}
	
	
	public static void main(String[] args) {
		try {
			//sendObjn( 4, "Resources/default scenery/sim objects/apt_aircraft/fighter/F4/F4_static.obj" );
			sendObjl( 4 );
			
			
			/*
			double[] latLonEle = new double[3];
			latLonEle[0] = -22.80103;
			latLonEle[1] = -43.22729;
			latLonEle[2] = 5.0;
			
			float[] psiThePhi = new float[3];
			psiThePhi[0] = (float)0.0;
			psiThePhi[1] = (float)0.0;
			psiThePhi[2] = (float)0.0;
			
			TesteOBJLStruct objl = new TesteOBJLStruct( 46, latLonEle, psiThePhi, 5, (float)0.0 );
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(bos);
			oos.writeObject( objl );
			oos.flush();
			byte[] data = bos.toByteArray();			
			
			System.out.println( Arrays.toString( latLonEle ) ); 
			*/
			
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}
		
	
	private static void sendData( byte[] data ) {
		try {
			UDPClient client = new UDPClient("192.168.0.76", 49000);
			client.sendData( data );
		} catch ( Exception e) {
			e.printStackTrace();
		}
	}
	
	
}
