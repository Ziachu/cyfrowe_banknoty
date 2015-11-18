package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.Border;

public class CommandPanel extends JPanel {
	private static final long serialVersionUID = -5776073736711008761L;
	private JTextArea textArea;
	private JScrollPane scrollArea;
	
	public CommandPanel() {
		textArea = new JTextArea();
		textArea.setBackground(Color.BLACK);
		textArea.setForeground(Color.WHITE);
		textArea.setLineWrap(true);
		textArea.setCaretColor(Color.WHITE);
		textArea.setMargin(new Insets(0, 2, 0, 2));
		
		scrollArea = new JScrollPane(textArea);
		scrollArea.setBackground(Color.BLACK);
		scrollArea.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		
		setLayout(new BorderLayout());
		
		Border innerBorder = BorderFactory.createLineBorder(Color.BLACK);
		Border outerBorder = BorderFactory.createEmptyBorder(5, 5, 5, 5);
		setBorder(BorderFactory.createCompoundBorder(outerBorder, innerBorder));
		
		add(scrollArea, BorderLayout.CENTER);
	}
	
	public void appendText(String text) {
		textArea.append(text);
	}
}