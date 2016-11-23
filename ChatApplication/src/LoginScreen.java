import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

public class LoginScreen extends JFrame {

	
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField txtName;
	private JTextField txtAdress;
	private JLabel lblIP;
	private JLabel lblPort;
	private JTextField txtPort;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					LoginScreen frame = new LoginScreen();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});		
	}
	//login method for listener:
			public void login(String name, String address, int port) {	
				dispose();
				new ClientInterface(name, address, port);
			}

	public LoginScreen(){
		
		setResizable(false);
		setTitle("Login");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setSize(300,375);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		txtName = new JTextField();
		txtName.setBounds(71, 42, 151, 20);
		contentPane.add(txtName);
		txtName.setColumns(10);
		
		JLabel labelName = new JLabel("Name");
		labelName.setBounds(123, 21, 42, 14);
		contentPane.add(labelName);
		
		txtAdress = new JTextField();
		txtAdress.setBounds(71, 110, 151, 20);
		contentPane.add(txtAdress);
		txtAdress.setColumns(10);
		
		lblIP = new JLabel("Input IP Adress:");
		lblIP.setBounds(101, 85, 106, 14);
		contentPane.add(lblIP);
		
		lblPort = new JLabel("Input port:");
		lblPort.setBounds(123, 155, 75, 14);
		contentPane.add(lblPort);
		
		txtPort = new JTextField();
		txtPort.setBounds(71, 180, 151, 20);
		contentPane.add(txtPort);
		txtPort.setColumns(10);
		
		JButton btnLogin = new JButton("Login");
		
		//listener for login button:
		btnLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String name = txtName.getText();
				String address = txtAdress.getText();
				int port = Integer.parseInt(txtPort.getText());
				login(name, address, port);
			}
		});
		btnLogin.setBounds(103, 271, 89, 23);
		contentPane.add(btnLogin);
		
		//UI manager to set look and feel to be same as native appplications on platform..
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
	}
}
}
