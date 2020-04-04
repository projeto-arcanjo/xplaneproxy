package br.com.cmabreu.udp.processors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.cmabreu.udp.XPlaneData;

public class MagnecticCompassProcessor implements IPacketProcessor {
	private Logger logger = LoggerFactory.getLogger( MagnecticCompassProcessor.class );

	@Override
	public long getIndex() {
		// 19 = Compass (Mag) 
		return 19;
	}

	@Override
	public void processPacket( XPlaneData data ) {
		logger.info( data.toString() );
	}

}
