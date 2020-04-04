package br.com.cmabreu.udp.processors;

import java.util.ArrayList;
import java.util.List;

import br.com.cmabreu.udp.XPlaneData;

public class PacketProcessor {
	private List<IPacketProcessor> pktprocs;
	
	public PacketProcessor() {
		this.pktprocs = new ArrayList<IPacketProcessor>();
		
		// Adiciona processadores especificos para cada tipo de mensagem enviada pelo X-Plane
		this.pktprocs.add( new LatLongAltPacketProcessor() );
		this.pktprocs.add( new PitchRollHeadingProcessor() );
		this.pktprocs.add( new MagnecticCompassProcessor() );
		
	}

	
	
	public void processPacket( XPlaneData data ) {
		for( IPacketProcessor pktproc : pktprocs ) {
			
			if( pktproc.getIndex() == data.getIndex() ) {
				pktproc.processPacket(data);
			}
			
		}
	}
	
}
