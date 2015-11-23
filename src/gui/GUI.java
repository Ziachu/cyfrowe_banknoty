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

import server.Server;

public class GUI extends JFrame{
	
	private static final long serialVersionUID = -3503888985555900291L;
	static Point mouse_down_comp_coords;
	
	private CommandPanel cmd_panel;
	private Toolbar toolbar;
	private LoginPanel login_panel;
	
	private Server server;
	
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
		
		setSize(600, 400);
		setVisible(true);
	}

	private void initListeners() {
		
		toolbar.setCommandListener(new CommandListener(){
			public void CommandEmitted(String cmd) {
				cmd_panel.appendText(cmd);
			}
		});
		
		login_panel.setLoginListener(new LoginListener() {
			public void loginEventOccured(LoginEvent e) {
				String role = e.getRole();
				String host = e.getHost();
				String port = e.getPort();
				
				host = host.equals("") ? "localhost" : host;
				//cmd_panel.appendText(">>> " + role + " is connecting to (" + host + ":" + port + ").");
				
				switch(role) {
				case "Server":
					server = new Server(Integer.parseInt(port));
					server.setCommandListener(new CommandListener() {
							
						public void CommandEmitted(String cmd) {
							cmd_panel.appendText(cmd);
						}
					});
						
					server.start();

					cmd_panel.appendText(">>> Server is up and runnig.");
					
					break;
				case "Bank":
					
					break;
				default:
					cmd_panel.appendText(">>> Role undefined.");
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
