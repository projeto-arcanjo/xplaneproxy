package br.com.cmabreu.udp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPClient {
    private DatagramSocket socket;
    private InetAddress address;
    private int port;
    private byte[] buf;
    
    public UDPClient( String host, int port ) throws Exception {
    	this.port = port;
        this.socket = new DatagramSocket();
        this.address = InetAddress.getByName( host );    	
	}
    
    public void close() {
    	this.socket.close();
    }    
    
    public void sendEcho( String msg ) throws Exception {
        buf = msg.getBytes();
        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, this.port);
        socket.send(packet);
        //packet = new DatagramPacket(buf, buf.length);
        //socket.receive(packet);
        //String received = new String( packet.getData(), 0, packet.getLength() );
        //return received;
    }    
    
}
