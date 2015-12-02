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

import listeners.LoginEvent;
import listeners.LoginListener;


public class LoginPanel extends JPanel implements ActionListener {
	private static final long serialVersionUID = 4493531797921885789L;
	private JLabel role_label;
	private JLabel host_label;
	private JLabel port_label;
	private JTextField role_field;
	private JTextField host_field;
	private JTextField port_field;
	private JButton login_btn;
	
	private LoginListener login_listener;
	
	public LoginPanel() {
		Dimension dim = getPreferredSize();
		dim.width = 250;
		setPreferredSize(dim);
		
		role_label = new JLabel("Role: ");
		host_label = new JLabel("Host: ");
		port_label = new JLabel("Port: ");
		
		role_field = new JTextField(10);
		host_field = new JTextField(10);
		port_field = new JTextField(10);
		
		login_btn = new JButton("Login");
		login_btn.addActionListener(this);
		
		Border inner_border = BorderFactory.createTitledBorder("Log In");
		Border outer_border = BorderFactory.createEmptyBorder(5, 5, 5, 5);
		setBorder(BorderFactory.createCompoundBorder(outer_border, inner_border));
		
		setLayout(new GridBagLayout());
		
		GridBagConstraints gc = new GridBagConstraints();
		
		///////////////// FIRST ROW //////////////////////////
		
		gc.weightx = 1;
		gc.weighty = 0.1;
		
		gc.gridx = 0;
		gc.gridy = 0;
		gc.anchor = GridBagConstraints.LINE_END;
		add(role_label, gc);
		
		gc.gridx = 1;
		gc.gridy = 0;
		gc.anchor = GridBagConstraints.LINE_START;
		add(role_field, gc);
		
		///////////////// SECOND ROW //////////////////////////
		
		gc.weightx = 1;
		gc.weighty = 0.1;
		
		gc.gridx = 0;
		gc.gridy = 1;
		gc.anchor = GridBagConstraints.LINE_END;
		add(host_label, gc);
		
		gc.gridx = 1;
		gc.gridy = 1;
		gc.anchor = GridBagConstraints.LINE_START;
		add(host_field, gc);
		
		///////////////// THIRD ROW //////////////////////////
		
		gc.weightx = 1;
		gc.weighty = 0.1;
		
		gc.gridx = 0;
		gc.gridy = 2;
		gc.anchor = GridBagConstraints.LINE_END;
		add(port_label, gc);
		
		gc.gridx = 1;
		gc.gridy = 2;
		gc.anchor = GridBagConstraints.LINE_START;
		add(port_field, gc);
		
		///////////////// FOURTH ROW //////////////////////////
		
		gc.weightx = 1;
		gc.weighty = 2.0;
		
		gc.gridx = 1;
		gc.gridy = 3;
		gc.anchor = GridBagConstraints.FIRST_LINE_START;
		add(login_btn, gc);
	}
	
	public void setLoginListener(LoginListener listener) {
		this.login_listener = listener;
	}
	
	public void actionPerformed(ActionEvent e) {
		JButton clicked = (JButton) e.getSource();
		
		if (clicked == login_btn) {
			String role = role_field.getText();
			String host = host_field.getText();
			String port = port_field.getText();
			
			LoginEvent ev = new LoginEvent(this, role, host, port);
			
			if (login_listener != null) 
				login_listener.loginEventOccured(ev);
		}
	}
}
