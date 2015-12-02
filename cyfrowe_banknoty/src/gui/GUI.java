package gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import listeners.CommandListener;
import listeners.LoginEvent;
import listeners.LoginListener;
import listeners.UserInputEvent;
import listeners.UserInputListener;
import server.Server;
import support.Roles;
import vendor.Vendor;
import alice.Alice;
import bank.Bank;

public class GUI extends JFrame{
	
	private static final long serialVersionUID = -3503888985555900291L;
	static Point mouse_down_comp_coords;
	
	private CommandPanel cmd_panel;
	private Toolbar toolbar;
	private LoginPanel login_panel;
	
	private Roles app_role;
	private Server server;
	private Bank bank;
	private Vendor vendor;
	private Alice alice;
	
	public GUI() {

		initGUI();
		initListeners();
	}
	
	private void initGUI() {
		
		setLayout(new BorderLayout());
	
		toolbar = new Toolbar();
		cmd_panel = new CommandPanel();
		login_panel = new LoginPanel();
		
		add(toolbar, BorderLayout.NORTH);
		add(cmd_panel, BorderLayout.CENTER);
		add(login_panel, BorderLayout.WEST);
		
//		setUndecorated(true);
		setResizable(false);
		setLocation(100, 100);
		setTitle("Cyfrowe Pieniądze");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		setSize(700, 400);
		setVisible(true);
	}

	private void initListeners() {
		
		toolbar.setCommandListener(new CommandListener(){
			public void CommandEmitted(String cmd, boolean nl) {
				if (nl)
					cmd_panel.appendLine(cmd);
				else
					cmd_panel.appendText(cmd);
			}
		});
		
		login_panel.setLoginListener(new LoginListener() {
			public void loginEventOccured(LoginEvent e) {
				String role = e.getRole();
				String host = e.getHost();
				int port = Integer.parseInt(e.getPort());
				
				host = host.equals("") ? "localhost" : host;
				
				try {
					app_role = Roles.valueOf(role);
				} catch (IllegalArgumentException err) { 
					app_role = Roles.Vendor;
				}
				
				switch(app_role) {
				case Server:
					setTitle(getTitle() + " - Server");
					server = new Server(port);
					server.setCommandListener(new CommandListener() {
						public void CommandEmitted(String cmd, boolean nl) {
							if (nl)
								cmd_panel.appendLine(cmd);
							else
								cmd_panel.appendText(cmd);
						}
					});
						
					server.start();					
					break;
				case Bank:
					setTitle(getTitle() + " - Bank");
					bank = new Bank(host, port);
					bank.setCommandListener(new CommandListener() {
						public void CommandEmitted(String cmd, boolean nl) {
							if (nl)
								cmd_panel.appendLine(cmd);
							else
								cmd_panel.appendText(cmd);
						}
					});
					
					bank.start();
					break;
				case Alice:
					setTitle(getTitle() + " - Alice");
					alice = new Alice(host, port);
					alice.setCommandListener(new CommandListener() {
						public void CommandEmitted(String cmd, boolean nl) {
							if (nl)
								cmd_panel.appendLine(cmd);
							else
								cmd_panel.appendText(cmd);
						}
					});
					
					alice.start();
					break;
				default:
					cmd_panel.appendLine(">>> " + app_role + " will be a Vendor.");

					setTitle(getTitle() + " - Vendor (" + app_role + ")");
					vendor = new Vendor(host, port);
					vendor.setCommandListener(new CommandListener() {
						public void CommandEmitted(String cmd, boolean nl) {
							if (nl)
								cmd_panel.appendLine(cmd);
							else
								cmd_panel.appendText(cmd);
						}
					});
					
					vendor.start();
					break;
				}
				
			}
		});
		
		cmd_panel.setUserInputListener(new UserInputListener() {
			
			// w zależności od tego, jaki "aktor" uruchomił aplikację, temu przekazywane są polecenia użytkownika
			public void userWrites(UserInputEvent e) {
				switch(app_role) {
				case Bank:
					bank.manageUserInput(e.getUserIn());
					break;
				case Vendor:
					vendor.manageUserInput(e.getUserIn());
					break;
				case Alice:
					alice.manageUserInput(e.getUserIn());
					break;
				case Server:
					break;
				default:
					break;
				}
			}
		});
		
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				dispose();
				System.gc();
			};
		});
		
		mouse_down_comp_coords = null;

		addMouseListener(new MouseListener() {	
			public void mouseReleased(MouseEvent e) {
				mouse_down_comp_coords = null;
			}
			
			public void mousePressed(MouseEvent e) {
				mouse_down_comp_coords = e.getPoint();
			}
			
			public void mouseExited(MouseEvent arg0) {}
			
			public void mouseEntered(MouseEvent arg0) {}
			
			public void mouseClicked(MouseEvent arg0) {}
		});
		
		addMouseMotionListener(new MouseMotionListener() {
			public void mouseMoved(MouseEvent e) {}
			
			public void mouseDragged(MouseEvent e) {
				Point currentCoords = e.getLocationOnScreen();
				setLocation(currentCoords.x - mouse_down_comp_coords.x, currentCoords.y - mouse_down_comp_coords.y);
			}
		});
	}
	
	public static void main(String[] args) {
	
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				new GUI();
			}
		});
	}

}
