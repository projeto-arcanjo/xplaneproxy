package br.com.cmabreu.datatypes;

import hla.rti1516e.RtiFactory;
import hla.rti1516e.RtiFactoryFactory;
import hla.rti1516e.encoding.EncoderFactory;
import hla.rti1516e.encoding.HLAoctet;

public class ForceIdentifierEnumEncoder {
	
	
	private EncoderFactory encoderFactory;
	
	{
		try {
			RtiFactory rtiFactory = RtiFactoryFactory.getRtiFactory();
			encoderFactory = rtiFactory.getEncoderFactory(); 
		}
		catch (Exception RTIinternalError) { 		
		}
	}	
	
	HLAoctet forceIdEncoder = encoderFactory.createHLAoctet();
	
	public ForceIdentifierEnumEncoder(ForceIdentifierEnum forceId) {		
		forceIdEncoder.setValue((byte) forceId.getForceId());     //3 eh pra ser uma variavel qualquer	associada a forceId	
	}	

    public byte[] toByteArray() {    	
    	return forceIdEncoder.toByteArray();
    }  

}
