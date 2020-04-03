package br.com.cmabreu.udp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UDPServer extends Thread {
    private DatagramSocket socket;
    private boolean running;
    private byte[] buffer = new byte[2048];
	private ByteBuffer byteBuffer;
	private Logger logger = LoggerFactory.getLogger( UDPServer.class );
	private int port;
	
	
    public UDPServer( int port ) {
    	this.port = port;
    	try {
    		socket = new DatagramSocket( port );
    	} catch ( Exception e ) {
    		e.printStackTrace();
    	}
    }  
    
    public void run() {
        running = true;
        DatagramPacket packet = new DatagramPacket( buffer, buffer.length );
        
        logger.info("Servidor iniciado na porta " + port);
        
        while (running) {
        	try {
	            
	            socket.receive( packet );
	            byteBuffer = ByteBuffer.wrap( buffer ).order( ByteOrder.LITTLE_ENDIAN );
				
	            XPlaneDataPacket dtp = new XPlaneDataPacket( packet.getAddress().getHostName(), byteBuffer.array() );
	            
	    		for( XPlaneData data : dtp.getData() ) {
	    			System.out.println( dtp.getHostName() + " | " + data.getIndex() + " | " + data.toString() );
	    		}		            
	            
	            
	            
				/*
				String lat = msg.substring(18, 26);
				String lng = msg.substring(26, 34);
				String lat2 = reverseHex(lat);
				String lng2 = reverseHex(lng);				
				*/
				
				// packet.getAddress().getHostName();
				//System.out.println("lat: "+ hexToFloat(lat2) +" - long: "+ hexToFloat(lng2) );				
				//System.out.println( msg );
	            
	            
	            
/*	            
				//System.out.println(msg2);			
				hexToFloat(lat2);
				hexToFloat(lng2);
				arrayTesteConv2 = CoordinateConversions.getXYZfromLatLonDegrees( hexToFloat(lat2),hexToFloat(lng2), 0.0);
				//arrayTesteConv2 = conversao.getXYZfromLatLonDegrees(-23.0946534902203,-45.108200517635815, 0.0); 
				worldLocation2 = new WorldLocationStruct(arrayTesteConv2[0],arrayTesteConv2[1],arrayTesteConv2[2]);
				System.out.println("Pos X: " + arrayTesteConv2[0] + "  Pos Y: "+ arrayTesteConv2[1]+ "  Pos Z: "+ arrayTesteConv2[2]);
				TESTE.updateMySpatial2(worldLocation2);				
				packet.setLength(buffer.length);	            
	            
*/	            
        	} catch( Exception e ) {
        		e.printStackTrace();
        	}
        }
        
        socket.close();
    }  
    

	

		
  
}
