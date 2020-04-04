package br.com.cmabreu.udp.processors;

import br.com.cmabreu.udp.XPlaneData;

public interface IPacketProcessor {
	long getIndex();
	void processPacket( XPlaneData data );
}
