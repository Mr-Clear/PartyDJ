package gui.settings;

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
	private JButton btsSwap;

	public Stuff() {
		super();

	SwingUtilities.invokeLater(new Runnable(){
		@Override
		public void run()
		{
			initGUI();
		}});
		
	}
	
	private void initGUI() {
		try {
			GroupLayout thisLayout = new GroupLayout(this);
			this.setLayout(thisLayout);
			setPreferredSize(new Dimension(400, 300));
			{
				btnDerbyDebug = new JButton();
				btnDerbyDebug.setText("Datenbank Debug-Fenster ...");
				btnDerbyDebug.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent e)
					{
						Controller.getInstance().registerWindow(new data.derby.DebugWindow());
					}});
			}
			{
				btsSwap = new JButton();
				btsSwap.setText("Swap Test");
				btsSwap.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent e)
					{
						try
						{
							Controller.getInstance().getListProvider().getDbList("Playlist").swap(1, 4);
						}
						catch (ListException e1)
						{
							e1.printStackTrace();
						}
					}});
			}
			thisLayout.setVerticalGroup(thisLayout.createSequentialGroup()
				.addContainerGap()
				.addComponent(btnDerbyDebug, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
				.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
				.addComponent(btsSwap, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addContainerGap(233, Short.MAX_VALUE));
			thisLayout.setHorizontalGroup(thisLayout.createSequentialGroup()
				.addContainerGap()
				.addGroup(thisLayout.createParallelGroup()
				    .addGroup(thisLayout.createSequentialGroup()
				        .addComponent(btnDerbyDebug, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
				        .addGap(0, 0, Short.MAX_VALUE))
				    .addGroup(GroupLayout.Alignment.LEADING, thisLayout.createSequentialGroup()
				        .addComponent(btsSwap, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				        .addGap(0, 105, Short.MAX_VALUE)))
				.addContainerGap(224, 224));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
