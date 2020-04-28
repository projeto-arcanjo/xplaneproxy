package br.com.cmabreu.services;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import br.com.cmabreu.udp.AircraftData;
import br.com.cmabreu.udp.ObjectData;
import gov.nasa.xpc.XPlaneConnect;

@Service
@EnableScheduling
public class ObjectService {
	private XPlaneConnect xpc; 
	private Logger logger = LoggerFactory.getLogger( ObjectService.class );
	private List<ObjectData> objects;
	
    @Value("${xplane.address}")
    String xplaneAddress;		
	
    @Scheduled(fixedRate = 1000)
    public void updateObjects() {
    	logger.info("atualizando " + this.objects.size() + " objetos");
		for( ObjectData object : objects ) {
			try {
				if( object.isActive() ) this.updateObject(object);
			} catch ( Exception e ) {
				logger.error( e.getMessage() );
			}
		}
    }
    
	public ObjectService() {
		try {
			xpc = new XPlaneConnect( xplaneAddress, 49009, 49007 );
			this.objects = new ArrayList<ObjectData>();
			logger.info("enviando dados para X-Plane no endereco ");
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}

	public ObjectData getObject( long index ) {
		for( ObjectData object : objects ) {
			if( object.getIndex() == index ) return object;
		}
		return null;
	}
	
	public void sendText( String texto ) throws Exception {
        xpc.sendTEXT( texto, 200, 400 );
	}	
	

	public AircraftData getAircraftData( int aircraftNumber ) throws Exception {
    	double[] posi = xpc.getPOSI( aircraftNumber );
    	float[]  ctrl = xpc.getCTRL( aircraftNumber );
    	return new AircraftData( posi, ctrl );
	}
	
	public void loadObject( int index, String objectPath ) throws Exception {
		xpc.sendOBJN( index, objectPath );
	}
	
	public void updateObject( ObjectData object ) throws Exception {
		xpc.sendOBJL( object.getIndex(), object.getLatLonEle(), object.getPsiThePhi(), object.getGround(), object.getSmoke() );
	}
	
	public ObjectData spawn( double lat, double lon, double ele, float psi, float the, float phi, long ground, float smoke, String objectPath ) throws Exception {
		int index = this.objects.size() + 1;
		ObjectData object = new ObjectData( index, lat, lon, ele, psi, the, phi, ground, smoke, objectPath );
		this.objects.add( object );
		this.loadObject( index, objectPath );
		this.updateObject( object );
		return object;
	}

	
	public void setHeading( int index, float psi ) {
		getObject(index).setPsi( psi );
	}
	
}
