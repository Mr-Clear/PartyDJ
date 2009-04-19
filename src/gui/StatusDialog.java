package gui;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Calendar;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComponent;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.LayoutStyle;
import javax.swing.Timer;


public class StatusDialog extends javax.swing.JDialog implements UncaughtExceptionHandler
{
	private static final long serialVersionUID = -7585629827078152783L;
	private JLabel icon;
	private JLabel progress;
	private JButton cancelButton;
	private JProgressBar statusBar;
	private JLabel statusInfo;
	private StatusSupportedFunction initialiser;
	private final StatusDialog me = this;
	private StatusThread thread;
	
	private final long startTime = System.currentTimeMillis(); 
	private JLabel time;
	Timer showTimeTimer;

	public StatusDialog(String title, Frame frame, StatusSupportedFunction init) 
	{
		super(frame);
		initialiser = init;

		DialogListener dialogListener = new DialogListener();
		this.addWindowListener(dialogListener);
		this.addComponentListener(dialogListener);
		this.setLocationRelativeTo(frame);
		this.setResizable(true);
		this.setMaximumSize(new Dimension(Integer.MAX_VALUE, 155));
		this.setMinimumSize(new Dimension(100, 155));
		this.setTitle(title);
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		this.setModalityType(ModalityType.DOCUMENT_MODAL);
		initGUI();
		thread = new StatusThread();
		thread.setUncaughtExceptionHandler(this);
		thread.start();
		
		showTimeTimer = new Timer(100, new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				if(statusBar.getValue() > 0)
				{
					long elapsedTime = (System.currentTimeMillis() - startTime) / 1000;
					long remainingTime = elapsedTime * (statusBar.getMaximum() - statusBar.getValue()) / statusBar.getValue();
					Calendar cal = Calendar.getInstance();
					cal.add(Calendar.SECOND, (int)remainingTime);
					time.setText("Vergangen: " + common.Functions.formatTime(elapsedTime) + 
							"   Verbleibend: " + common.Functions.formatTime(remainingTime) +
							"   Fertig: " + String.format("%tT%n", cal));
							//" Fertig: " + common.Functions.formatTime((System.currentTimeMillis() / 1000 + remainingTime) % 86400));
				}
			}});
		
		showTimeTimer.start();
		this.setVisible(true);
	}
	
	private void initGUI() 
	{
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
			time = new JLabel();
			time.setText("k.A.");
			time.setHorizontalTextPosition(JLabel.RIGHT);
		}
		{
			icon = new JLabel(new ImageIcon("Resources/Settings32.gif"));
			//icon = new JLabel();
			//icon.setText("ICON");
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
			progress = new JLabel();
			progress.setText("Fortschritt...");
		}
		thisLayout.setVerticalGroup(thisLayout.createSequentialGroup()
			.addGroup(thisLayout.createParallelGroup()
			    .addGroup(GroupLayout.Alignment.LEADING, thisLayout.createSequentialGroup()
			        .addComponent(icon, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
			        .addGap(14))
			    .addGroup(GroupLayout.Alignment.LEADING, thisLayout.createSequentialGroup()
			        .addGap(12)
			        .addComponent(statusInfo, 0, 18, Short.MAX_VALUE)))
			.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
			.addComponent(statusBar, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
			.addGap(16)
			.addGroup(thisLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
			    .addComponent(cancelButton, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, 24, GroupLayout.PREFERRED_SIZE)
			    .addComponent(progress, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
			    .addComponent(time, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
			.addContainerGap(18, 18));
		thisLayout.setHorizontalGroup(thisLayout.createParallelGroup()
			.addGroup(GroupLayout.Alignment.LEADING, thisLayout.createSequentialGroup()
			    .addComponent(icon, GroupLayout.PREFERRED_SIZE, 37, GroupLayout.PREFERRED_SIZE)
			    .addGap(20)
			    .addComponent(statusInfo, 0, 549, Short.MAX_VALUE)
			    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 4, GroupLayout.PREFERRED_SIZE))
			.addGroup(thisLayout.createSequentialGroup()
			    .addPreferredGap(icon, progress, LayoutStyle.ComponentPlacement.INDENT)
			    .addGroup(thisLayout.createParallelGroup()
			        .addGroup(GroupLayout.Alignment.LEADING, thisLayout.createSequentialGroup()
			            .addComponent(progress, GroupLayout.PREFERRED_SIZE, 75, GroupLayout.PREFERRED_SIZE)
			            .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
			            .addComponent(time, 0, 414, Short.MAX_VALUE)
			            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
			            .addComponent(cancelButton, GroupLayout.PREFERRED_SIZE, 81, GroupLayout.PREFERRED_SIZE))
			        .addComponent(statusBar, GroupLayout.Alignment.LEADING, 0, 594, Short.MAX_VALUE))
			    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 4, GroupLayout.PREFERRED_SIZE)));

		this.setSize(626, 155);
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
		progress.setText((Math.round(statusBar.getPercentComplete() * 10000) / 100d) + " %");
	}
	
	class StatusThread extends Thread
	{
		public void run()
		{
			initialiser.runFunction(me);
			showTimeTimer.stop();
			dispose();
		}
	}
		
	public interface StatusSupportedFunction
	{
		public void runFunction(StatusDialog sd);
		public void stopTask();
	}

	class DialogListener implements WindowListener, ComponentListener
	{
		//WindowListener
		public void windowClosing(WindowEvent e)
		{
			initialiser.stopTask();
		}
		public void windowClosed(WindowEvent e)
		{
			initialiser.stopTask();
		}
		
		public void windowActivated(WindowEvent e){}
		public void windowDeactivated(WindowEvent e){}
		public void windowDeiconified(WindowEvent e){}
		public void windowIconified(WindowEvent e){}
		public void windowOpened(WindowEvent e){}

		//ComponentListener
		public void componentResized(ComponentEvent e)
		{
			StatusDialog dialog = (StatusDialog)e.getSource();
			dialog.setSize(dialog.getSize().width, dialog.getMaximumSize().height);
		}
		
		public void componentHidden(ComponentEvent e){}
		public void componentMoved(ComponentEvent e){}
		public void componentShown(ComponentEvent e){}
	}

	public void uncaughtException(Thread t, Throwable e)
	{
		JOptionPane.showMessageDialog(me, "Fehler aufgetreten:\n" + e.getMessage(), "Status Dialog", JOptionPane.ERROR_MESSAGE);
		dispose();
	}
}
