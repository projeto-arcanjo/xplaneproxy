package br.com.cmabreu.models;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.cmabreu.datatypes.EntityIdentifierStruct;
import br.com.cmabreu.datatypes.EntityIdentifierStructEncoder;
import br.com.cmabreu.datatypes.EntityTypeStruct;
import br.com.cmabreu.datatypes.EntityTypeStructEncoder;
import br.com.cmabreu.datatypes.ForceIdentifierEnumEncoder;
import br.com.cmabreu.datatypes.MarkingStruct;
import br.com.cmabreu.datatypes.MarkingStructEncoder;
import br.com.cmabreu.datatypes.OrientationStruct;
import br.com.cmabreu.datatypes.RPRboolean;
import br.com.cmabreu.datatypes.SpatialFPStruct;
import br.com.cmabreu.datatypes.SpatialFPStructEncoder;
import br.com.cmabreu.datatypes.VelocityVectorStruct;
import br.com.cmabreu.datatypes.WorldLocationStruct;
import br.com.cmabreu.misc.CoordinateConversions;
import br.com.cmabreu.services.XPlaneAircraftManagerService;
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
import hla.rti1516e.encoding.HLAoctet;

public class XPlaneAircraft {
	private ObjectInstanceHandle objectInstanceHandle;
	private XPlaneAircraftManagerService manager;
	private EncoderFactory encoderFactory;
	private EntityTypeStruct entityType;
	private Integer forceId;
	private EntityIdentifierStruct entityIdentifier;
	private int isConcealed;
	private MarkingStruct marking;
	private double latitude;
	private double longitude;
	private double altitude;
	private Logger logger = LoggerFactory.getLogger( XPlaneAircraft.class );
	private String identificador;
	
	
	public boolean isMe( String identificador ) {
		return identificador.equals( this.identificador );
	}
	
	public XPlaneAircraft( XPlaneAircraftManagerService manager, String identificador ) throws Exception {
		this.objectInstanceHandle = manager.getRtiAmb().registerObjectInstance( manager.getClassHandle() );
		this.encoderFactory = RtiFactoryFactory.getRtiFactory().getEncoderFactory(); 
		this.manager = manager;
		this.identificador = identificador;
		
		// Ajusta os valores default
		
		this.entityType = new EntityTypeStruct( 
				EntityKind.PLATFORM.value, 
				EntityDomain.AIR.value, 
				CountryType.BRAZIL.value,
				PlatformAir.ATTACK_HELICOPTER.value, 
				13, 
				3, 
				0);
		
		this.forceId = ForceID.NEUTRAL.value;
		this.entityIdentifier = new EntityIdentifierStruct( 3001, 101, 102 );
		this.isConcealed = 0;
        this.marking = new MarkingStruct(1,"TesteXplane");
        this.latitude = -23.0946534902203;
        this.longitude = -45.108200517635815;
        this.altitude = 7.0;
        
        // Envia ao RTI os valores default
        updateAllValues();
        logger.info("Nova aeronave '"+ identificador + "' pronta em " + latitude + "," + longitude + " " + altitude);
	}
	
	public void updateAllValues() throws Exception {
		AttributeHandleValueMap attributes = manager.getRtiAmb().getAttributeHandleValueMapFactory().create(6);

		
		// Encoda os atributos
		EntityTypeStructEncoder entityTypeStructEncoder = new EntityTypeStructEncoder( this.entityType );
        attributes.put( manager.getAttributeEntityType(), entityTypeStructEncoder.toByteArray());

        ForceIdentifierEnumEncoder forceIdEnumEncoder = new ForceIdentifierEnumEncoder( forceId );
        attributes.put( manager.getAttributeForceId(), forceIdEnumEncoder.toByteArray() );

        EntityIdentifierStructEncoder entityIdentifierStructEncoder = new EntityIdentifierStructEncoder( entityIdentifier );      
     	attributes.put( manager.getAttributeEntityId() , entityIdentifierStructEncoder.toByteArray() );
		
        // IsConcealed
        HLAoctet isConcealedOct = this.encoderFactory.createHLAoctet();           
        isConcealedOct.setValue( (byte) isConcealed );
        attributes.put( manager.getAttributeIsConcealed(), isConcealedOct.toByteArray() ); 
     	

        MarkingStructEncoder markingStructEncoder = new MarkingStructEncoder( marking );        
        attributes.put( manager.getAttributeMarking(), markingStructEncoder.toByteArray() );

        
        double[] arrayPosition = CoordinateConversions.getXYZfromLatLonDegrees( latitude, longitude, altitude );
        
        WorldLocationStruct worldLocation = new WorldLocationStruct( arrayPosition[0], arrayPosition[1], arrayPosition[2] );
        VelocityVectorStruct velocityVectorStruct = new VelocityVectorStruct(0.5415523,-0.5452158,-1.1100446);
        OrientationStruct orientationStruct = new OrientationStruct(-12.183228,-7.050103e+07,0.0);
        SpatialFPStruct spatialFPStruct = new SpatialFPStruct(worldLocation,RPRboolean.False,velocityVectorStruct,orientationStruct);
        SpatialFPStructEncoder spatialFPStructEncoder = new SpatialFPStructEncoder(spatialFPStruct);
        attributes.put( manager.getAttributeSpatial() , spatialFPStructEncoder.toByteArray());       
        
		
		this.manager.getRtiAmb().updateAttributeValues( objectInstanceHandle, attributes, null); 		
	}
	
	public void update( XPlaneDataPacket dataPacket ) {
		for( XPlaneData data : dataPacket.getData() ) {
			System.out.println("  > " + data.toString() );
		}		            
	}
	
	
}
