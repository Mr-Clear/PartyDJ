package de.klierlinge.partydj.client;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.io.IOException;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import de.klierlinge.partydj.pjr.beans.LiveData;
import de.klierlinge.partydj.pjr.beans.Message;
import de.klierlinge.partydj.pjr.beans.PdjCommand;
import de.klierlinge.partydj.pjr.beans.PdjCommand.Command;
import de.klierlinge.partydj.pjr.client.Client;
import de.klierlinge.partydj.pjr.client.ClientConnection;
import de.klierlinge.utils.Functions;

public class App implements Client
{
	private JFrame frame;
	private JLabel lblTitle;
	ClientConnection connection;
	private JLabel lblElapsed;
	private JProgressBar progressBar;
	private JLabel lblRemaining;
	private JLabel lblDuration;
	private JButton btnStop;
	private JButton btnPlay;
	private JButton btnBack;
	private JButton btnNext;

	/**
	 * Launch the application.
	 * @param args Ignored
	 */
	public static void main(String[] args)
	{
		EventQueue.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					App window = new App();
					window.frame.setVisible(true);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public App()
	{
		initialize();
		connectionClosed(false);
		connection = new ClientConnection(this);
		connection.connect();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize()
	{
		frame = new JFrame("Party DJ Remote");
		frame.setBounds(100, 100, 629, 153);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		lblTitle = new JLabel("Title");

		btnBack = new JButton("Back");
		btnBack.addActionListener((a) -> {
			try
			{
				connection.send(new PdjCommand(Command.Previous));
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});

		btnStop = new JButton("Stop");
		btnStop.addActionListener((a) -> {
			try
			{
				connection.send(new PdjCommand(Command.Stop));
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});

		btnPlay = new JButton("Play");
		btnPlay.addActionListener((a) -> {
			try
			{
				connection.send(new PdjCommand(Command.Play));
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});

		btnNext = new JButton("Next");
		btnNext.addActionListener((a) -> {
			try
			{
				connection.send(new PdjCommand(Command.Next));
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});

		progressBar = new JProgressBar();

		JPanel panelTimes = new JPanel();
		GroupLayout groupLayout = new GroupLayout(frame.getContentPane());
		groupLayout.setHorizontalGroup(groupLayout.createParallelGroup(Alignment.LEADING).addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup().addContainerGap().addGroup(groupLayout.createParallelGroup(Alignment.TRAILING).addComponent(panelTimes, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 593, Short.MAX_VALUE).addComponent(lblTitle, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 593, Short.MAX_VALUE).addGroup(Alignment.LEADING, groupLayout.createSequentialGroup().addComponent(btnBack).addPreferredGap(ComponentPlacement.RELATED).addComponent(btnStop).addPreferredGap(ComponentPlacement.RELATED).addComponent(btnPlay).addPreferredGap(ComponentPlacement.RELATED).addComponent(btnNext)).addComponent(progressBar, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 593, Short.MAX_VALUE)).addContainerGap()));
		groupLayout.setVerticalGroup(groupLayout.createParallelGroup(Alignment.LEADING).addGroup(groupLayout.createSequentialGroup().addContainerGap().addGroup(groupLayout.createParallelGroup(Alignment.BASELINE).addComponent(btnBack).addComponent(btnStop).addComponent(btnPlay).addComponent(btnNext)).addPreferredGap(ComponentPlacement.RELATED).addComponent(lblTitle).addPreferredGap(ComponentPlacement.RELATED).addComponent(progressBar, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE).addPreferredGap(ComponentPlacement.RELATED).addComponent(panelTimes, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE).addContainerGap(392, Short.MAX_VALUE)));
		panelTimes.setLayout(new BorderLayout(0, 0));

		lblElapsed = new JLabel("0:00");
		panelTimes.add(lblElapsed, BorderLayout.WEST);

		lblRemaining = new JLabel("0:00");
		panelTimes.add(lblRemaining, BorderLayout.EAST);

		lblDuration = new JLabel("0:00");
		lblDuration.setHorizontalAlignment(SwingConstants.CENTER);
		panelTimes.add(lblDuration, BorderLayout.CENTER);
		frame.getContentPane().setLayout(groupLayout);
	}

	@Override
	public void messageReceived(Message message)
	{
		switch (message.getType())
		{
		case LiveData:
			final LiveData data = (LiveData)message;
			SwingUtilities.invokeLater(() -> {
				lblTitle.setText(data.track.name);
				progressBar.setMaximum((int)(data.track.duration * 100));
				progressBar.setValue((int)(data.position * 100));
				lblElapsed.setText(Functions.formatTime(data.position));
				lblRemaining.setText(Functions.formatTime(data.track.duration - data.position));
				lblDuration.setText(Functions.formatTime(data.track.duration));
				btnStop.setEnabled(data.playing);
				btnPlay.setEnabled(!data.playing);
			});
			break;
		default:
			System.out.println("Unhandled message: " + message);
		}
	}

	@Override
	public void connectionOpened()
	{
		SwingUtilities.invokeLater(() -> {
			lblTitle.setText("Connection open...");
			btnBack.setEnabled(true);
			btnNext.setEnabled(true);
		});
		//		try
		//		{
		//			connection.send(new DataRequest());
		//		}
		//		catch (IOException e)
		//		{
		//			// TODO Auto-generated catch block
		//			e.printStackTrace();
		//		}
	}

	@Override
	public void connectionClosed(boolean externalReason)
	{
		SwingUtilities.invokeLater(() -> {
			lblTitle.setText("Connection closed");
			btnBack.setEnabled(false);
			btnStop.setEnabled(false);
			btnPlay.setEnabled(false);
			btnNext.setEnabled(false);
		});
	}
}
