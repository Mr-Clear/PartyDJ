package gui;
import gui.settings.MasterList;
import java.awt.Frame;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComponent;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.LayoutStyle;
import javax.swing.SwingUtilities;

public class StatusDialog extends javax.swing.JDialog {
	private JLabel icon;
	private JButton cancelButton;
	private JProgressBar statusBar;
	private JLabel statusInfo;
	private ÖlaPalöma initialiser;
	private final StatusDialog me = this;
	private final Object object;

	public StatusDialog(String title, Frame frame, ÖlaPalöma init, Object object) 
	{
		super(frame);
		initialiser = init;
		this.object = object;
							
		this.addWindowListener(new CloseListener());
		this.setLocationRelativeTo(frame);
		this.setResizable(false);
		this.setTitle(title);
		this.setModalityType(ModalityType.DOCUMENT_MODAL);
		initGUI();
		new StatusThread().start();
		this.setVisible(true);
	}
	
	private void initGUI() 
	{
		try {
			GroupLayout thisLayout = new GroupLayout((JComponent)getContentPane());
			getContentPane().setLayout(thisLayout);
			{
				statusInfo = new JLabel();
				statusInfo.setText("Info");
			}
			{
				statusBar = new JProgressBar();
			}
			{
				cancelButton = new JButton();
				cancelButton.setText("Cancel");
				cancelButton.addMouseListener(new MouseAdapter()
				{
					public void mouseClicked(MouseEvent me) 
					{
						initialiser.stopTask();
						dispose();
					}
				});
			}
			{
				icon = new JLabel();
				icon.setIcon(new ImageIcon("Resources/Settings32.gif"));
			}
			
			thisLayout.setVerticalGroup(thisLayout.createSequentialGroup()
				.addGroup(thisLayout.createParallelGroup()
				    .addComponent(icon, GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 50, GroupLayout.PREFERRED_SIZE)
				    .addGroup(GroupLayout.Alignment.LEADING, thisLayout.createSequentialGroup()
				        .addGap(21)
				        .addComponent(statusInfo, GroupLayout.PREFERRED_SIZE, 24, GroupLayout.PREFERRED_SIZE)
				        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)))
				.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
				.addComponent(statusBar, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
				.addGap(16)
				.addComponent(cancelButton, GroupLayout.PREFERRED_SIZE, 24, GroupLayout.PREFERRED_SIZE)
				.addContainerGap(17, 17));
			thisLayout.setHorizontalGroup(thisLayout.createSequentialGroup()
				.addContainerGap()
				.addGroup(thisLayout.createParallelGroup()
				    .addGroup(GroupLayout.Alignment.LEADING, thisLayout.createSequentialGroup()
				        .addComponent(icon, GroupLayout.PREFERRED_SIZE, 62, GroupLayout.PREFERRED_SIZE)
				        .addGap(42)
				        .addGroup(thisLayout.createParallelGroup()
				            .addComponent(statusInfo, GroupLayout.Alignment.LEADING, 0, 264, Short.MAX_VALUE)
				            .addGroup(GroupLayout.Alignment.LEADING, thisLayout.createSequentialGroup()
				                .addGap(0, 183, Short.MAX_VALUE)
				                .addComponent(cancelButton, GroupLayout.PREFERRED_SIZE, 81, GroupLayout.PREFERRED_SIZE))))
				    .addComponent(statusBar, GroupLayout.Alignment.LEADING, 0, 368, Short.MAX_VALUE))
				.addContainerGap());
			this.setSize(408, 180);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void setLabel(String text)
	{
		statusInfo.setText(text);
	}
	
	public void setBarMaximum(int max)
	{
		statusBar.setMaximum(max);
	}
	
	public void setBarPosition(int position)
	{
		statusBar.setValue(position);
	}
	
	class StatusThread extends Thread
	{
		public void run()
		{
			boolean done = initialiser.runStatusDialog(me, object);
			if(done)
				dispose();
		}
	}
	
	public interface ÖlaPalöma
	{
		public boolean runStatusDialog(StatusDialog sd, Object object);
		public void stopTask();
	}

	class CloseListener implements WindowListener 
	{

		public void windowActivated(WindowEvent e)
		{
			// TODO Auto-generated method stub
			
		}

		public void windowClosed(WindowEvent e)
		{
		}

		public void windowClosing(WindowEvent e)
		{
			initialiser.stopTask();
			dispose();
		}

		public void windowDeactivated(WindowEvent e)
		{
			// TODO Auto-generated method stub
			
		}

		public void windowDeiconified(WindowEvent e)
		{
			// TODO Auto-generated method stub
			
		}

		public void windowIconified(WindowEvent e)
		{
			// TODO Auto-generated method stub
			
		}

		public void windowOpened(WindowEvent e)
		{
			// TODO Auto-generated method stub
			
		}
		
	}
}
