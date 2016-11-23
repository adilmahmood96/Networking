import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class Client {

	private static final long serialVersionUID = 1L;
	private String name;
	private String address;
	private int port;
	private DatagramSocket sock;
	private InetAddress ipAdd; 
	private Thread thrdSend;
	private int ID = -1; //not specified yet

	public Client(String name, String address, int port){
		this.name = name;
		this.address = address;
		this.port = port;
	}
	
	public boolean startConnection(String address){
		try {
			sock = new DatagramSocket();
			ipAdd = InetAddress.getByName(address);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
		
	}
	

	
	//receiving packets 
	//will need a thread for receive, as packets will be waiting to be processed at same time
	public String recievePackets(){
		byte[] byteLength = new byte[1024];
		//packet to store the data
		DatagramPacket pack1 = new DatagramPacket(byteLength, byteLength.length);
		try {
			//listen and receive the packets
			sock.receive(pack1);
		} catch (IOException e) {
			e.printStackTrace();
		}
		//return array of the bytes created above
		String msg1 = new String(pack1.getData());
	
		return msg1;
	}
	
	//for sending data thread
	//final due to anomalous inner class
	public void sendBytes(final byte[] byteData){
		thrdSend = new Thread("Send"){
			public void run(){
				//send a datagram packet now
				//need to send to specific destination
				DatagramPacket pac2 = new DatagramPacket(byteData, byteData.length, ipAdd, port);
				try {
					sock.send(pac2);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};thrdSend.start();
		
	}

	public String getName() {
		// TODO Auto-generated method stub
		return name;
	}
	
	public String getAddr(){
		return address;
	}
	
	public int getPort(){
		return port;
	}

	public void setID(int ID) {
        this.ID = ID;
		
	}
	public int getID(){
		return ID;
	}
	//method for disc(closing)
	public void exit(){
		//syc for sock so it doesn't terminate it whilst still in use
		synchronized (sock) {
			sock.close();

		}
	}

}
