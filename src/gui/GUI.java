package gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JFrame;

public class GUI extends JFrame{
	private static final long serialVersionUID = -3503888985555900291L;
	static Point mouseDownCompCoords;
	private CommandPanel textPanel;
	private Toolbar toolbar;
	private LoginPanel loginPanel;
	
	public GUI() {
		
		initGUI();
	}
	
	private void initGUI() {
		
		setLayout(new BorderLayout());
	
		toolbar = new Toolbar();
		textPanel = new CommandPanel();
		loginPanel = new LoginPanel();
		
		toolbar.setStrigListener(new StringListener(){
			public void textEmitted(String text) {
				textPanel.appendText(text);
			}
		});
		
		loginPanel.setLoginListener(new LoginListener() {
			public void loginEventOccured(LoginEvent e) {
				String role = e.getRole();
				String host = e.getHost();
				String port = e.getPort();
				
				textPanel.appendText(">>> " + role + " is connecting to (" + host + ":" + port + ").\n");
			}
		});
		
		add(toolbar, BorderLayout.NORTH);
		add(textPanel, BorderLayout.CENTER);
		add(loginPanel, BorderLayout.WEST);
		
		mouseDownCompCoords = null;
		addMouseListeners();
		
//		setUndecorated(true);
		setResizable(false);
		setLocation(100, 100);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(600, 400);
		setVisible(true);
	}

	private void addMouseListeners() {
		addMouseListener(new MouseListener() {	
			public void mouseReleased(MouseEvent e) {
				mouseDownCompCoords = null;
			}
			
			public void mousePressed(MouseEvent e) {
				mouseDownCompCoords = e.getPoint();
			}
			
			public void mouseExited(MouseEvent arg0) {}
			
			public void mouseEntered(MouseEvent arg0) {}
			
			public void mouseClicked(MouseEvent arg0) {}
		});
		
		addMouseMotionListener(new MouseMotionListener() {
			public void mouseMoved(MouseEvent e) {}
			
			public void mouseDragged(MouseEvent e) {
				Point currentCoords = e.getLocationOnScreen();
				setLocation(currentCoords.x - mouseDownCompCoords.x, currentCoords.y - mouseDownCompCoords.y);
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
