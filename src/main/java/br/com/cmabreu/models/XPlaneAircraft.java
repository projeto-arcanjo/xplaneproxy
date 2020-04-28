package br.com.cmabreu.models;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.cmabreu.codec.Codec;
import br.com.cmabreu.codec.EntityIdentifier;
import br.com.cmabreu.codec.EntityType;
import br.com.cmabreu.codec.ForceIdentifier;
import br.com.cmabreu.codec.Marking;
import br.com.cmabreu.codec.SpatialVariant;
import br.com.cmabreu.misc.EncoderDecoder;
import br.com.cmabreu.misc.Environment;
import br.com.cmabreu.services.XPlaneAircraftManager;
import br.com.cmabreu.udp.XPlaneData;
import br.com.cmabreu.udp.XPlaneDataPacket;
import edu.nps.moves.disenum.CountryType;
import edu.nps.moves.disenum.DamageState;
import edu.nps.moves.disenum.EntityDomain;
import edu.nps.moves.disenum.EntityKind;
import edu.nps.moves.disenum.ForceID;
import edu.nps.moves.disenum.PlatformAir;
import hla.rti1516e.AttributeHandleValueMap;
import hla.rti1516e.ObjectInstanceHandle;
import hla.rti1516e.RtiFactoryFactory;
import hla.rti1516e.encoding.EncoderFactory;

public class XPlaneAircraft implements Serializable {
	private static final long serialVersionUID = 1L;
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
	private byte damageState;
	
	private float velocityX;
	private float velocityY;
	private float velocityZ;
	
	private float orientationPsi;
	private float orientationTheta;
	private float orientationPhi;
	
	
	private float latitude;
	private float longitude;
	private float altitude;
	private String identificador;
	private Logger logger = LoggerFactory.getLogger( XPlaneAircraft.class );
	
	public boolean isMyHandle( ObjectInstanceHandle objectInstanceHandle ) {
		return ( this.objectInstanceHandle.equals( objectInstanceHandle) );
	}
	
	public boolean isMe( String identificador ) {
		return identificador.equals( this.identificador );
	}
	
	
	/*
					NETN-FOM.pdf 
	*/
	
	public XPlaneAircraft( XPlaneAircraftManager manager, String identificador ) throws Exception {
		this.objectInstanceHandle = manager.getRtiAmb().registerObjectInstance( manager.getEntityHandle() );
		this.encoderFactory = RtiFactoryFactory.getRtiFactory().getEncoderFactory(); 
		this.manager = manager;
		this.identificador = identificador;
		this.env = new Environment();
		this.codec = new Codec( this.encoderFactory );
		
		// Seta as variaveis internas
		this.entityIdentifier = new EntityIdentifier( 3001, 101, 102 );
		
		/*
		 * 		Consultar o SISO-010-2019
		 * 		Item 5.13.15 (pág 113)
		 * 
		 * 		e https://github.com/open-dis/DISTutorial/wiki/Entity-Types
		 *  
		 */
		
		
		this.entityType = new EntityType(  
				(byte) EntityKind.PLATFORM.getValue(),
				(byte) EntityDomain.AIR.getValue(),
				(byte) CountryType.BRAZIL.getValue(),
				(byte) PlatformAir.ATTACK_HELICOPTER.getValue(),
				(byte) 13, 
				(byte) 3, 
				(byte) 0 
		);
		
		this.spatialVariant = new SpatialVariant();
		this.forceIdentifier = new ForceIdentifier( (byte)ForceID.NEUTRAL.value );
		this.marking = new Marking( this.identificador );
		this.latitude = (float)-23.0;
		this.longitude = (float)-45.0;
		this.altitude = (float)0.0;
		this.isConcealed = (byte)0;
		this.velocityX = (float) 0.0;
		this.velocityY = (float) 0.0;
		this.velocityZ = (float) 0.0;
		this.orientationPhi = (float)0.0;
		this.orientationTheta = (float)0.0;
		this.orientationPsi = (float)0.0;
		this.damageState = (byte)DamageState.NO_DAMAGE.getValue();
		
        updateAllValues();
        
        int handle = new EncoderDecoder().getObjectHandle(this.objectInstanceHandle  );
        
        logger.info("Nova aeronave '"+ identificador + "' [" + handle + "] pronta em " + latitude + "," + longitude + " " + altitude);
	}
	
	public void updateAllValues() throws Exception {
		
		// Posicao
		double[] geodetic = new double[3];
		geodetic[ Environment.LAT ] = this.latitude;
		geodetic[ Environment.LON ] = this.longitude;
		geodetic[ Environment.ALT ] = this.altitude;		
		
		double[] geocentric = env.getGeocentricLocation(geodetic);
		this.spatialVariant.setWorldLocation(geocentric[ SpatialVariant.X ], geocentric[ SpatialVariant.Y ], geocentric[ SpatialVariant.Z ]);
		this.spatialVariant.setOrientation( this.orientationPsi, this.orientationTheta, this.orientationPhi );
		this.spatialVariant.setFrozen(false);
		this.spatialVariant.setVelocityVector( this.velocityX, this.velocityY, this.velocityZ );
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
		
		// Damage State
		byte[] encodedDamageState = this.codec.encodeDamageState( this.damageState );
		
		// Cria o pacote de atributos
		
		AttributeHandleValueMap ahvm = manager.getRtiAmb().getAttributeHandleValueMapFactory().create( 7 );
		ahvm.put( manager.getSpatialHandle(), encodedSpatialVariant);		
		ahvm.put( manager.getIsConcealedHandle(), encodedConcealed );
		ahvm.put( manager.getForceIdentifierHandle(), encodedForceId );
		ahvm.put( manager.getMarkingHandle(), encodedMarking );
		ahvm.put( manager.getEntityIdentifierHandle(), encodedEntityIdentifier );
		ahvm.put( manager.getEntityTypeHandle(), encodedEntityType );
		ahvm.put( manager.getDamageStateHandle(), encodedDamageState );
		
		// ENVIA O UPDATE PARA A RTI
		manager.getRtiAmb().updateAttributeValues( this.objectInstanceHandle, ahvm, "XPLANE_ALL_INFO".getBytes() );
		logger.info("enviando atualizacao completa para a RTI");
		
	}
	
	public void update( XPlaneDataPacket dataPacket ) throws Exception{
		for( XPlaneData data : dataPacket.getData() ) {

			// 17 : Pitch, Roll, Heading
			if( data.getIndex() == 17 ) {
				updatePitchRollReading( data );
			}
			
			// 20 : LatLongAlt
			if( data.getIndex() == 20 ) {
				updateLatLongAlt( data );
			}
			
		}
		
		// Depois de atualizar todos os atributos eu 
		// disparo a posicao para a RTI de uma vez so
		sendSpatialVariant();
	}

	// Envia situacao de posicionamento do X-Plane para a RTI
	private void updatePitchRollReading( XPlaneData data ) {
		float pitch = data.getValues().get(0).getValue(); 
		float roll = data.getValues().get(1).getValue();
		float hTrue = data.getValues().get(2).getValue();
		// vou descartar o rumo magnetico porque o Cesium trabalha com o verdadeiro.
		// float hMagn = data.getValues().get(3).getValue();
		
		// https://mathworld.wolfram.com/EulerAngles.html
		// theta is pitch, psi is roll, and phi is yaw/heading.
		this.orientationPsi = roll;
		this.orientationTheta = pitch;		
		this.orientationPhi = hTrue;
	}
	
	// Envia posicao vinda do X-Plane para a RTI
	private void updateLatLongAlt( XPlaneData data ) {
		this.latitude = data.getValues().get(0).getValue();
		this.longitude = data.getValues().get(1).getValue();
		this.altitude = data.getValues().get(2).getValue();		
	}
	
	/*
	 * 		GETTERS E SETTERS
	 */
		

	public EntityType getEntityType() {
		return entityType;
	}

	public void setEntityType(EntityType entityType) {
		this.entityType = entityType;
	}

	public SpatialVariant getSpatialVariant() {
		return spatialVariant;
	}

	public void setSpatialVariant(SpatialVariant spatialVariant) {
		this.spatialVariant = spatialVariant;
	}

	public ForceIdentifier getForceIdentifier() {
		return forceIdentifier;
	}

	public void setForceIdentifier(ForceIdentifier forceIdentifier) {
		this.forceIdentifier = forceIdentifier;
	}

	public EntityIdentifier getEntityIdentifier() {
		return entityIdentifier;
	}

	public void setEntityIdentifier(EntityIdentifier entityIdentifier) {
		this.entityIdentifier = entityIdentifier;
	}

	public Marking getMarking() {
		return marking;
	}

	public void setMarking(Marking marking) {
		this.marking = marking;
	}

	public byte getIsConcealed() {
		return isConcealed;
	}

	public void setIsConcealed(byte isConcealed) {
		this.isConcealed = isConcealed;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(float latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(float longitude) {
		this.longitude = longitude;
	}

	public double getAltitude() {
		return altitude;
	}

	public void setAltitude(float altitude) {
		this.altitude = altitude;
	}

	public String getIdentificador() {
		return identificador;
	}

	public void setIdentificador(String identificador) {
		this.identificador = identificador;
	}

	public byte getDamageState() {
		return damageState;
	}
	
	public void setDamageState(byte damageState) {
		this.damageState = damageState;
	}

	public void sendSpatialVariant() throws Exception {
			
			double[] geodetic = new double[3];
			geodetic[ Environment.LAT ] = this.latitude;
			geodetic[ Environment.LON ] = this.longitude;
			geodetic[ Environment.ALT ] = this.altitude;		
			
			double[] geocentric = env.getGeocentricLocation(geodetic);
			this.spatialVariant.setWorldLocation(geocentric[ SpatialVariant.X ], geocentric[ SpatialVariant.Y ], geocentric[ SpatialVariant.Z ]);
			this.spatialVariant.setOrientation( this.orientationPsi, this.orientationTheta, this.orientationPhi );
			this.spatialVariant.setFrozen(false);
			this.spatialVariant.setVelocityVector( this.velocityX, this.velocityY, this.velocityZ );
			this.spatialVariant.setDiscriminator(SpatialVariant.DRM_FPW);		
			byte[] encodedSpatialVariant = this.codec.encodeSpatialVariant( this.spatialVariant );
			
			// Cria o pacote de atributos
			AttributeHandleValueMap ahvm = manager.getRtiAmb().getAttributeHandleValueMapFactory().create(1);
			ahvm.put( manager.getSpatialHandle(), encodedSpatialVariant);		
			
			// ENVIA O UPDATE PARA A RTI
			manager.getRtiAmb().updateAttributeValues( this.objectInstanceHandle, ahvm, null );
	}


	// So para cumprir o metodo que vem do controller para testes
	// Simula um pacote UDP de posicao (codigo 20) do X-Plane
	public void updateTest(float lat, float lon, float alt, float head, float pitch, float roll) {
		this.latitude = lat;
		this.longitude = lon;
		this.altitude = alt;
		this.orientationPsi = roll;
		this.orientationTheta = pitch;		
		this.orientationPhi = head;		
	}
	
		
}
