package gui.settings;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class Settings  extends JPanel
{
	private static final long serialVersionUID = -6987892133947674516L;
	static JSpinner wish = new JSpinner(new SpinnerNumberModel(50, 0, 100, 1));
	static JSpinner main = new JSpinner(new SpinnerNumberModel(50, 0, 100, 1));
	
	public Settings()
	{
		initGUI();
	}
	
	private void initGUI()
	{
		GridBagLayout layout = new GridBagLayout();
		GridBagConstraints con = new GridBagConstraints();
		
		setLayout(layout);
		con.anchor = GridBagConstraints.NORTHWEST;
		con.insets = new Insets(0, 0, 0, 0);
		con.fill = GridBagConstraints.BOTH;
		
		con.gridx = 0;
		con.gridy = 0;
		con.weightx = 0.0;
		con.weighty = 0.0;
		con.ipadx = GridBagConstraints.REMAINDER;
		con.ipady = 22;
		add(shuffleSetting(), con);
		
		this.setVisible(true);
	}
	
	private JPanel shuffleSetting()
	{
		GridBagConstraints c = new GridBagConstraints();
		JPanel shuffleSet = new JPanel(new GridBagLayout());
		
		JLabel titel = new JLabel("Shuffle");
		JLabel expl = new JLabel("Hier können Sie einstellen, wie viel Prozent von der Wunschliste und wie viel aus der Hauptliste gespielt werden sollen.");
		JLabel wishLabel = new JLabel("Aus Wunschliste:");
		JLabel mainLabel = new JLabel("Aus Hauptliste:");
		
		titel.setFont(new Font(null, Font.BOLD, 18));
		expl.setFont(new Font(null, Font.ITALIC, 12));
		wishLabel.setFont(new Font(null, Font.LAYOUT_LEFT_TO_RIGHT, 14));
		mainLabel.setFont(new Font(null, Font.LAYOUT_LEFT_TO_RIGHT, 14));
		
		wish.addChangeListener(new SpinnerListener(main));
		main.addChangeListener(new SpinnerListener(wish));
		wish.setName("wish");
		main.setName("main");
		
		c.insets = new Insets(0, 5, 0, 5);
		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.BOTH;
		shuffleSet.setBackground(Color.darkGray);	
		
		c.weightx = 0.0;
		c.weighty = 0.0;
		//c.ipadx = 10;
		c.gridx = 0;
		c.gridy = 0;
		shuffleSet.add(titel, c);
		
		c.insets = new Insets(5, 5, 10, 5);
		c.weightx = 0.0;
		c.weighty = 0.0;
		//c.ipadx = 50;
		c.gridy = 1;
		shuffleSet.add(expl, c);
		
		c.insets = new Insets(0, 5, 0, 5);
		c.weightx = 0.0;
		c.weighty = 0.0;
		//c.ipadx = 50;
		c.gridy = 2;
		shuffleSet.add(wishLabel, c);
		
		c.weightx = 0.0;
		c.weighty = 0.0;
		//c.ipadx = 50;
		c.gridx = 1;
		shuffleSet.add(wish, c);
		
		c.weightx = 0.0;
		c.weighty = 0.0;
		//c.ipadx = 50;
		c.gridy = 3;
		c.gridx = 0;
		shuffleSet.add(mainLabel, c);
		
		c.weightx = 0.0;
		c.weighty = 0.0;
		//c.ipadx = 50;
		c.gridx = 1;
		shuffleSet.add(main, c);
		
		shuffleSet.setBackground(null);
				
		return shuffleSet;
	}
	
	public static int getWishPercentage()
	{
		if(wish.getValue() instanceof Integer)
			return (Integer) wish.getValue();
		return 0;
	}
	
	public static int getMainPercentage()
	{
		if(main.getValue() instanceof Integer)
			return (Integer) main.getValue();
		return 0;
	}
	
	class SpinnerListener implements ChangeListener
	{
		private final JSpinner partner;
		
		public SpinnerListener(JSpinner partner)
		{
			super();
			this.partner = partner;
		}
		
		@Override
		public void stateChanged(ChangeEvent ce)
		{
			if(ce.getSource() instanceof JSpinner)
			{
				Object value = ((JSpinner)ce.getSource()).getValue();
				if(value instanceof Integer)
					partner.setValue(100 - (Integer)value);
			}
		}
	}

}
