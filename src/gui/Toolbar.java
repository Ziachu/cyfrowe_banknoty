package gui;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.Border;

public class Toolbar extends JPanel implements ActionListener{
	
	private static final long serialVersionUID = -8572905636384948856L;
	private JButton helloBtn;
	private JButton goodbyeBtn;
	
	private StringListener textListener;
	
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
	
	public void setStrigListener(StringListener listener) {
		this.textListener = listener;
	}

	public void actionPerformed(ActionEvent e) {
		JButton clicked = (JButton) e.getSource();
		
		if (clicked == helloBtn) {
			if (textListener != null)
				textListener.textEmitted("Hello\n");
		} else if (clicked == goodbyeBtn) {
			if (textListener != null)
				textListener.textEmitted("Goodbye\n");
		}
	}
}