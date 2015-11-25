package gui;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.Border;

import listeners.CommandListener;

public class Toolbar extends JPanel implements ActionListener{
	
	private static final long serialVersionUID = -8572905636384948856L;
	private JButton helloBtn;
	private JButton goodbyeBtn;
	
	private CommandListener cmd_listener;
	
	public Toolbar() {
		helloBtn = new JButton("Hello");
		goodbyeBtn = new JButton("Goodbye");
		
		helloBtn.addActionListener(this);
		goodbyeBtn.addActionListener(this);
		
		setLayout(new FlowLayout(FlowLayout.LEFT));
		
		Border innerBorder = BorderFactory.createTitledBorder("Toolbar");
		Border outerBorder = BorderFactory.createEmptyBorder(5, 5, 5, 5);
		setBorder(BorderFactory.createCompoundBorder(outerBorder, innerBorder));
		
		add(helloBtn);
		add(goodbyeBtn);	
	}
	
	public void setCommandListener(CommandListener listener) {
		this.cmd_listener = listener;
	}

	public void actionPerformed(ActionEvent e) {
		JButton clicked = (JButton) e.getSource();
		
		if (clicked == helloBtn) {
			if (cmd_listener != null)
				cmd_listener.CommandEmitted("Hello", true);
		} else if (clicked == goodbyeBtn) {
			if (cmd_listener != null)
				cmd_listener.CommandEmitted("Goodbye", true);
		}
	}
}