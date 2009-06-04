package gui;
import java.awt.Dimension;
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

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.LayoutStyle;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

/**
 * Führt eine Aufgabe vom Typ StatusSupportedFunction aus,
 * und zeigt den Fortschritt der Aufgabe an.
 * 
 * @author Sam, Eraser
 * 
 * @see StatusSupportedFunction
 */
public class StatusDialog extends javax.swing.JDialog implements UncaughtExceptionHandler
{
	private static final long serialVersionUID = -7585629827078152783L;
	private JLabel icon;
	private JLabel progress;
	private JButton cancelButton;
	private JProgressBar statusBar;
	private JLabel statusInfo;
	private StatusSupportedFunction initialiser;
	private StatusThread thread;
	long elapsedTime;
	
	private final long startTime = System.currentTimeMillis(); 
	private JLabel time;
	protected Timer showTimeTimer;

	public StatusDialog(final String title, final JFrame owner, final StatusSupportedFunction init) 
	{
		super(owner);
		SwingUtilities.invokeLater(new Runnable(){
			@Override
			public void run()
			{
				initialiser = init;

				DialogListener dialogListener = new DialogListener();
				StatusDialog.this.addWindowListener(dialogListener);
				StatusDialog.this.addComponentListener(dialogListener);
				StatusDialog.this.setLocationRelativeTo(owner);
				StatusDialog.this.setResizable(true);
				StatusDialog.this.setMaximumSize(new Dimension(Integer.MAX_VALUE, 155));
				StatusDialog.this.setMinimumSize(new Dimension(100, 155));
				StatusDialog.this.setTitle(title);
				StatusDialog.this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
				StatusDialog.this.setModalityType(ModalityType.DOCUMENT_MODAL);
				initGUI();
				thread = new StatusThread();
				//thread.setUncaughtExceptionHandler(this);
				thread.start();
				
				showTimeTimer = new Timer(100, new ActionListener(){
					public void actionPerformed(ActionEvent e)
					{
						if(statusBar.getValue() > 0)
						{
							elapsedTime = (System.currentTimeMillis() - startTime) / 1000;
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
				StatusDialog.this.setVisible(true);
			}});
	}
	
	private void initGUI() 
	{
		GroupLayout thisLayout = new GroupLayout(getContentPane());
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
//			time.setText("k.A.");
			time.setHorizontalTextPosition(SwingConstants.RIGHT);
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
				@Override
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
	
	/**ThreadSafe
	 * 
	 * @param text
	 */
	public void setLabel(final String text)
	{
		SwingUtilities.invokeLater(new Runnable(){
			@Override
			public void run()
			{
				statusInfo.setText(text);
			}});
		
	}
	
	/**ThreadSafe
	 * 
	 * @param max
	 */
	public void setBarMaximum(final int max)
	{
		SwingUtilities.invokeLater(new Runnable(){
			@Override
			public void run()
			{
				statusBar.setMaximum(max);
			}});
		
	}
	
	/**
	 * ThreadSafe
	 * @param position Neue Position der StatusBar.
	 */
	public void setBarPosition(final int position)
	{
		SwingUtilities.invokeLater(new Runnable(){
			@Override
			public void run()
			{
				statusBar.setValue(position);
				progress.setText((Math.round(statusBar.getPercentComplete() * 10000) / 100d) + " %");
			}});
		
	}
	
	/**
	 * Stoppt den Timer des StatusDialoges.
	 */
	public void stopTimer()
	{
		showTimeTimer.stop();
		time.setText("Vergangen: " + common.Functions.formatTime(elapsedTime));
	}
	
	class StatusThread extends Thread
	{
		@Override
		public void run()
		{
			initialiser.runFunction(StatusDialog.this);
			SwingUtilities.invokeLater(new Runnable(){
				@Override
				public void run()
				{
					dispose();
				}});
		}
	}
	
	/**
	 * Funktionen die mit StatusDialog ausgegeben werden, müssen in Klassen sein,
	 * sie von diesem Interface abgeleitet sind.
	 * 
	 * @author Eraser
	 * 
	 * @see StatusDialog
	 */
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
			showTimeTimer.stop();
		}
		public void windowClosed(WindowEvent e)
		{
			//Entfernt wegen Bug mit öffnen-Dialog.
			//initialiser.stopTask();
		}
		
		public void windowActivated(WindowEvent e){}
		public void windowDeactivated(WindowEvent e){}
		public void windowDeiconified(WindowEvent e){}
		public void windowIconified(WindowEvent e){}
		public void windowOpened(WindowEvent e){}

		//ComponentListener
		public void componentResized(final ComponentEvent e)
		{
			SwingUtilities.invokeLater(new Runnable(){
				@Override
				public void run()
				{
					StatusDialog dialog = (StatusDialog)e.getSource();
					dialog.setSize(dialog.getSize().width, dialog.getMaximumSize().height);
				}});
			
		}
		
		public void componentHidden(ComponentEvent e){}
		public void componentMoved(ComponentEvent e){}
		public void componentShown(ComponentEvent e){}
	}

	public void uncaughtException(Thread t, Throwable e)
	{
		JOptionPane.showMessageDialog(StatusDialog.this, "Fehler in " + t + " aufgetreten:\n" + e.getMessage(), "Status Dialog", JOptionPane.ERROR_MESSAGE);
		dispose();
	}
}
