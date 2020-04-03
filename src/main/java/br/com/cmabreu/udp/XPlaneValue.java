package br.com.cmabreu.udp;

public class XPlaneValue {
	private Float value;
	private String hexValue;
	private String originalHexValue;

	public XPlaneValue(Float value, String hexValue, String originalHexValue) {
		this.value = value;
		this.hexValue = hexValue;
		this.originalHexValue = originalHexValue;
		
		//System.out.println("  > " + originalHexValue + "  [" + hexValue + "] " + value );
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

}
