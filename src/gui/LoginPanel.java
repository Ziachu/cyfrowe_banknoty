package gui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;

public class LoginPanel extends JPanel {
	private static final long serialVersionUID = 4493531797921885789L;
	private JLabel roleLabel;
	private JLabel hostLabel;
	private JLabel portLabel;
	private JTextField roleField;
	private JTextField hostField;
	private JTextField portField;
	private JButton loginBtn;
	private LoginListener loginListener;
	
	public LoginPanel() {
		Dimension dim = getPreferredSize();
		dim.width = 250;
		setPreferredSize(dim);
		
		roleLabel = new JLabel("Role: ");
		hostLabel = new JLabel("Host: ");
		portLabel = new JLabel("Port: ");
		
		roleField = new JTextField(10);
		hostField = new JTextField(10);
		portField = new JTextField(10);
		
		loginBtn = new JButton("Login");
		loginBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String role = roleField.getText();
				String host = hostField.getText();
				String port = portField.getText();
				
				LoginEvent ev = new LoginEvent(this, role, host, port);
				
				if (loginListener != null) 
					loginListener.loginEventOccured(ev);
			}
		});
		
		Border innerBorder = BorderFactory.createTitledBorder("Log In");
		Border outerBorder = BorderFactory.createEmptyBorder(5, 5, 5, 5);
		setBorder(BorderFactory.createCompoundBorder(outerBorder, innerBorder));
		
		setLayout(new GridBagLayout());
		
		GridBagConstraints gc = new GridBagConstraints();
		
		///////////////// FIRST ROW //////////////////////////
		
		gc.weightx = 1;
		gc.weighty = 0.1;
		
		gc.gridx = 0;
		gc.gridy = 0;
		gc.anchor = GridBagConstraints.LINE_END;
		add(roleLabel, gc);
		
		gc.gridx = 1;
		gc.gridy = 0;
		gc.anchor = GridBagConstraints.LINE_START;
		add(roleField, gc);
		
		///////////////// SECOND ROW //////////////////////////
		
		gc.weightx = 1;
		gc.weighty = 0.1;
		
		gc.gridx = 0;
		gc.gridy = 1;
		gc.anchor = GridBagConstraints.LINE_END;
		add(hostLabel, gc);
		
		gc.gridx = 1;
		gc.gridy = 1;
		gc.anchor = GridBagConstraints.LINE_START;
		add(hostField, gc);
		
		///////////////// THIRD ROW //////////////////////////
		
		gc.weightx = 1;
		gc.weighty = 0.1;
		
		gc.gridx = 0;
		gc.gridy = 2;
		gc.anchor = GridBagConstraints.LINE_END;
		add(portLabel, gc);
		
		gc.gridx = 1;
		gc.gridy = 2;
		gc.anchor = GridBagConstraints.LINE_START;
		add(portField, gc);
		
		///////////////// FOURTH ROW //////////////////////////
		
		gc.weightx = 1;
		gc.weighty = 2.0;
		
		gc.gridx = 1;
		gc.gridy = 3;
		gc.anchor = GridBagConstraints.FIRST_LINE_START;
		add(loginBtn, gc);
	}
	
	public void setLoginListener(LoginListener listener) {
		this.loginListener = listener;
	}
}
