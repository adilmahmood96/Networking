

import java.net.InetAddress;

public class ServerClient {

	public String name;
	public InetAddress addr;
	public int port;
	public final int ID;
	public int trying = 0;
	
	public ServerClient(String name, InetAddress addr, int port, final int ID){
		this.name = name;
		this.addr = addr;
		this.port = port;	
		this.ID = ID;
	}
	
	public int getID(){
		return ID;
	}
}
