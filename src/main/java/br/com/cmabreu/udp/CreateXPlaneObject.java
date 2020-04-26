package br.com.cmabreu.udp;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class CreateXPlaneObject {
	private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

	private static void log( ByteBuffer buffer ) {
		System.out.println( buffer.toString() );
	}
	
	
	private static void sendObjn( int index, String path ) {
		// 504 bytes ( 504 + "OBJN" + 0  = 509 )
		ByteBuffer buffer = ByteBuffer.allocate( 509 );
		buffer.put( "OBJN".getBytes( StandardCharsets.US_ASCII ) );
		buffer.putInt( 0 );
		buffer.putInt( getReverseInt( index ) );
		buffer.put( path.getBytes( StandardCharsets.US_ASCII ) );
		sendData( buffer.array() );
	}

	
	private static void sendObjl( int index) {
		// 56 bytes
		
		float lat = getReverseFloat( (float)-22.80103 );
		float lon = getReverseFloat( (float)-43.22729 );
		float ele = getReverseFloat( (float)0.0 );
		
		float psi = getReverseFloat( (float)0.0 );
		float the = getReverseFloat( (float)0.0 );
		float phi = getReverseFloat( (float)0.0 );
		
		float smoke = getReverseFloat( (float)5.0 );
		
		ByteBuffer buffer = ByteBuffer.allocate( 61 );
		buffer.put( "OBJL".getBytes( StandardCharsets.US_ASCII ) );
		
		buffer.putInt( 0 );
		buffer.putInt( getReverseInt( index ) );
		buffer.putDouble( lat );
		buffer.putDouble( lon );
		buffer.putDouble( ele );
		buffer.putFloat( psi );
		buffer.putFloat( the );
		buffer.putFloat( phi );
		buffer.putInt( 0 );			// On the ground?
		buffer.putFloat( smoke );	// Smoke size

		
		buffer.put( "000".getBytes( StandardCharsets.UTF_8 ) );			// Desconhecido
		log( buffer );
		
		sendData( buffer.array() );
	}
	
	
	public static void main(String[] args) {
		sendObjn( 4, "Resources\\default scenery\\sim objects\\apt_aircraft\\fighter\\F4\\F4_static.obj" );
		sendObjl( 4 );
	}
	
	
	
	
	private static void sendData( byte[] data ) {
		try {
			UDPClient client = new UDPClient("192.168.0.76", 49000);
			client.sendData( data );
		} catch ( Exception e) {
			e.printStackTrace();
		}
	}
	
	
	private static String reverseHex(String originalHex) {
		int lengthInBytes = originalHex.length() / 2;
		char[] chars = new char[lengthInBytes*2];
		for (int index = 0 ; index < lengthInBytes; index++) {
			int reversedIndex = lengthInBytes -1 - index;
			chars[reversedIndex*2] = originalHex.charAt(index*2);
			chars[reversedIndex*2+1] = originalHex.charAt(index*2+1);
		}
		return new String(chars);
	}
	
	private static String bytesToHex(byte[] bytes) {
		char[] hexChars = new char[bytes.length*2];
		for (int j = 0 ; j < bytes.length; j++) {
			int v = bytes[j] & 0xFF;
			hexChars[j*2]= HEX_ARRAY[v >>> 4];
			hexChars[j*2 + 1]= HEX_ARRAY[v & 0x0F];
		}
		return new String(hexChars);
	}
	
/*
	private static double getReverseDouble(double value) {
		System.out.println("Reverse Double: " + value );

		ByteBuffer tempBuffer = ByteBuffer.allocate( 8 ).putDouble( value );
		String hexBytes = bytesToHex( tempBuffer.array() );
		System.out.println("Hex:  " + hexBytes );
		
		String reverseHex = reverseHex( hexBytes );
		System.out.println("rHex: " + reverseHex );

		double result = Double.parseDouble( reverseHex );
		System.out.println("Res:  " + result );
		
		return result;
	}
*/
	
	private static int getReverseInt( int value ) {
		ByteBuffer tempBuffer = ByteBuffer.allocate( 4 ).putInt( value );
		String hexBytes = bytesToHex( tempBuffer.array() );
		String reverseHex = reverseHex( hexBytes );
		int result = Integer.parseInt( reverseHex, 16 );
		
		System.out.println("Reverse Integer: " + value );
		System.out.println("Hex:  " + hexBytes );
		System.out.println("rHex: " + reverseHex );
		System.out.println("Res:  " + result );
		return result;
	}
	
	private static float getReverseFloat( float value ) {
		ByteBuffer tempBuffer = ByteBuffer.allocate( 4 ).putFloat( value );
		String hexBytes = bytesToHex( tempBuffer.array() );
		String reverseHex = reverseHex( hexBytes );
		//int result = Integer.parseInt( reverseHex, 16 );
		float result = hexToFloat( reverseHex );
		
		System.out.println("Reverse Float: " + value );
		System.out.println("Hex:  " + hexBytes );
		System.out.println("rHex: " + reverseHex );
		System.out.println("Res:  " + result );
		return result;
	}
	
	private static float hexToFloat(String myString) {
		Long i = Long.parseLong(myString,16);
		Float f = Float.intBitsToFloat(i.intValue());
		return f;
	}    
    	

	
	
	
	
	
	
	
	
	// Exemplo tirado de https://forums.x-plane.org/index.php?/forums/topic/15583-placing-objects-into-the-x-plane-3d-world-via-udp/
	
	
/*	
char Buffer[4096], ErrorString[80];
int Index, Ptr = 0;
float SendData[8];
int NumberBytes;

AddressLen = sizeof(Address);
memset(SendData, 0, sizeof(SendData));
strcpy(Buffer, "OBJN");
Ptr += 4;
Buffer[Ptr++] = 0;
Buffer[Ptr++] = 0;
Buffer[Ptr++] = 0;
Buffer[Ptr++] = 0;
Buffer[Ptr++] = 1;
char ObjectPath[500];
// 
strcpy(ObjectPath, "Resources\\default scenery\\900 roads\\powerline_tower.obj");

memcpy(Buffer+Ptr, ObjectPath, sizeof(ObjectPath));
Ptr += sizeof(ObjectPath);

NumberBytes = sendto (XplaneSocket, Buffer, Ptr, 0, (LPSOCKADDR)&amp;Address, AddressLen);
Ptr = 0;
strcpy(Buffer, "OBJL");
Ptr += 4;
Buffer[Ptr++] = 0;
Buffer[Ptr++] = 0;
Buffer[Ptr++] = 0;
Buffer[Ptr++] = 0;
Buffer[Ptr++] = 1;
double ObjectPosition[3];
ObjectPosition[0] = 34.091;
ObjectPosition[1] = -117.25;
ObjectPosition[2] = 1161.7;
memcpy(Buffer+Ptr, &amp;ObjectPosition, sizeof(ObjectPosition));
Ptr += sizeof(ObjectPosition);

float ObjectAttitude[3];
ObjectAttitude[0] = 70.203;
ObjectAttitude[1] = 0.0;
ObjectAttitude[2] = 0.0;
memcpy(Buffer+Ptr, &amp;ObjectAttitude, sizeof(ObjectAttitude));
Ptr += sizeof(ObjectAttitude);

int OnGround = 0;
memcpy(Buffer+Ptr, &amp;OnGround, sizeof(OnGround));
Ptr += sizeof(OnGround);

float SmokeSize = 0;
memcpy(Buffer+Ptr, &amp;SmokeSize, sizeof(SmokeSize));
Ptr += sizeof(SmokeSize);

NumberBytes = sendto (XplaneSocket, Buffer, Ptr, 0, (LPSOCKADDR)&amp;Address, AddressLen);	
	
*/
	
	
	
// http://www.nuclearprojects.com/xplane/xplaneref.html
	
/*	
OBJN
DATA INPUT STRUCTURE:
struct objN_struct{ // object name: draw any object in the world in the sim
xint index;
xchr path[strDIM];};
Just like the airplane struct, but with any OBJ7 object (see the San Bernardino "KSBD_example.obj" sample object 
in the Custom Scenery folder for an example of an OBJ7 object. With this message, simply send in the path of any object 
that you have on the drive and you want X-Plane to display! The location is controlled with the struct below.	
*/	

	
/*	
DATA INPUT STRUCTURE:
struct objL_struct{ // object location: draw any object in the world in the sim
xint index;
xdob lat_lon_ele[3];
xflt psi_the_phi[3];
xint on_ground; // is this object on the ground? if so, simply enter 0 for the elevation, x-plane will put it on the ground
xflt smoke_size;}; // is this object smoking? if so, simply indicate the size of the smoke puffs here
Enter the location of the object you loaded here. It can be a tank driving around on the ground, a missile firing, or anythng else you can imagine.	
*/
	
	
}
