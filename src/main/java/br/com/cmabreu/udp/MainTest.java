package br.com.cmabreu.udp;

public class MainTest {

	private static void testReceive() {
		XPlaneDataPacket dtp = new XPlaneDataPacket( "fdfdfd", "dsdsd".getBytes() );
		
		String msg = "444154412A140000006E53B7C124A72CC2964B54403AC9883E0000803F0E4B54400000B4C100002CC214000000D2BC3D42749CF4C203CFA9434DE28B3E0000803FF677A94300003E420000F4C214000000D2BC3D42749CF4C203CFA9434DE28B3E0000803FF677A94300003E420000F4C214000000D2BC3D42749CF4C203CFA9434DE28B3E0000803FF677A94300003E420000F4C2";
		dtp.processMessage(msg);
		
		
		try {
			new XPlaneDataProcessor().process(dtp);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static void testSend() {
		int port = 49003;
		String msg = "444154412A140000006E53B7C124A72CC2964B54403AC9883E0000803F0E4B54400000B4C100002CC214000000D2BC3D42749CF4C203CFA9434DE28B3E0000803FF677A94300003E420000F4C214000000D2BC3D42749CF4C203CFA9434DE28B3E0000803FF677A94300003E420000F4C214000000D2BC3D42749CF4C203CFA9434DE28B3E0000803FF677A94300003E420000F4C2";
		try {
			UDPClient client = new UDPClient( "localhost", port );
			client.sendEcho(msg);
			client.close();
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}
	
	
	public static void main(String[] args) {
		testSend();
		testReceive();
	}

}
