package br.com.cmabreu.datatypes;

import hla.rti1516e.RtiFactory;
import hla.rti1516e.RtiFactoryFactory;
import hla.rti1516e.encoding.EncoderFactory;
import hla.rti1516e.encoding.HLAoctet;

public class ForceIdentifierEnumEncoder {
	private EncoderFactory encoderFactory;
	private HLAoctet forceIdEncoder = encoderFactory.createHLAoctet();
	
	public ForceIdentifierEnumEncoder() {
		try {
			RtiFactory rtiFactory = RtiFactoryFactory.getRtiFactory();
			encoderFactory = rtiFactory.getEncoderFactory(); 
		} catch (Exception RTIinternalError) {
			//
		}
	}	
	
	
	public ForceIdentifierEnumEncoder( int forceId) {		
		forceIdEncoder.setValue( (byte)forceId );     //3 eh pra ser uma variavel qualquer	associada a forceId	
	}	

    public byte[] toByteArray() {    	
    	return forceIdEncoder.toByteArray();
    }  

}
