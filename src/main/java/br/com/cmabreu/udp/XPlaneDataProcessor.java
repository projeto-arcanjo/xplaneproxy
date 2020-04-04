package br.com.cmabreu.udp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.cmabreu.udp.processors.PacketProcessor;

public class XPlaneDataProcessor {
	private PacketProcessor pp;
	private Logger logger = LoggerFactory.getLogger( XPlaneDataProcessor.class );
	
	public XPlaneDataProcessor() {
		this.pp = new PacketProcessor();
	}
	
	public synchronized void process( XPlaneDataPacket dataPacket ) {
		logger.info( dataPacket.getHostName() );
		
		for( XPlaneData data : dataPacket.getData() ) {
			pp.processPacket( data );
		}		            
		
	}

}
