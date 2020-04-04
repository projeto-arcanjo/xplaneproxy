package br.com.cmabreu.models;



import java.util.HashMap;
import java.util.Map;

import br.com.cmabreu.datatypes.EntityIdentifierStruct;
import br.com.cmabreu.datatypes.EntityIdentifierStructEncoder;
import br.com.cmabreu.datatypes.EntityTypeStruct;
import br.com.cmabreu.datatypes.EntityTypeStructEncoder;
import br.com.cmabreu.datatypes.ForceIdentifierEnum;
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
import hla.rti1516e.AttributeHandle;
import hla.rti1516e.AttributeHandleSet;
import hla.rti1516e.AttributeHandleValueMap;
import hla.rti1516e.InteractionClassHandle;
import hla.rti1516e.ObjectClassHandle;
import hla.rti1516e.ObjectInstanceHandle;
import hla.rti1516e.RTIambassador;
import hla.rti1516e.RtiFactory;
import hla.rti1516e.RtiFactoryFactory;
import hla.rti1516e.encoding.EncoderFactory;
import hla.rti1516e.encoding.HLAoctet;
import hla.rti1516e.exceptions.AttributeNotDefined;
import hla.rti1516e.exceptions.AttributeNotOwned;
import hla.rti1516e.exceptions.FederateNotExecutionMember;
import hla.rti1516e.exceptions.NotConnected;
import hla.rti1516e.exceptions.ObjectInstanceNotKnown;
import hla.rti1516e.exceptions.RTIinternalError;
import hla.rti1516e.exceptions.RestoreInProgress;
import hla.rti1516e.exceptions.SaveInProgress;


public class Aircraft {
	   private RTIambassador _rtiAmbassador;
	   private InteractionClassHandle interactionHandle;   
	   private ObjectInstanceHandle _userId;
	   private AttributeHandle _attributeEntityType;
	   private AttributeHandle _attributeSpatial;   
	   private AttributeHandle _attributeEntityId; 
	   private AttributeHandle _attributeDamageState; 
	   private AttributeHandle _attributeForceId; 
	   private AttributeHandle _attributeIsConcealed; 
	   private AttributeHandle _attributeMarking; 
	   private final Map _knownObjects = new HashMap();
	   AttributeHandleValueMap attributes;
	   SpatialFPStructEncoder spatialFPStructEncoder;
	   protected boolean isRegulating;
	   protected double federateTime = 0.0;
	   protected double federateLookahead = 1.0;
	   double valor_teste = 4114673.3659611;
	   
	   CoordinateConversions conversao = new CoordinateConversions();
	   
	   static double teste1 = 2000;
	   static double teste2;

	   //private static final int CRC_PORT = 8989;
	   private EncoderFactory _encoderFactory;
	   
	   //Classe Participant que possui um nome	  
	public Aircraft(RTIambassador _rtiAmb) {
		_rtiAmbassador = _rtiAmb;
	      try {
	          try {
	        	  RtiFactory rtiFactory = RtiFactoryFactory.getRtiFactory();
	             _encoderFactory = rtiFactory.getEncoderFactory();    	             
	          } catch (Exception e) {
	             System.out.println("Unable to create RTI ambassador.");
	             return;
	          }        
	          // SUBSCRIBE AND PUBLISH OBJECTS
	          // IMPLEMENTACAO DA AERONAVE         
	          // Paramatro retirado do FOM
	          ObjectClassHandle participantId = _rtiAmbassador.getObjectClassHandle("BaseEntity.PhysicalEntity.Platform.Aircraft");                   
	          //Atributos da entidade criada. Esses campos foram retirados do FOM base, nesse caso o netn2_2010.xml
	          _attributeEntityType = _rtiAmbassador.getAttributeHandle(participantId, "EntityType");        
	          _attributeEntityId = _rtiAmbassador.getAttributeHandle(participantId, "EntityIdentifier");  
	          _attributeSpatial = _rtiAmbassador.getAttributeHandle(participantId, "Spatial");  
	          _attributeDamageState = _rtiAmbassador.getAttributeHandle(participantId, "DamageState");   
	          _attributeForceId = _rtiAmbassador.getAttributeHandle(participantId, "ForceIdentifier");  
	          _attributeIsConcealed = _rtiAmbassador.getAttributeHandle(participantId, "IsConcealed");   
	          _attributeMarking = _rtiAmbassador.getAttributeHandle(participantId, "Marking");  	                   
	          //criando o attributehandle set dos atributos
	          AttributeHandleSet attributeSet = _rtiAmbassador.getAttributeHandleSetFactory().create();
	          attributeSet.add(_attributeEntityType);
	          attributeSet.add(_attributeSpatial);
	          attributeSet.add(_attributeEntityId);
	          attributeSet.add(_attributeDamageState);
	          attributeSet.add(_attributeForceId);
	          attributeSet.add(_attributeIsConcealed);
	          attributeSet.add(_attributeMarking);
	          
	          //TENTATIVA DE CODAR POSICAO        
	          // Posicao Inicial antes do Update 7.291122019556398e-304,4114673.3659611,-4150319.2556862
	          // Posicao teste  4129080.813, -4177197.526, 2478095.458 Nao funcionou
	          // outro teste 4112437.52278387, -4192930.90535051, -2479448.27855349 tambem nao funcionou
	          // 958506.1, 455637.2,4344627.4  NAO FUNCIONOU 
	          double[] arrayTesteConv = conversao.getXYZfromLatLonDegrees(-23.0946534902203,-45.108200517635815, 7.0);   
	          
	          WorldLocationStruct worldLocation = new WorldLocationStruct(arrayTesteConv[0],arrayTesteConv[1],arrayTesteConv[2]);         
	          VelocityVectorStruct velocityVectorStruct = new VelocityVectorStruct(0.5415523,-0.5452158,-1.1100446);
	          OrientationStruct orientationStruct = new OrientationStruct(-12.183228,-7.050103e+07,0.0);
	          SpatialFPStruct spatialFPStruct = new SpatialFPStruct(worldLocation,RPRboolean.False,velocityVectorStruct,orientationStruct);
	          spatialFPStructEncoder = new SpatialFPStructEncoder(spatialFPStruct);             
	          /////////////////// Entity Type
	 		     ////010200ff 14010000
	 		     EntityTypeStruct entityTypeStruct = new EntityTypeStruct(1,2,29,20,13,3,0);
	 		     EntityTypeStructEncoder entityTypeStructEncoder = new EntityTypeStructEncoder(entityTypeStruct);
	          ///////////// Force Identifier         
	          	 ForceIdentifierEnum forceIdEnum = ForceIdentifierEnum.NEUTRAL;
	          	 ForceIdentifierEnumEncoder forceIdEnumEncoder = new ForceIdentifierEnumEncoder(0);
	          /////////////// Entity Identifier                  	 
	          	 EntityIdentifierStruct entityIdentifierStruct = new EntityIdentifierStruct(3001,101,102);
	          	 EntityIdentifierStructEncoder entityIdentifierStructEncoder = new EntityIdentifierStructEncoder(entityIdentifierStruct);
	 	       //  0bb90065	         
	          // IsConcealed
	          HLAoctet isConcealed = _encoderFactory.createHLAoctet();           
	          isConcealed.setValue((byte) 0);         
	          // Marking gambiarra
	          // Marking, [12:0158506c 616e6500 00000000]
	          MarkingStruct markingStruct = new MarkingStruct(1,"TesteXplane");
	          MarkingStructEncoder markingStructEncoder = new MarkingStructEncoder(markingStruct);
	          // Criando o set de attributos
	          attributes = _rtiAmbassador.getAttributeHandleValueMapFactory().create(6);	          
	          attributes.put(_attributeEntityType , entityTypeStructEncoder.toByteArray());
	          attributes.put(_attributeSpatial , spatialFPStructEncoder.toByteArray());       
	          attributes.put(_attributeForceId, forceIdEnumEncoder.toByteArray());
	          attributes.put(_attributeEntityId , entityIdentifierStructEncoder.toByteArray());
	          attributes.put(_attributeIsConcealed, isConcealed.toByteArray());  
	          attributes.put(_attributeMarking, markingStructEncoder.toByteArray());        	        
	          // dando -h e subscribe em tds os atributos         
	          _rtiAmbassador.subscribeObjectClassAttributes(participantId, attributeSet);
	          _rtiAmbassador.publishObjectClassAttributes(participantId, attributeSet);               
	          interactionHandle =  _rtiAmbassador.getInteractionClassHandle("Acknowledge");
	          _rtiAmbassador.publishInteractionClass(interactionHandle);
	          _rtiAmbassador.subscribeInteractionClass(interactionHandle);     	          
          // Registra uma instancia do federado no RTI, recebe um numero de instancia	          
	          	_userId = _rtiAmbassador.registerObjectInstance(participantId);   
	        // _rtiAmbassador.updateAttributeValues( _userId, attributes, null );            	         
	         	 _rtiAmbassador.updateAttributeValues( _userId, attributes, null);  
	 	         _rtiAmbassador.evokeMultipleCallbacks(0.0, 0.1); 	  	     
	          // Esse loop mantem o Federado conectado, se eliminado o codigo pula direto pra resignFedExecution
	          //Quita
	         	    } catch (Exception e) {
	          e.printStackTrace();
	       }
	   
	}
	
	
	   public final void provideAttributeValueUpdate(ObjectInstanceHandle theObject,
               AttributeHandleSet theAttributes,
               byte[] userSuppliedTag)
			{
			if (theObject.equals( _userId ) && theAttributes.contains(_attributeSpatial)) {
			new Thread() {
			public void run()
			{
			updateMySpatial();
			}
				}.start();
			}
			}
			
			private void updateMySpatial()
			{
			
			}
	
	   public void updateMySpatial2(WorldLocationStruct worldLocation, AttributeHandleValueMap attributes) {	   
	 		System.out.println("Atualizando pos dentro de updateMySpatial2");     	        	 		
		    
		    double[] arrayTesteConv = conversao.getXYZfromLatLonDegrees(-23.0946534902203 ,-45.108200517635815 , 0.0);        		    
		    worldLocation.setWorldLocationY(arrayTesteConv[0]);
		    worldLocation.setWorldLocationZ(arrayTesteConv[1]);	        		    
		    VelocityVectorStruct velocityVectorStruct = new VelocityVectorStruct(0.5415523,-0.5452158,-1.1100446);
		    OrientationStruct orientationStruct = new OrientationStruct(-12.183228,-7.050103e+07,0.0);
		    SpatialFPStruct spatialFPStruct = new SpatialFPStruct(worldLocation,RPRboolean.False,velocityVectorStruct,orientationStruct);
		    SpatialFPStructEncoder spatialFPStructEncoder = new SpatialFPStructEncoder(spatialFPStruct);  
	        attributes.put(_attributeSpatial, spatialFPStructEncoder.toByteArray());        	 	         
	   }
	   
	   
	   public void updateMySpatial2(WorldLocationStruct worldLocation) throws AttributeNotOwned, AttributeNotDefined, ObjectInstanceNotKnown, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {	   
	 		System.out.println("Atualizando pos dentro de updateMySpatial2___");	
	 		
		    double[] arrayTesteConv = conversao.getXYZfromLatLonDegrees(worldLocation.getWorldLocationX(),worldLocation.getWorldLocationY(), worldLocation.getWorldLocationZ());    
		    
		    worldLocation.setWorldLocationY(worldLocation.getWorldLocationY());
		    worldLocation.setWorldLocationZ(worldLocation.getWorldLocationZ());	        		    
		    VelocityVectorStruct velocityVectorStruct = new VelocityVectorStruct(0.5415523,-0.5452158,-1.1100446);
		    OrientationStruct orientationStruct = new OrientationStruct(0.0,0.0,0.0);
		    SpatialFPStruct spatialFPStruct = new SpatialFPStruct(worldLocation,RPRboolean.False,velocityVectorStruct,orientationStruct);
		    SpatialFPStructEncoder spatialFPStructEncoder2 = new SpatialFPStructEncoder(spatialFPStruct);  			    
	        this.attributes.put(this._attributeSpatial, spatialFPStructEncoder2.toByteArray());	        
	        _rtiAmbassador.updateAttributeValues( _userId, attributes, null);
	       teste1 += 1000;
	   }	
	
	
}
