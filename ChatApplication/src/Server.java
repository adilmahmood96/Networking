import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Server implements Runnable{
	
	private int port;
	private DatagramSocket sock;
	private Thread thrdServer;
	private Thread thrdControl;
	private Thread thrdRecieve;
	private Thread thrdSend;
	//arraylist of clients
	private List<ServerClient> users = new ArrayList<ServerClient>();
	private List<Integer> clientReplies = new ArrayList<Integer>();
	private boolean serverRun = false;
	//client allowed 3 attempts to connect before getting disconnected 
	private final int MAX = 6;
	private boolean rw = false;
	
	public Server(int port){
		this.port = port;
		try {
			//send and recieve data through this socket
			sock = new DatagramSocket(port);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		thrdServer = new Thread(this, "Server");
		thrdServer.start();
	}
	public void run(){
		//server commands: raw and users and kick
		serverRun = true;
		System.out.println("Server connected on port " + port);
		controlClient();
		retreive();
		//while we run can accept certain commands
		Scanner scnr = new Scanner(System.in);
		while(serverRun){
			String txtInput = scnr.nextLine();
			if(!txtInput.startsWith("/")){
				sendAll("/m/Server: " + txtInput + "/e/");
				continue;
			}
			txtInput = txtInput.substring(1);
			if(txtInput.equals("rw")){
				//
				rw = !rw;
			} else if(txtInput.equals("users")){
				System.out.println("Users:");
				System.out.println("-----------");
				for(int i = 0; i < users.size(); i++){
					ServerClient x = users.get(i);
					System.out.println(x.name + "(" + x.getID() + "): " + x.addr.toString() + ": "+ x.port );
				}
				System.out.println("-----------");
			} else if(txtInput.startsWith("kick")){
				//we left with /[kick] [adil] we want index 1
				String userName = txtInput.split(" ")[1];
				//want to figure out if int or string
				int id = -1;
				boolean number = true;
				//try to conver userName to string
				try{
					id = Integer.parseInt(userName);
				}catch(NumberFormatException e){
					number = false;
				}if(number){
					boolean wake = false;
					for(int i = 0; i < users.size(); i++){
						if(users.get(i).getID() == id){
							wake = true;
							break;
						}
					}
					if(wake) disconnectClient(id, true);
					else System.out.println("client " + id + " is not on the system. Make sure you have entered correct ID number!");
				
				}else {
					
					for(int i = 0; i < users.size(); i++){
						ServerClient x = users.get(i);
						if(userName.equals(x.name)){
							disconnectClient(x.getID(), true);
							break;
						}
					}
				}
			}
		}
	}
	
	//make sure it disconnects them and stuff manages them
	public void controlClient(){
		thrdControl = new Thread("Control"){
			public void run(){
				while(serverRun){
					//manage the client
					sendAll("/i/server");
					sendonlineState();
					//wait for a while
					try {
						Thread.sleep(3000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					for(int i = 0; i < users.size(); i++){
						ServerClient temp = users.get(i);
						if(!clientReplies.contains(temp.getID())){
							//if one of our clients doesnt respond
							if(temp.trying >= MAX){
								disconnectClient(temp.getID(), false);
							}//havent reached max attempt keep trying
							else{
								temp.trying++;
							}//if we did recieve response then:
					        }
						else {
							clientReplies.remove(new Integer(temp.getID()));	
							temp.trying = 0;
				       }
				}
			  }
			}
		};	thrdControl.start();
	
	}
	private void sendonlineState(){
		if(users.size() <= 0) return;
		String usr = "/u/";
		for(int i = 0; i < users.size();i++){
			usr = usr + users.get(i).name + "/e/";
			sendAll(usr);
		}
	}
	//Receive any type of data 
	public void retreive(){
		thrdRecieve = new Thread("Recieve"){
			public void run(){
				while(serverRun){
					//nice print statement in console of the amount of users 
					//System.out.println("Number of clients on server: " + users.size());
					//recieve data
					byte data[] = new byte[1024];
					//shoves the data we recieve into the packet then into data array
					DatagramPacket pack1 = new DatagramPacket(data, data.length);
					try {
						sock.receive(pack1);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
						
					//String toConsole = new String(pack1.getData(),pack1.getOffset(),pack1.getLength());
				    processData(pack1);
					//System.out.println(toConsole);}
			}
		}
		};thrdRecieve.start();
	}
	//send string msg to all clients
	private void sendAll(String msg){
		if(msg.startsWith("/m/")){
		String input = msg.substring(3);
		input = input.split("/e/")[0];
		System.out.println(msg);
		}
		for(int  i = 0; i < users.size(); i++){
			ServerClient client1 = users.get(i);
		//gona do it for each client from method below this one
			sending(msg.getBytes(), client1.addr, client1.port);
		}
	}
	private void sending(final byte[] data, final InetAddress adr, final int port){
		thrdSend = new Thread("send"){
			public void run(){
				DatagramPacket pack1 = new DatagramPacket(data, data.length, adr, port);
			    try {
					sock.send(pack1);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		};thrdSend.start();
		
	}
	private void send(String message, InetAddress adr, int port){
	    message += "/e/"; //prefix for end
		sending(message.getBytes(), adr, port);
		
	}
	//want to connect
	//ID for clients
	private void processData(DatagramPacket pac){
		//had to add on "0, pack.getlength(" to remove the boxes in the console..
		String temp = new String(pac.getData());
		if(rw) System.out.println(temp);
		if(temp.startsWith("/c/")){
			//UUID uniqueID = UUID.randomUUID();
			int idRandom = UniqueID.getID();			
			String nam = temp.split("/c/|/e/")[1];
			System.out.println(nam + " (" + idRandom + ") connected successfully." );

			//substring to get the name to add to users/clients
			users.add(new ServerClient(nam, pac.getAddress(), pac.getPort(), idRandom));
		    String prefix  = "/c/" + idRandom;
		    send(prefix, pac.getAddress(), pac.getPort());
 		
		} else if(temp.startsWith("/m/")){
			//chops off first 3 char of string
			sendAll(temp);
		
		} else if(temp.startsWith("/d/")){
			String ID = temp.split("/d/|/e/")[1];
			//status = true so server doesn't have to manually disc, client requests it
			disconnectClient(Integer.parseInt(ID), true);
		}
		else if(temp.startsWith("/i/")){
			//arraylist tp check em one at a time
			clientReplies.add(Integer.parseInt(temp.split("/i/|/e/")[1]));
		}
		
		else {
			System.out.println(temp);
			
		}
		
		

	} 
	//method for disc - if ID matches then disc from the server
	private void disconnectClient(int id, boolean state){
		ServerClient discClient  = null;
		boolean wake2 = false;
		for(int i = 0; i <users.size(); i++){
			if(users.get(i).getID() == id){
				discClient = users.get(i);
				users.remove(i);
				wake2 = true;
				break;
			}
		}
		if(!wake2)return;
		String msg = "";
		if(state){
			msg = "Client " + discClient.name + " (" + discClient.getID() + ") " + discClient.addr.toString() + " has disconnected." ;
		}else{
			msg = "Client " + discClient.name + " (" + discClient.getID() + ") " + discClient.addr.toString() + " has timed out." ;

		}System.out.println(msg);
	}
}
