import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DefaultCaret;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
//loginscreen.java creates a new instance of client 
public class ClientInterface extends JFrame implements Runnable{

	private static final long serialVersionUID = 1L;
	private Client client;
	private JTextField txtMessageSend;
	private JTextArea textChatHistory;
	private JPanel contentPane;
	private DefaultCaret car;
	private Thread wait;
	private Thread thrdRun;
	private boolean run = false;
	private JMenuBar menuBar;
	private JMenu menUser;
	private JMenuItem mntmUsersOnline;
	private JMenuItem mntmExit;
	
	private Online onlineUsers;
	
	public ClientInterface(String name, String address, int port) {
		setResizable(false);
		setTitle("Chat Client");
		client = new Client(name, address, port);
		boolean start = client.startConnection(address);
		if(!start){
			System.out.println("connection unsuccessfull");
			chatInfoConsole("connection unsuccessful");
		}
		GUI();
		chatInfoConsole("Connecting user: " + name + ", " + "to: " + address  + ": " + port );
		//"/c/" means its a connection packet
		String connection = "/c/" + name + "/e/"; 
		client.sendBytes(connection.getBytes());
		onlineUsers = new Online(); 
		run = true;
		thrdRun = new Thread(this, "Run");
		thrdRun.start();
	}

	//Separate method for GUI to keep constructor clean
	private void GUI(){
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(850, 525);
		setLocationRelativeTo(null);
		
		menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		menUser = new JMenu("Users");
		menuBar.add(menUser);
		
		mntmUsersOnline = new JMenuItem("Users online");
		mntmUsersOnline.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onlineUsers.setVisible(true);		
			}
		});
		menUser.add(mntmUsersOnline);
		
		mntmExit = new JMenuItem("Exit");
		menUser.add(mntmExit);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);		
		
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[]{30, 780, 30, 4};
		gbl_contentPane.rowHeights = new int[]{35, 450, 15};
		gbl_contentPane.columnWeights = new double[]{1.0, 1.0};
		gbl_contentPane.rowWeights = new double[]{1.0, Double.MIN_VALUE};
		contentPane.setLayout(gbl_contentPane);
		
		textChatHistory = new JTextArea();
		textChatHistory.setEditable(false);
		car = (DefaultCaret) textChatHistory.getCaret();
		car.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		JScrollPane scroller = new JScrollPane(textChatHistory);
		GridBagConstraints constraintScroller = new GridBagConstraints();
		constraintScroller.insets = new Insets(0, 0, 5, 5);
		constraintScroller.fill = GridBagConstraints.BOTH;
		constraintScroller.gridx = 0;
		constraintScroller.gridy = 1;
		constraintScroller.gridwidth = 3;
		contentPane.add(scroller, constraintScroller);
		
		txtMessageSend = new JTextField();
		/*txtMessageSend.addKeyListener(new KeyAdapter() {

		    public void keyPressed(KeyEvent a) {
				if(a.getKeyCode() == KeyEvent.VK_ENTER);
				sendToServer(txtMessageSend.getText());
			}
		});*/
		GridBagConstraints gbc_txtMessageSend = new GridBagConstraints();
		gbc_txtMessageSend.insets = new Insets(0, 0, 0, 5);
		gbc_txtMessageSend.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtMessageSend.gridx = 1;
		gbc_txtMessageSend.gridy = 2;
		contentPane.add(txtMessageSend, gbc_txtMessageSend);
		txtMessageSend.setColumns(10);
		
		JButton btnClientSend = new JButton("Send");
		btnClientSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sendToServer(txtMessageSend.getText(), true);
			}
		});
		GridBagConstraints gbc_btnClientSend = new GridBagConstraints();
		gbc_btnClientSend.insets = new Insets(0, 0, 0, 5);
		gbc_btnClientSend.gridx = 2;
		gbc_btnClientSend.gridy = 2;
		contentPane.add(btnClientSend, gbc_btnClientSend);
		
		
		//listener for the exit button, for disconnecting from client side..
		addWindowListener(new WindowAdapter() {
	
				public void windowClosing(WindowEvent e) {
					String disc = "/d/" + client.getID() + "/e/";
					sendToServer(disc, false);
					run = false;
					client.exit();
					
				}		
	
		});
		setVisible(true);  
		txtMessageSend.requestFocusInWindow();

		//UI manager to set look and feel to be same as native applications on platform..
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
	}			
	}
	
	public void run(){
		waitAndListen();
	}
	private void sendToServer(String message1, boolean input){
		//chatInfoConsole(message1);
		if(message1.equals("")) return;
		if(input){
		message1 = client.getName() + ": " + message1;
		message1 = "/m/" + message1; 
 		txtMessageSend.setText("");

	}
		client.sendBytes(message1.getBytes()); 
	}


	public void waitAndListen(){
		wait = new Thread("Wait and listent"){
			public void run(){
				while(run){
					String msg1 = client.recievePackets();
					if(msg1.startsWith("/c/")){
					client.setID(Integer.parseInt(msg1.split("/c/|/e/")[1]));
				    chatInfoConsole("Connection to server successfull. " + "ID: " + client.getID());
				}else if (msg1.startsWith("/m/")){
					//Separate it
					String input = msg1.substring(3);
					input = input.split("/e/")[0];
					chatInfoConsole(input);
				}else if (msg1.startsWith("/i/")){
					String  txt = "/i/" + client.getID() +"/e/";
					sendToServer(txt, false);
				}else if(msg1.startsWith("/u/")){
					String[] uArray = msg1.split("/u/|/n/|/e/");
					//trim the array
					onlineUsers.refresh(Arrays.copyOfRange(uArray, 1, uArray.length - 1));
				}
			}
			}
			
		};wait.start();
	}
	public void chatInfoConsole(String message1){
		textChatHistory.append(message1 + "\n\r");
		textChatHistory.setCaretPosition(textChatHistory.getDocument().getLength());

	}
	
}
