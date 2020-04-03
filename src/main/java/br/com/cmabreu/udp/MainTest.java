package br.com.cmabreu.udp;

public class MainTest {

/*	
	
__lat,__deg |   __lon,__deg |   __alt,ftmsl |   __alt,ftagl |   ___on,runwy |   __alt,__ind |   __lat,south |   __lon,_west | 
------------|---------------|---------------|---------------|---------------|---------------|---------------|---------------|
   47.50037 |    -122.21684 |       4.87364 |       0.35146 |       1.00000 |       4.87439 |      46.00000 |    -124.00000 | 	
	
*/	
	public static void main(String[] args) {
		byte[] by = {3,5,6,7};
		XPlaneDataPacket dtp = new XPlaneDataPacket( "fdfdfd", by );
		
		for( XPlaneData data : dtp.getData() ) {
			System.out.println( "Indice " + data.getIndex() + " : [ " + data.toString() + " ]");
		}
		
	}

}
