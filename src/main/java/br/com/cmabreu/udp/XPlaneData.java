package br.com.cmabreu.udp;

import java.util.ArrayList;
import java.util.List;

public class XPlaneData {
	private String hexIndex;
	private Long index;
	private List<XPlaneValue> values;
	
	public XPlaneData(String hexIndex, Long index) {
		this.values = new ArrayList<XPlaneValue>();
		this.hexIndex = hexIndex;
		this.index = index;
	}

	public void put( XPlaneValue value) {
		values.add( value );
	}
	
	public String getHexIndex() {
		return hexIndex;
	}
	
	public Long getIndex() {
		return index;
	}
	
	public List<XPlaneValue> getValues() {
		return values;
	}
		
	public String toString() {
		StringBuilder sb = new StringBuilder();
		String separator = "";
		for( XPlaneValue value : getValues() ) {
			sb.append( separator + value.getValue() );
			separator = " | ";
		}
		return sb.toString();
	}

}
