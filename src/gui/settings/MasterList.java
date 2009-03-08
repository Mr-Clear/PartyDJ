package gui.settings;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JPanel;

public class MasterList extends JPanel
{
	private static final long serialVersionUID = 6101715371957303072L;

	public MasterList()
	{
		super();
		
		setLayout(new BorderLayout());
		
		Box box = Box.createVerticalBox();
		JButton addFolder = new JButton("Verzeichnis einfügen.");
		addFolder.addActionListener(new ActionListener(){
						public void actionPerformed(ActionEvent arg0)
						{
							// TODO Auto-generated method stub
						}});
		box.add(addFolder);
		JButton addFile = new JButton("Datei einfügen.");
		addFolder.addActionListener(new ActionListener(){
						public void actionPerformed(ActionEvent arg0)
						{
							// TODO Auto-generated method stub
						}});
		box.add(addFile);
		
		/*
		Box.createVerticalGlue();
		
		JButton removeFile = new JButton("Tracks entfernen.");
		addFolder.addActionListener(new ActionListener(){
						public void actionPerformed(ActionEvent arg0)
						{
							// TODO Auto-generated method stub
						}});
		box.add(removeFile);
		*/
		
		add(box);		
	}
}
