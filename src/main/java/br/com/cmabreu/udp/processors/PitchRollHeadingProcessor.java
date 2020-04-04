package br.com.cmabreu.udp.processors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.cmabreu.udp.XPlaneData;

public class PitchRollHeadingProcessor implements IPacketProcessor {
	private Logger logger = LoggerFactory.getLogger( PitchRollHeadingProcessor.class );

	@Override
	public long getIndex() {
		// 17 = Pitch, Roll, Heading
		return 17;
	}

	@Override
	public void processPacket( XPlaneData data ) {
		logger.info( data.toString() );
	}
	
}
