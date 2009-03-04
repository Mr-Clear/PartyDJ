package gui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;
import basics.Controller;
import common.ListException;
import common.PlayerException;
import common.Track;
import common.Track.Problem;

public class EditTrackWindow extends JDialog
{
	private static final long serialVersionUID = -2570515444243311682L;
	
	private Track myTrack;
	private JTextField txtPath;
	private JTextField txtName;
	private JComboBox cmbProblem;
	private JTextField txtDuration;
	private JTextField txtSize;
	private JTextArea txtarInfo;
	
	private double duration;
	private long size;
	
	EditTrackWindow(Track track)
	{
		myTrack = track;
		setTitle(track.name);
		
		Insets insets = new Insets(4, 5, 4, 6);
		
		GridBagLayout layout = new GridBagLayout();
		setLayout(layout);
		
		GridBagConstraints c;
		
		//---Pfad---
		JLabel lblPfad = new JLabel("Pfad:");
		c = new GridBagConstraints();
		c.insets = insets;
		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.EAST;
		add(lblPfad, c);
		
		txtPath = new JTextField(track.path);
		c = new GridBagConstraints();
		c.insets = insets;
		c.gridx = 1;
		c.gridy = 0;
		c.gridwidth = 2;
		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1d;
		add(txtPath, c);
		
		//---Name---
		JLabel lblName = new JLabel("Name:");
		c = new GridBagConstraints();
		c.insets = insets;
		c.gridx = 0;
		c.gridy = 1;
		c.anchor = GridBagConstraints.EAST;
		add(lblName, c);
		
		txtName = new JTextField(track.name);
		c = new GridBagConstraints();
		c.insets = insets;
		c.gridx = 1;
		c.gridy = 1;
		c.gridwidth = 2;
		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1d;
		add(txtName, c);
		
		//---Problem---
		JLabel lblProblem = new JLabel("Problem:");
		c = new GridBagConstraints();
		c.insets = insets;
		c.gridx = 0;
		c.gridy = 2;
		c.anchor = GridBagConstraints.EAST;
		add(lblProblem, c);
		
		cmbProblem = new JComboBox(Track.Problem.getStringArray());
		cmbProblem.setSelectedItem(Track.Problem.problemToArrayIndex(track.problem));
		c = new GridBagConstraints();
		c.insets = insets;
		c.gridx = 1;
		c.gridy = 2;
		c.gridwidth = 2;
		c.anchor = GridBagConstraints.WEST;
		add(cmbProblem, c);
		
		//---Dauer---
		JLabel lblDuration = new JLabel("Dauer:");
		c = new GridBagConstraints();
		c.insets = insets;
		c.gridx = 0;
		c.gridy = 3;
		c.anchor = GridBagConstraints.EAST;
		add(lblDuration, c);
		
		txtDuration = new JTextField(track.name);
		txtDuration.setText(common.Functions.formatTime(track.duration));
		txtDuration.setEditable(false);
		txtDuration.setBorder(null);
		c = new GridBagConstraints();
		c.insets = insets;
		c.gridx = 1;
		c.gridy = 3;
		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1d;
		add(txtDuration, c);
		
		duration = track.duration;
		
		JButton btnDuration = new JButton("Dauer einlesen");
		c = new GridBagConstraints();
		c.insets = insets;
		c.gridx = 2;
		c.gridy = 3;
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.WEST;
		add(btnDuration, c);
		btnDuration.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0)
			{
				try
				{
					duration = Controller.instance.player.getDuration(myTrack);
					txtDuration.setText(common.Functions.formatTime(duration));
				}
				catch (PlayerException e)
				{
					JOptionPane.showMessageDialog(null, e.getMessage(), "PartyDJ", JOptionPane.ERROR_MESSAGE);
					cmbProblem.setSelectedIndex(Track.Problem.problemToArrayIndex(e.problem));
				}
			}});
		
		//---Größe---
		JLabel lblSize = new JLabel("Größe:");
		c = new GridBagConstraints();
		c.insets = insets;
		c.gridx = 0;
		c.gridy = 4;
		c.anchor = GridBagConstraints.EAST;
		add(lblSize, c);
		
		txtSize = new JTextField(track.name);
		txtSize.setText(common.Functions.formatSize(track.size, 4, true));
		txtSize.setEditable(false);
		txtSize.setBorder(null);
		c = new GridBagConstraints();
		c.insets = insets;
		c.gridx = 1;
		c.gridy = 4;
		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1d;
		add(txtSize, c);
		
		duration = track.duration;
		
		JButton btnSize = new JButton("Größe einlesen");
		c = new GridBagConstraints();
		c.insets = insets;
		c.gridx = 2;
		c.gridy = 4;
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.WEST;
		add(btnSize, c);
		btnSize.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0)
			{
				size = new File(myTrack.path).length();
				txtSize.setText(common.Functions.formatSize(size, 4, true));
			}});
		
		//---Info---
		JLabel lblInfo = new JLabel("Zusätzliche Info:");
		c = new GridBagConstraints();
		c.insets = insets;
		c.gridx = 0;
		c.gridy = 5;
		c.anchor = GridBagConstraints.NORTHEAST;
		add(lblInfo, c);
		
		txtarInfo = new JTextArea(track.info);
		txtarInfo.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		c = new GridBagConstraints();
		c.insets = insets;
		c.gridx = 1;
		c.gridy = 5;
		c.gridwidth = 2;
		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1d;
		c.weighty = 1d;
		add(txtarInfo, c);
		
		//---Buttons---
		JButton btnCancel = new JButton("Abbrechen");
		c = new GridBagConstraints();
		c.insets = insets;
		c.gridx = 1;
		c.gridy = 6;
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;
		add(btnCancel, c);
		btnCancel.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0)
			{
				dispose();
			}});
		
		JButton btnOk = new JButton("Übernehmen");
		c = new GridBagConstraints();
		c.insets = insets;
		c.gridx = 2;
		c.gridy = 6;
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.WEST;
		add(btnOk, c);
		btnOk.addActionListener(new BtnOkActionListener());

		
		setModal(true);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setSize(new Dimension(700, 300));
		//setResizable(false);
		setVisible(true);
	}
	
	class BtnOkActionListener implements ActionListener
	{
		public void actionPerformed(ActionEvent arg0)
		{
			int changeCount = 0;
			Track.TrackElement change;
			if(myTrack.path != txtPath.getText())
			{
				if(!new File(myTrack.path).exists())
				{
					if(JOptionPane.NO_OPTION == JOptionPane.showConfirmDialog(null, "Angegebene Datei existiert nicht.\nTrotzdem ändern?", "PartyDJ", JOptionPane.YES_NO_OPTION))
						return;
				}
				changeCount++;
				myTrack.path = txtPath.getText();
				change = Track.TrackElement.PATH;
			}
			if(myTrack.name != txtName.getText())
			{
				changeCount++;
				myTrack.name = txtName.getText();
				change = Track.TrackElement.NAME;
			}
			if(myTrack.problem != Problem.arrayIndexToProblem(cmbProblem.getSelectedIndex()))
			{
				changeCount++;
				myTrack.problem = Problem.arrayIndexToProblem(cmbProblem.getSelectedIndex());
				change = Track.TrackElement.PROBLEM;
			}
			if(myTrack.duration != duration)
			{
				changeCount++;
				myTrack.name = txtName.getText();
				change = Track.TrackElement.NAME;
			}
			if(myTrack.size != size)
			{
				changeCount++;
				myTrack.size = size;
				change = Track.TrackElement.SIZE;
			}
			if(myTrack.info != txtarInfo.getText())
			{
				changeCount++;
				myTrack.info = txtarInfo.getText();
				change = Track.TrackElement.INFO;
			}
						
			if(changeCount == 1)
			{
				myTrack.name = txtName.getText();
				try
				{
					Controller.instance.data.updateTrack(myTrack, Track.TrackElement.NAME);
				}
				catch (ListException e)
				{
					e.printStackTrace();
				}
			}
		}
	}
}
