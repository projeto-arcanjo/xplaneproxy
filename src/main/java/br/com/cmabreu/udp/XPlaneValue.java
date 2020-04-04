package br.com.cmabreu.udp;

public class XPlaneValue {
	private Float value;
	private String hexValue;
	private String originalHexValue;

	public XPlaneValue( String originalHexValue ) {
		this.hexValue = reverseHex( originalHexValue );
		this.originalHexValue = originalHexValue;
		this.value = convertValue();
	}

	private Float convertValue() {
		return hexToFloat( this.hexValue );
	}
	
	public Float getValue() {
		return value;
	}
	
	public String getHexValue() {
		return hexValue;
	}
	
	public String getOriginalHexValue() {
		return originalHexValue;
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

	/*
	private String bytesToHex(byte[] bytes) {
		char[] hexChars = new char[bytes.length*2];
		for (int j = 0 ; j < bytes.length; j++) {
			int v = bytes[j] & 0xFF;
			hexChars[j*2]= HEX_ARRAY[v >>> 4];
			hexChars[j*2 + 1]= HEX_ARRAY[v & 0x0F];
		}
		return new String(hexChars);
	}
	*/
	
	private float hexToFloat(String myString) {
		Long i = Long.parseLong(myString,16);
		Float f = Float.intBitsToFloat(i.intValue());
		return f;
	}    
    
		

}
