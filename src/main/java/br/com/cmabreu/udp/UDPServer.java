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
	private XPlaneDataProcessor processor;
	
    public UDPServer( int port ) {
    	this.port = port;
    	this.processor = new XPlaneDataProcessor();
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
	            this.processor.process(dtp);
        	} catch( Exception e ) {
        		e.printStackTrace();
        	}
        }
        
        logger.info("Servidor finalizado. ");
        socket.close();
    }  
    

	

		
  
}
