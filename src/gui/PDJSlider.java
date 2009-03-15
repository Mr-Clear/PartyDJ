package gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;

public class PDJSlider extends JPanel
{
	private static final long serialVersionUID = -4711501280677705114L;
	
	private JLabel start = new JLabel(" ");
	private JLabel end = new JLabel(" ");
	private JLabel middle = new JLabel(" ");
	private JSlider slider = new JSlider();
	
	public PDJSlider()
	{
		this.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		this.setBackground(Color.darkGray);
		
		slider.setBackground(Color.darkGray);
		slider.setPaintTicks(true);
		slider.setValue(0);
		
		start.setForeground(Color.green);
		middle.setForeground(Color.green);
		end.setForeground(Color.green);
		start.setFont(new Font(start.getFont().getName(), Font.BOLD, 12)); 
		middle.setFont(new Font(middle.getFont().getName(), Font.BOLD, 12)); 
		end.setFont(new Font(end.getFont().getName(), Font.BOLD, 12)); 
		
		middle.setVisible(true);
		end.setVisible(true);
		start.setVisible(true);
		
		c.fill = GridBagConstraints.BOTH;
		c.gridwidth = 3;
		c.ipadx = 1600;
		c.gridx = 0;
		c.gridy = 0;
		this.add(slider, c);
		
		c.fill = GridBagConstraints.NONE;
		c.gridwidth = 0;
		c.anchor = GridBagConstraints.WEST;
		c.insets = new Insets(0, 0, 0, 0);
		c.ipadx = 0;
		c.gridwidth = 1;
		c.gridx = 0;
		c.gridy = 1;
		this.add(start, c);
		
		c.anchor = GridBagConstraints.CENTER;
		c.gridx = 1;
		c.insets = new Insets(0, 367, 0, 0);
		this.add(middle, c);
		
		c.gridx = 2;
		c.anchor = GridBagConstraints.EAST;
		c.insets = new Insets(0, 0, 0, 0);
		this.add(end, c);
		
		this.setVisible(true);
	}

	public void setEndLabel(String endLabel)
	{
		end.setText(endLabel);
	}
	
	public void setStartLabel(String startLabel)
	{
		start.setText(startLabel);
	}
	
	public void setMiddleLabel(String middleLabel)
	{
		middle.setText(middleLabel);
	}
	
	public void setValue(int value)
	{
		slider.setValue(value);
	}
	
	public void setMaximum(int maximum)
	{
		slider.setMaximum(maximum);
	}
	
	public void setMajorTickSpacing(int major)
	{
		slider.setMajorTickSpacing(major);
	}
	
	public void setMinorTickSpacing(int minor)
	{
		slider.setMinorTickSpacing(minor);
	}
	

	public static void main(String...args)
	{
		new PDJSlider();
	}
}
