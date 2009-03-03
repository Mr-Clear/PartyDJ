package gui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;
import basics.Controller;
import common.ListException;
import common.Track;

public class EditTrackWindow extends JDialog
{
	private static final long serialVersionUID = -2570515444243311682L;
	
	private Track track;
	private JTextField txtPath;
	private JTextField txtName;
	
	EditTrackWindow(Track track)
	{
		this.track = track;
		
		GridBagLayout layout = new GridBagLayout();
		setLayout(layout);
		
		GridBagConstraints c;
		
		JLabel lblPfad = new JLabel("Pfad:");
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.EAST;
		add(lblPfad, c);
		
		txtPath = new JTextField(track.path);
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1d;
		add(txtPath, c);
		
		JLabel lblName = new JLabel("Name:");
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 1;
		c.anchor = GridBagConstraints.EAST;
		add(lblName, c);
		
		txtName = new JTextField(track.name);
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = 1;
		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1d;
		add(txtName, c);
		
		JButton btnCancel = new JButton("Abbrechen");
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 2;
		c.fill = GridBagConstraints.NONE;
		add(btnCancel, c);
		
		JButton btnOk = new JButton("Übernehmen");
		btnOk.addActionListener(new okActionListener());
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = 2;
		c.fill = GridBagConstraints.NONE;
		add(btnOk, c);

		
		setModal(true);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setSize(new Dimension(400,200));
		//setResizable(false);
		setVisible(true);
	}
	
	private class okActionListener implements ActionListener
	{
		public void actionPerformed(ActionEvent arg0)
		{
			if(txtName.getText() != track.name)
			{
				track.name = txtName.getText();
				try
				{
					Controller.instance.data.updateTrack(track, Track.TrackElement.NAME);
				}
				catch (ListException e)
				{
					e.printStackTrace();
				}
			}
		}
	}
}
