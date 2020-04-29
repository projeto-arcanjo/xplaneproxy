package br.com.cmabreu.threads;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.cmabreu.managers.XPlaneAircraftManager;
import br.com.cmabreu.udp.XPlaneDataPacket;

public class UDPServerThread implements Runnable {
    private DatagramSocket socket;
    private boolean running;
    private byte[] buffer = new byte[2048];
	private ByteBuffer byteBuffer;
	private Logger logger = LoggerFactory.getLogger( UDPServerThread.class );
	private int port;
	
	public void finish() {
		this.socket.close();
		this.running = false;
	}
	
    public UDPServerThread( int port ) {
    	this.port = port;
    	try {
    		socket = new DatagramSocket( port );
    	} catch ( Exception e ) {
    		e.printStackTrace();
    	}
    }  
    
    public void run() {
        this.running = true;
        DatagramPacket packet = new DatagramPacket( buffer, buffer.length );
        logger.info("Servidor iniciado na porta " + port);

        while ( this.running ) {
        	
        	try {
	            socket.receive( packet );

	            // Isso foi para testar a porta
	            //for(int i=0; i< buffer.length ; i++) { System.out.print(buffer[i] +" ");  }	            
	            
	            byteBuffer = ByteBuffer.wrap( buffer ).order( ByteOrder.LITTLE_ENDIAN );
	            XPlaneDataPacket dtp = new XPlaneDataPacket( packet.getAddress().getHostName(), byteBuffer.array() );
	            XPlaneAircraftManager.getInstance().update( dtp );
	            
        	} catch( SocketException se ) {
        		logger.error( se.getMessage() );
        	} catch( Exception e ) {
        		e.printStackTrace();
        	}
        	
        }
        
        logger.info("Servidor UDP finalizado.");
        
    }  
    

	

		
  
}
