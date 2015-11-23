package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.Border;

import listeners.UserInputEvent;
import listeners.UserInputListener;

public class CommandPanel extends JPanel implements ActionListener{
	private static final long serialVersionUID = -5776073736711008761L;
	private JTextArea textArea;
	private JScrollPane scrollArea;
	private JTextField textField;
	
	private String user_in;
	private UserInputListener user_in_listener;
	
	public CommandPanel() {
		textArea = new JTextArea();
		textArea.setBackground(Color.BLACK);
		textArea.setForeground(Color.WHITE);
		textArea.setLineWrap(true);
		textArea.setCaretColor(Color.WHITE);
		textArea.setMargin(new Insets(0, 2, 0, 2));
		textArea.setEditable(false);
		
		scrollArea = new JScrollPane(textArea);
		scrollArea.setBackground(Color.BLACK);
		scrollArea.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		
		textField = new JTextField();
		textField.setBackground(Color.BLACK);
		textField.setForeground(Color.WHITE);
		textField.setCaretColor(Color.WHITE);
		textField.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
		
		textField.addActionListener(this);
		
		setLayout(new BorderLayout());
		
		Border innerBorder = BorderFactory.createLineBorder(Color.BLACK);
		Border outerBorder = BorderFactory.createEmptyBorder(5, 5, 5, 5);
		setBorder(BorderFactory.createCompoundBorder(outerBorder, innerBorder));
		
		add(scrollArea, BorderLayout.CENTER);
		add(textField, BorderLayout.SOUTH);
	}
	
	public void setUserInputListener(UserInputListener listener) {
		this.user_in_listener = listener;
	}
	
	public void appendText(String text) {
		textArea.append(text + "\n");
	}
	
	public void appendError(String msg) {
		textArea.append("[!] Error: " + msg + "\n");
	}

	public void actionPerformed(ActionEvent e) {
		user_in = textField.getText();
		appendText("[usr] " + user_in);
		textField.setText("");
		
		UserInputEvent ev = new UserInputEvent(this, user_in);
		
		if (user_in_listener != null)
			user_in_listener.userWrites(ev);
	}
}