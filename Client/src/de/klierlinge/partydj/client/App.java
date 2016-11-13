package de.klierlinge.partydj.client;

import java.awt.EventQueue;
import java.io.IOException;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingUtilities;
import de.klierlinge.partydj.pjr.beans.DataRequest;
import de.klierlinge.partydj.pjr.beans.InitialData;
import de.klierlinge.partydj.pjr.beans.Message;
import de.klierlinge.partydj.pjr.beans.PdjCommand;
import de.klierlinge.partydj.pjr.beans.PdjCommand.Command;
import de.klierlinge.partydj.pjr.client.Client;
import de.klierlinge.partydj.pjr.client.ClientConnection;

public class App implements Client
{
	private JFrame frame;
	private JLabel lblTitle;
	ClientConnection connection;

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
		frame.setBounds(100, 100, 629, 510);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		lblTitle = new JLabel("Title");

		JButton btnBack = new JButton("Back");
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

		JButton btnStop = new JButton("Stop");
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

		JButton btnPlay = new JButton("Play");
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

		JButton btnNext = new JButton("Next");
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
		GroupLayout groupLayout = new GroupLayout(frame.getContentPane());
		groupLayout.setHorizontalGroup(groupLayout.createParallelGroup(Alignment.LEADING).addGroup(groupLayout.createSequentialGroup().addContainerGap().addGroup(groupLayout.createParallelGroup(Alignment.LEADING).addComponent(lblTitle).addGroup(groupLayout.createSequentialGroup().addComponent(btnBack).addPreferredGap(ComponentPlacement.RELATED).addComponent(btnStop).addPreferredGap(ComponentPlacement.RELATED).addComponent(btnPlay).addPreferredGap(ComponentPlacement.RELATED).addComponent(btnNext))).addContainerGap(333, Short.MAX_VALUE)));
		groupLayout.setVerticalGroup(groupLayout.createParallelGroup(Alignment.LEADING).addGroup(groupLayout.createSequentialGroup().addContainerGap().addGroup(groupLayout.createParallelGroup(Alignment.BASELINE).addComponent(btnBack).addComponent(btnStop).addComponent(btnPlay).addComponent(btnNext)).addPreferredGap(ComponentPlacement.RELATED).addComponent(lblTitle).addContainerGap(418, Short.MAX_VALUE)));
		frame.getContentPane().setLayout(groupLayout);
	}

	@Override
	public void messageReceived(Message message)
	{
		switch (message.getType())
		{
		case InitialData:
			System.out.println(((InitialData)message).getTracks().size());
			break;
		default:
			System.out.println("Unhandled message: " + message);
		}
	}

	@Override
	public void connectionOpened()
	{
		SwingUtilities.invokeLater(() -> lblTitle.setText("Connection open..."));
		try
		{
			connection.send(new DataRequest());
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void connectionClosed(boolean externalReason)
	{
		SwingUtilities.invokeLater(() -> lblTitle.setText("Connection closed"));
	}
}
