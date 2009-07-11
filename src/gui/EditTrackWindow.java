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
import players.PlayerException;
import basics.Controller;
import common.Track;
import common.Track.Problem;

/**
 * Kleiner Dialog über den die Eigenschaften eines AudioTracks angezeigt
 * und bearbeitet werden können.
 * 
 * @author Eraser
 * 
 * @see Track
 */
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
	
	public EditTrackWindow(Track track)
	{
		myTrack = track;
		setTitle(track.getName());
		duration = track.getDuration();
		size = track.getSize();
		
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
		
		txtPath = new JTextField(track.getPath());
		txtPath.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e)
			{
				String name = txtPath.getText().substring(txtPath.getText().lastIndexOf("\\") + 1, txtPath.getText().lastIndexOf("."));
				txtName.setText(name);
			}});
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
		
		txtName = new JTextField(track.getName());
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
		
		cmbProblem = new JComboBox(Problem.getStringArray());
		cmbProblem.setSelectedIndex(Problem.problemToArrayIndex(track.getProblem()));
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
		
		txtDuration = new JTextField(track.getName());
		txtDuration.setText(common.Functions.formatTime(track.getDuration()));
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
		
		duration = track.getDuration();
		
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
					duration = Controller.getInstance().getPlayer().getDuration(myTrack);
				}
				catch (PlayerException e)
				{
					JOptionPane.showMessageDialog(null, e.getMessage(), "PartyDJ", JOptionPane.ERROR_MESSAGE);
					cmbProblem.setSelectedIndex(Problem.problemToArrayIndex(e.problem));
					duration = 0;
				}
				txtDuration.setText(common.Functions.formatTime(duration));
			}});
		
		//---Größe---
		JLabel lblSize = new JLabel("Größe:");
		c = new GridBagConstraints();
		c.insets = insets;
		c.gridx = 0;
		c.gridy = 4;
		c.anchor = GridBagConstraints.EAST;
		add(lblSize, c);
		
		txtSize = new JTextField(track.getName());
		txtSize.setText(common.Functions.formatSize(track.getSize(), 4, true));
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
		
		duration = track.getDuration();
		
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
				File file = new File(myTrack.getPath());
				if(file.exists())
					size = new File(myTrack.getPath()).length();
				else
				{
					size = 0;
					cmbProblem.setSelectedIndex(Problem.problemToArrayIndex(Problem.FILE_NOT_FOUND));
				}
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
		
		txtarInfo = new JTextArea(track.getInfo());
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
		JButton btnCancel = new JButton("Schließen");
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
			myTrack.setName(txtName.getText());

			if(!myTrack.getPath().equals(txtPath.getText()))
			{
				if(!new File(txtPath.getText()).exists())
				{
					if(JOptionPane.NO_OPTION == JOptionPane.showConfirmDialog(null, "Datei wirklich umbennen bzw. verschieben?", "PartyDJ", JOptionPane.YES_NO_OPTION))
						return;
				}

				new File(myTrack.getPath()).renameTo(new File(txtPath.getText()));
				myTrack.setPath(txtPath.getText());
			}

			myTrack.setProblem(Problem.arrayIndexToProblem(cmbProblem.getSelectedIndex()));
			myTrack.setDuration(duration);
			myTrack.setSize(size);
			myTrack.setInfo(txtarInfo.getText());
		}
	}
}
