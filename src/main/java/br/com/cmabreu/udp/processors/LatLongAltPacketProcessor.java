package br.com.cmabreu.udp.processors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/*	
__lat,__deg |   __lon,__deg |   __alt,ftmsl |   __alt,ftagl |   ___on,runwy |   __alt,__ind |   __lat,south |   __lon,_west | 
------------|---------------|---------------|---------------|---------------|---------------|---------------|---------------|
   47.50037 |    -122.21684 |       4.87364 |       0.35146 |       1.00000 |       4.87439 |      46.00000 |    -124.00000 | 	
*/	



import br.com.cmabreu.udp.XPlaneData;

public class LatLongAltPacketProcessor implements IPacketProcessor {
	private Logger logger = LoggerFactory.getLogger( LatLongAltPacketProcessor.class );

	@Override
	public long getIndex() {
		// Index 20 = LatLongAlt
		return 20;
	}

	@Override
	public void processPacket( XPlaneData data ) {
		logger.info( data.toString() );
	}
	
	
}
