package br.com.cmabreu.udp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPClient {
    private DatagramSocket socket;
    private InetAddress address;
    private int port;
    
    public UDPClient( String host, int port ) throws Exception {
    	this.port = port;
        this.socket = new DatagramSocket();
        this.address = InetAddress.getByName( host );    	
	}
    
    public void close() {
    	this.socket.close();
    }    
    
    public void sendData( byte[] buf ) throws Exception {
    	DatagramPacket packet = new DatagramPacket(buf, buf.length, address, this.port);
        socket.send(packet);
        socket.close();
    }    
    
}
