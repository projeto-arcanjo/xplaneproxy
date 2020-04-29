package br.com.cmabreu.services;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import br.com.cmabreu.managers.IManager;
import br.com.cmabreu.models.XPlaneObject;
import br.com.cmabreu.models.XPlaneObjectDTO;
import br.com.cmabreu.udp.AircraftData;
import gov.nasa.xpc.XPlaneConnect;
import hla.rti1516e.AttributeHandleValueMap;
import hla.rti1516e.ObjectInstanceHandle;

@Service
@EnableScheduling
public class ObjectService {
	private XPlaneConnect xpc; 
	private Logger logger = LoggerFactory.getLogger( ObjectService.class );
	private List<XPlaneObject> objects;
	
    @Value("${xplane.address}")
    String xplaneAddress;		
	
    @Scheduled(fixedRate = 1000)
    private void updateObjectsInXPlane() {
    	if( objects.size() > 0 ) {
			for( XPlaneObject object : objects ) {
				try {
					if( object.isActive() ) this.updateObject( object );
				} catch ( Exception e ) {
					logger.error( e.getMessage() );
				}
			}
    	}
    }
    
    @PostConstruct
	public void initializer() {
		try {
			xpc = new XPlaneConnect( xplaneAddress, 49009, 49007 );
			this.objects = new ArrayList<XPlaneObject>();
			logger.info("enviando dados para X-Plane no endereco " + xplaneAddress );
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}

	public XPlaneObject getObject( long index ) {
		for( XPlaneObject object : objects ) {
			if( object.getIndex() == index ) return object;
		}
		return null;
	}
	
	public XPlaneObjectDTO getObjectDTO( long index ) {
		for( XPlaneObject object : objects ) {
			if( object.getIndex() == index ) return new XPlaneObjectDTO( object );
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
	
	private synchronized void updateObject( XPlaneObject object ) throws Exception {
		xpc.sendOBJL( object.getIndex(), object.getLatLonEle(), object.getPsiThePhi(), object.getGround(), object.getSmoke() );
	}
	
	public XPlaneObjectDTO spawn( double lat, double lon, double ele, float psi, float the, float phi, int ground, float smoke, String objectPath, String objectName ) throws Exception {
		int index = this.objects.size() + 1;
		XPlaneObject object = new XPlaneObject( index, lat, lon, ele, psi, the, phi, ground, smoke, objectPath, objectName );
		this.objects.add( object );
		this.loadObject( index, objectPath );
		this.updateObject( object );
		return new XPlaneObjectDTO( object );
	}

	// Prepara uma instancia inicial de um objeto
	// mas nao avisa ao X-Plane ainda. So guarda na lista com o handle da RTI.
	// esse metodo veio de uma notificacao de novo objeto encontrado na RTI
	// mas eu ainda nao tenho seus atributos.
	// quando recebo um discoverObjectInstance preciso pedir os atributos atualizados e
	// esperar a RTI ( o dono do objeto ) responder antes de plotar no X-Plane
	public synchronized XPlaneObject prepare( ObjectInstanceHandle instanceHandle, String objectPath, String className, IManager manager, String objectName ) throws Exception {
		int index = this.objects.size() + 1;
		XPlaneObject object = new XPlaneObject( index, objectPath, instanceHandle, className, manager, objectName );
		this.objects.add( object );
		return object;
	}
	
	// Atualiza os atributos deste objeto baseado nos dados que 
	// chegaram da RTI. O dono do objeto enviou estas atualizacoes
	// provavelmente respondendo à um provideAttributeValueUpdate
	// Este federado trata tudo como Objeto. A diferenca foi feita na
	// criacao, quando recebeu um discoverObjectInstance da RTI
	public XPlaneObject updateAttributes( ObjectInstanceHandle instanceHandle, AttributeHandleValueMap theAttributes ) throws Exception {

		for( XPlaneObject object : objects ) {
			
			// object.getInstanceHandle() vem null quando alguem deu spawn pela interface
			// para testar o REST -- isso eh um caso raro e devera ser removido no futuro
			if( ( object.getInstanceHandle() != null ) && object.getInstanceHandle().equals( instanceHandle )  ) {
				object.updateAttributes( theAttributes );
				
				if( !object.isActive() ) {
					xpc.sendOBJN( object.getIndex(), object.getObjectPath() );
					object.activate();
					logger.info("ativando " + object.getIndex() + " : " + object.getObjectName() );
				}
				
				return object;
			}
			
		}
		
		return null;
	}

	public void setHeading( int index, float psi ) {
		getObject(index).setPsi( psi );
	}

	public void setSmoke(Integer index, Float amount) {
		getObject(index).setSmoke( amount );
	}

	
}
