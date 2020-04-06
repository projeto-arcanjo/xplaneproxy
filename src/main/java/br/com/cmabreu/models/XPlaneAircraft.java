package br.com.cmabreu.models;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.cmabreu.codec.Codec;
import br.com.cmabreu.codec.EntityIdentifier;
import br.com.cmabreu.codec.EntityType;
import br.com.cmabreu.codec.ForceIdentifier;
import br.com.cmabreu.codec.Marking;
import br.com.cmabreu.codec.SpatialVariant;
import br.com.cmabreu.misc.Environment;
import br.com.cmabreu.services.XPlaneAircraftManager;
import br.com.cmabreu.udp.XPlaneData;
import br.com.cmabreu.udp.XPlaneDataPacket;
import edu.nps.moves.disenum.CountryType;
import edu.nps.moves.disenum.EntityDomain;
import edu.nps.moves.disenum.EntityKind;
import edu.nps.moves.disenum.ForceID;
import edu.nps.moves.disenum.PlatformAir;
import hla.rti1516e.AttributeHandleValueMap;
import hla.rti1516e.ObjectInstanceHandle;
import hla.rti1516e.RtiFactoryFactory;
import hla.rti1516e.encoding.EncoderFactory;

public class XPlaneAircraft {
	private ObjectInstanceHandle objectInstanceHandle;
	private XPlaneAircraftManager manager;
	private EncoderFactory encoderFactory;
	private Environment env;
	private Codec codec;
	
	private EntityType entityType;
	private SpatialVariant spatialVariant;
	private ForceIdentifier forceIdentifier;
	private EntityIdentifier entityIdentifier;
	private Marking marking;
	private byte isConcealed;
	
	private double latitude;
	private double longitude;
	private double altitude;
	private String identificador;
	private Logger logger = LoggerFactory.getLogger( XPlaneAircraft.class );
	
	
	public boolean isMe( String identificador ) {
		return identificador.equals( this.identificador );
	}
	
	public XPlaneAircraft( XPlaneAircraftManager manager, String identificador ) throws Exception {
		this.objectInstanceHandle = manager.getRtiAmb().registerObjectInstance( manager.getEntityHandle() );
		this.encoderFactory = RtiFactoryFactory.getRtiFactory().getEncoderFactory(); 
		this.manager = manager;
		this.identificador = identificador;
		this.env = new Environment();
		this.codec = new Codec( this.encoderFactory );
		
		// Seta as variaveis internas
		this.entityIdentifier = new EntityIdentifier( 3001, 101, 102 );
		
		this.entityType = new EntityType(  
				(byte) EntityKind.PLATFORM.value,
				(byte) EntityDomain.AIR.value,
				(byte) CountryType.BRAZIL.value,
				(byte) PlatformAir.ATTACK_HELICOPTER.value,
				(byte) 13, 
				(byte) 3, 
				(byte) 0 
		);
		
		this.spatialVariant = new SpatialVariant();
		this.forceIdentifier = new ForceIdentifier( (byte)ForceID.NEUTRAL.value );
		this.marking = new Marking( "TesteXplane" );
		this.latitude = -23.0946534902203;
		this.longitude = -45.108200517635815;
		this.altitude = 1001.0;
		this.isConcealed = (byte)0;
		
        updateAllValues();
        
        logger.info("Nova aeronave '"+ identificador + "' pronta em " + latitude + "," + longitude + " " + altitude);
	}
	
	public void updateAllValues() throws Exception {
		
		// Posicao
		double[] geodetic = new double[3];
		geodetic[ Environment.LAT ] = this.latitude;
		geodetic[ Environment.LON ] = this.longitude;
		geodetic[ Environment.ALT ] = this.altitude;		
		
		double[] geocentric = env.getGeocentricLocation(geodetic);
		this.spatialVariant.setWorldLocation(geocentric[ SpatialVariant.X ], geocentric[ SpatialVariant.Y ], geocentric[ SpatialVariant.Z ]);
		this.spatialVariant.setOrientation(0.0f, 0.0f, 0.0f);
		this.spatialVariant.setFrozen(false);
		this.spatialVariant.setVelocityVector(10.0f, 0.0f, 0.0f);
		this.spatialVariant.setDiscriminator(SpatialVariant.DRM_FPW);		
		byte[] encodedSpatialVariant = this.codec.encodeSpatialVariant( this.spatialVariant );
		
		// Force ID
		byte[] encodedForceId = this.codec.encodeForceIdentifier( this.forceIdentifier );
		
		// Concealed
        byte[] encodedConcealed = this.codec.encodeIsConcealed( this.isConcealed );
		
        // Marking
		byte[] encodedMarking = this.codec.encodeMarking( this.marking );
        
		// Entity Identifier
		byte[] encodedEntityIdentifier = this.codec.encodeEntityIdentifier( this.entityIdentifier );
		
		// Entity Type
		byte[] encodedEntityType = this.codec.encodeEntityType( this.entityType );
		
		// Cria o pacote de atributos
		// *********  FALTA OS OUTROS ***********
		AttributeHandleValueMap ahvm = manager.getRtiAmb().getAttributeHandleValueMapFactory().create( 6 );
		ahvm.put( manager.getSpatialHandle(), encodedSpatialVariant);		
		ahvm.put( manager.getIsConcealedHandle(), encodedConcealed );
		ahvm.put( manager.getForceIdentifierHandle(), encodedForceId );
		ahvm.put( manager.getMarkingHandle(), encodedMarking );
		ahvm.put( manager.getEntityIdentifierHandle(), encodedEntityIdentifier );
		ahvm.put( manager.getEntityTypeHandle(), encodedEntityType );
		
		
		// ENVIA O UPDATE PARA A RTI
		manager.getRtiAmb().updateAttributeValues( this.objectInstanceHandle, ahvm, null );
		
	}
	
	public void update( XPlaneDataPacket dataPacket ) {
		for( XPlaneData data : dataPacket.getData() ) {
			
			// 20 : LatLongAlt
			if( data.getIndex() == 20 ) {
				updateLatLongAlt( data );
			}
			
		}		            
	}

	
	// Envia posicao vinda do X-Plane para a RTI
	private void updateLatLongAlt( XPlaneData data ) {
		
		// Latitude
		this.latitude = data.getValues().get(0).getValue();
		// Longitude
		this.longitude = data.getValues().get(1).getValue();
		// Altitude
		this.altitude = data.getValues().get(3).getValue();
		
		System.out.println( latitude +  ", " + longitude + " || " + altitude );
		
		try {
			
			double[] geodetic = new double[3];
			geodetic[ Environment.LAT ] = this.latitude;
			geodetic[ Environment.LON ] = this.longitude;
			geodetic[ Environment.ALT ] = this.altitude;		
			
			double[] geocentric = env.getGeocentricLocation(geodetic);
			this.spatialVariant.setWorldLocation(geocentric[ SpatialVariant.X ], geocentric[ SpatialVariant.Y ], geocentric[ SpatialVariant.Z ]);
			this.spatialVariant.setOrientation(0.0f, 0.0f, 0.0f);
			this.spatialVariant.setFrozen(false);
			this.spatialVariant.setVelocityVector(10.0f, 0.0f, 0.0f);
			this.spatialVariant.setDiscriminator(SpatialVariant.DRM_FPW);		
			
			byte[] encodedSpatialVariant = this.codec.encodeSpatialVariant( this.spatialVariant );
			
			// Cria o pacote de atributos
			AttributeHandleValueMap ahvm = manager.getRtiAmb().getAttributeHandleValueMapFactory().create(1);
			ahvm.put( manager.getSpatialHandle(), encodedSpatialVariant);		
			
			// ENVIA O UPDATE PARA A RTI
			manager.getRtiAmb().updateAttributeValues( this.objectInstanceHandle, ahvm, null );
			
		} catch ( Exception e ) {
			logger.error("Erro ao atualizar posicao: " + e.getMessage() );
		}
	}
	
	
	
}
