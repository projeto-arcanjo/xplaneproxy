package br.com.cmabreu.udp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class XPlaneDataPacket {
	private String hostName;
	private List<XPlaneData> data;
	private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
	
	public XPlaneDataPacket(String hostName, byte[] bytes ) {
		String msg = new String( bytesToHex( bytes ) );
		this.data = new ArrayList<XPlaneData>();
		this.hostName = hostName;
		processMessage( msg );
	}

	public String getHostName() {
		return hostName;
	}

	public List<XPlaneData> getData() {
		return data;
	} 
	
	
	private List<String> splitAfterNChars(String input, int splitLen){
	    String[] temp =  input.split( String.format("(?<=\\G.{%1$d})", splitLen) );
	    return new ArrayList<String>( Arrays.asList(temp) );
	}

	public void processMessage( String msg ) {
		int packetHexaSize = 36 * 2;
		//msg = "444154412A140000006E53B7C124A72CC2964B54403AC9883E0000803F0E4B54400000B4C100002CC214000000D2BC3D42749CF4C203CFA9434DE28B3E0000803FF677A94300003E420000F4C214000000D2BC3D42749CF4C203CFA9434DE28B3E0000803FF677A94300003E420000F4C214000000D2BC3D42749CF4C203CFA9434DE28B3E0000803FF677A94300003E420000F4C2";
		msg = msg.substring( 5 * 2 ); // remove 5 primeiros bytes em hexa
		
		List<String> dataPackets = splitAfterNChars( msg, packetHexaSize ); // decompoe a string em pacotes de 36 bytes
		for( String packet : dataPackets) {
			try {
				String packetIndex = packet.substring(0, 8); // identifica o indice do dado no X-Plane
				String packetData = packet.substring(8, packetHexaSize );
				Long index = Long.parseLong( reverseHex( packetIndex ), 16);
				
				if( index != 0 ) {
					XPlaneData dta = new XPlaneData( reverseHex( packetIndex ), index );
					List<String> packetDataBytes = splitAfterNChars( packetData, 8); // decompoe o dado em pacotes de 4 bytes
					for( String dataBytes : packetDataBytes ) {
						XPlaneValue val = new XPlaneValue( hexToFloat( reverseHex( dataBytes ) ), reverseHex( dataBytes ), dataBytes );
						dta.put( val );
					}
					this.data.add( dta );
				}
				
			} catch( Exception e) {
				// ignore
			}
			
		}
		
		
	}
	
	private String reverseHex(String originalHex) {
		int lengthInBytes = originalHex.length() / 2;
		char[] chars = new char[lengthInBytes*2];
		for (int index = 0 ; index < lengthInBytes; index++) {
			int reversedIndex = lengthInBytes -1 - index;
			chars[reversedIndex*2] = originalHex.charAt(index*2);
			chars[reversedIndex*2+1] = originalHex.charAt(index*2+1);
		}
		return new String(chars);
	}
	
	private String bytesToHex(byte[] bytes) {
		char[] hexChars = new char[bytes.length*2];
		for (int j = 0 ; j < bytes.length; j++) {
			int v = bytes[j] & 0xFF;
			hexChars[j*2]= HEX_ARRAY[v >>> 4];
			hexChars[j*2 + 1]= HEX_ARRAY[v & 0x0F];
		}
		return new String(hexChars);
	}
	
	
	private float hexToFloat(String myString) {
		Long i = Long.parseLong(myString,16);
		Float f = Float.intBitsToFloat(i.intValue());
		return f;
	}    
    
	

}
