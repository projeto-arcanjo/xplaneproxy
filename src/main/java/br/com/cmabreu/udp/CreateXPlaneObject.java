package br.com.cmabreu.udp;

public class CreateXPlaneObject {
	
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
strcpy(ObjectPath, "Resources\\default scenery\\X-Plane objects\\landscape\\powerline_tower.obj");

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
