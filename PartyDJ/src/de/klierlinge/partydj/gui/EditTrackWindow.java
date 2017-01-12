package de.klierlinge.partydj.gui;

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
import de.klierlinge.partydj.basics.Controller;
import de.klierlinge.partydj.common.Track;
import de.klierlinge.partydj.common.Track.Problem;
import de.klierlinge.partydj.players.PlayerException;

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
	
	private final Track myTrack;
	private final JTextField txtPath;
	private final JTextField txtName;
	private final JComboBox<String> cmbProblem;
	private final JTextField txtDuration;
	private final JTextField txtSize;
	private final JTextArea txtarInfo;
	
	private double duration;
	private long size;
	
	public EditTrackWindow(final Track track)
	{
		myTrack = track;
		setTitle(track.getName());
		duration = track.getDuration();
		size = track.getSize();
		
		final Insets insets = new Insets(4, 5, 4, 6);
		
		final GridBagLayout layout = new GridBagLayout();
		setLayout(layout);
		
		GridBagConstraints c;
		
		//---Pfad---
		final JLabel lblPfad = new JLabel("Pfad:");
		c = new GridBagConstraints();
		c.insets = insets;
		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.EAST;
		add(lblPfad, c);
		
		txtPath = new JTextField(track.getPath());
		txtPath.addActionListener(new ActionListener()
		{
			@Override public void actionPerformed(final ActionEvent e)
			{
				final String name = txtPath.getText().substring(txtPath.getText().lastIndexOf("\\") + 1, txtPath.getText().lastIndexOf("."));
				txtName.setText(name);
			}
		});
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
		final JLabel lblName = new JLabel("Name:");
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
		final JLabel lblProblem = new JLabel("Problem:");
		c = new GridBagConstraints();
		c.insets = insets;
		c.gridx = 0;
		c.gridy = 2;
		c.anchor = GridBagConstraints.EAST;
		add(lblProblem, c);
		
		cmbProblem = new JComboBox<>(Problem.getStringArray());
		cmbProblem.setSelectedIndex(Problem.problemToArrayIndex(track.getProblem()));
		c = new GridBagConstraints();
		c.insets = insets;
		c.gridx = 1;
		c.gridy = 2;
		c.gridwidth = 2;
		c.anchor = GridBagConstraints.WEST;
		add(cmbProblem, c);
		
		//---Dauer---
		final JLabel lblDuration = new JLabel("Dauer:");
		c = new GridBagConstraints();
		c.insets = insets;
		c.gridx = 0;
		c.gridy = 3;
		c.anchor = GridBagConstraints.EAST;
		add(lblDuration, c);
		
		txtDuration = new JTextField(track.getName());
		txtDuration.setText(de.klierlinge.utils.Functions.formatTime(track.getDuration()));
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
		
		final JButton btnDuration = new JButton("Dauer einlesen");
		c = new GridBagConstraints();
		c.insets = insets;
		c.gridx = 2;
		c.gridy = 3;
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.WEST;
		add(btnDuration, c);
		btnDuration.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(final ActionEvent arg0)
			{
				try
				{
					duration = Controller.getInstance().getPlayer().getDuration(myTrack);
				}
				catch (final PlayerException e)
				{
					JOptionPane.showMessageDialog(null, e.getMessage(), "PartyDJ", JOptionPane.ERROR_MESSAGE);
					cmbProblem.setSelectedIndex(Problem.problemToArrayIndex(e.getProblem()));
					duration = 0;
				}
				txtDuration.setText(de.klierlinge.utils.Functions.formatTime(duration));
			}
		});
		
		//---Größe---
		final JLabel lblSize = new JLabel("Größe:");
		c = new GridBagConstraints();
		c.insets = insets;
		c.gridx = 0;
		c.gridy = 4;
		c.anchor = GridBagConstraints.EAST;
		add(lblSize, c);
		
		txtSize = new JTextField(track.getName());
		txtSize.setText(de.klierlinge.utils.Functions.formatSize(track.getSize(), 4, true));
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
		
		final JButton btnSize = new JButton("Größe einlesen");
		c = new GridBagConstraints();
		c.insets = insets;
		c.gridx = 2;
		c.gridy = 4;
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.WEST;
		add(btnSize, c);
		btnSize.addActionListener(new ActionListener()
		{
			@Override public void actionPerformed(final ActionEvent arg0)
			{
				final File file = new File(myTrack.getPath());
				if(file.exists())
					size = new File(myTrack.getPath()).length();
				else
				{
					size = 0;
					cmbProblem.setSelectedIndex(Problem.problemToArrayIndex(Problem.FILE_NOT_FOUND));
				}
				txtSize.setText(de.klierlinge.utils.Functions.formatSize(size, 4, true));
			}
		});
		
		//---Info---
		final JLabel lblInfo = new JLabel("Zusätzliche Info:");
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
		final JButton btnCancel = new JButton("Schließen");
		c = new GridBagConstraints();
		c.insets = insets;
		c.gridx = 1;
		c.gridy = 6;
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;
		add(btnCancel, c);
		btnCancel.addActionListener(new ActionListener()
		{
			@Override public void actionPerformed(final ActionEvent arg0)
			{
				dispose();
			}
		});
		
		final JButton btnOk = new JButton("Übernehmen");
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
		@Override
		public void actionPerformed(final ActionEvent arg0)
		{
			myTrack.setName(txtName.getText());

			if(!myTrack.getPath().equals(txtPath.getText()))
			{
				if(myTrack.equals(Controller.getInstance().getPlayer().getCurrentTrack()))
				{
					JOptionPane.showMessageDialog(EditTrackWindow.this, "Datei kann nicht umbenannt oder verschoben werde, wenn sie gespielt wird!");
					return;
				}
				
				if(!new File(txtPath.getText()).exists())
				{
					if(JOptionPane.NO_OPTION == JOptionPane.showConfirmDialog(null, "Datei wirklich umbennen bzw. verschieben?", "PartyDJ", JOptionPane.YES_NO_OPTION))
					{
						txtPath.setText(myTrack.getPath());
						final String name = myTrack.getPath().substring(myTrack.getPath().lastIndexOf("\\") + 1, myTrack.getPath().lastIndexOf("."));
						txtName.setText(name);
					}
				}

				if(new File(myTrack.getPath()).renameTo(new File(txtPath.getText())))
					myTrack.setPath(txtPath.getText());
			}

			myTrack.setProblem(Problem.arrayIndexToProblem(cmbProblem.getSelectedIndex()));
			myTrack.setDuration(duration);
			myTrack.setSize(size);
			myTrack.setInfo(txtarInfo.getText());
		}
	}
}
