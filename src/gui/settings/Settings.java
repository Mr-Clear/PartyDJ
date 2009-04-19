package gui.settings;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import lists.ListException;
import basics.Controller;

public class Settings  extends JPanel
{
	private static final long serialVersionUID = -6987892133947674516L;
	
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
		
		con.gridy = 1;
		con.ipadx = GridBagConstraints.REMAINDER;
		add(listShuffler(), con);
		
		this.setVisible(true);
	}
	
	private JPanel shuffleSetting()
	{
		GridBagConstraints c = new GridBagConstraints();
		JPanel shuffleSet = new JPanel(new GridBagLayout());
		
		JLabel titel = new JLabel("Shuffle");
		JLabel expl = new JLabel("Hier können Sie einstellen, wie groß die Wahrscheinlichkeit ist, dass ein Lied von einer bestimmten Liste gespielt wird.");
		
		titel.setFont(new Font("Serif", Font.BOLD, 18));
		expl.setFont(new Font("Serif", Font.ITALIC, 12));
		
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
		
		shuffleSet.setBackground(null);
				
		return shuffleSet;
	}
	
	private JScrollPane listShuffler()
	{
		try
		{
			List<String> listNames = Controller.getInstance().getData().getLists();
			String[] labels = new String[]{"Liste", "Priorität", "Spielwahrscheinlichkeit"};
			String[][] lists = new String[listNames.size()][3];
			JTable listTable = new JTable(lists, labels);
			int skipped = 0;
			
			listTable.setFillsViewportHeight(true);

			listTable.setValueAt("Hauptliste", 0, 0);
			for(int i = 1; i < listNames.size(); i++)
			{
				String list = listNames.get(i);
				if(list != "lastPlayed")
				{
					listTable.setValueAt(list , i - skipped, 0);
				}
				else
					skipped++;
					
			}

			JScrollPane scroll = new JScrollPane(listTable);
			scroll.setVisible(true);
			return scroll;
		}
		catch (ListException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return new JScrollPane();
	}
	
	class SpinnerListener implements ChangeListener
	{
		@Override
		public void stateChanged(ChangeEvent ce)
		{
			if(ce.getSource() instanceof JSpinner)
			{
				
			}
		}
	}

}
