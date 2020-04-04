package br.com.cmabreu.udp;

import org.springframework.beans.factory.annotation.Autowired;

import br.com.cmabreu.services.XPlaneAircraftManagerService;

public class XPlaneDataProcessor {
	// private Logger logger = LoggerFactory.getLogger( XPlaneDataProcessor.class );
	
    @Autowired
    XPlaneAircraftManagerService xplaneManager;

	public synchronized void process( XPlaneDataPacket dataPacket ) throws Exception {
		// Atualizo se ja existir ou crio uma nova.
		// A identificacao da aeronave vem do dataPacket.getHostName() 
		xplaneManager.update(dataPacket);
		
	}

}
