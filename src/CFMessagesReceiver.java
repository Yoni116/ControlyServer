import java.io.IOException;
import java.net.ServerSocket;

public class CFMessagesReceiver {
private ServerSocket utilitySocket;
private int port;
	//Class to receive known messages from clients
	
	public CFMessagesReceiver() {
		try {
			utilitySocket = new ServerSocket(0);
			port = utilitySocket.getLocalPort();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
public ServerSocket getUtilitySocket(){
	return utilitySocket;
}
	
public int getPort(){
	return this.port;
}
}
