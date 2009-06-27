package gui.settings;

import gui.ClassicWindow;
import gui.ListWindow;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.SwingUtilities;

import javax.swing.LayoutStyle;
import lists.ListException;
import basics.Controller;

/**
 * Debug Hilfe.
 * Bietet Platz für verschiedene Einstellungen.
 * 
 * @author Eraser
 * 
 * @see SettingWindow
 */
public class Stuff extends javax.swing.JPanel {
	private static final long serialVersionUID = 5155888057108134828L;
	private JButton btnDerbyDebug;
	private JButton btnWacken;
	private JButton btnClassicWindow;
	
	protected final Controller controller = Controller.getInstance();

	public Stuff() {
		super();

	SwingUtilities.invokeLater(new Runnable(){
		@Override
		public void run()
		{
			initGUI();
		}});
		
	}
	
	private void initGUI()
	{
		GroupLayout thisLayout = new GroupLayout(this);
		this.setLayout(thisLayout);
		setPreferredSize(new Dimension(400, 300));
		{
			btnDerbyDebug = new JButton();
			btnDerbyDebug.setText("Datenbank Debug-Fenster ...");
			btnDerbyDebug.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e)
				{
					controller.registerWindow(new data.derby.DebugWindow());
				}});
		}
		{
			btnClassicWindow = new JButton();
			btnClassicWindow.setText("Classic Window");
			btnClassicWindow.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					controller.registerWindow(new ClassicWindow());
				}
			});
		}
		{
			btnWacken = new JButton();
			btnWacken.setText("Wacken Liste");
			btnWacken.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					try
					{
						Controller.getInstance().registerWindow(new ListWindow("Wacken"));
					}
					catch (ListException e)
					{
						controller.logError(Controller.NORMAL_ERROR, this, e, "Liste konnte nicht erstellt werden.");
					}
				}
			});
		}
		thisLayout.setVerticalGroup(thisLayout.createSequentialGroup()
			.addContainerGap()
			.addComponent(btnDerbyDebug, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
			.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
			.addComponent(btnClassicWindow, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
			.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
			.addComponent(btnWacken, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
			.addContainerGap(200, Short.MAX_VALUE));
		thisLayout.setHorizontalGroup(thisLayout.createSequentialGroup()
			.addContainerGap()
			.addGroup(thisLayout.createParallelGroup()
			    .addGroup(thisLayout.createSequentialGroup()
			        .addComponent(btnDerbyDebug, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
			        .addGap(0, 0, Short.MAX_VALUE))
			    .addGroup(GroupLayout.Alignment.LEADING, thisLayout.createSequentialGroup()
			        .addComponent(btnClassicWindow, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
			        .addGap(0, 67, Short.MAX_VALUE))
			    .addGroup(GroupLayout.Alignment.LEADING, thisLayout.createSequentialGroup()
			        .addComponent(btnWacken, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
			        .addGap(0, 105, Short.MAX_VALUE)))
			.addContainerGap(224, 224));
	}

}
