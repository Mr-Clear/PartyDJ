package gui.settings;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComponent;

import javax.swing.LayoutStyle;
import lists.ListException;
import basics.Controller;

/**
* This code was edited or generated using CloudGarden's Jigloo
* SWT/Swing GUI Builder, which is free for non-commercial
* use. If Jigloo is being used commercially (ie, by a corporation,
* company or business for any purpose whatever) then you
* should purchase a license for each developer using Jigloo.
* Please visit www.cloudgarden.com for details.
* Use of Jigloo implies acceptance of these licensing terms.
* A COMMERCIAL LICENSE HAS NOT BEEN PURCHASED FOR
* THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED
* LEGALLY FOR ANY CORPORATE OR COMMERCIAL PURPOSE.
*/
public class Stuff extends javax.swing.JPanel {
	private static final long serialVersionUID = 5155888057108134828L;
	private JButton btnDerbyDebug;
	private JButton btsSwap;

	public Stuff() {
		super();
		initGUI();
	}
	
	private void initGUI() {
		try {
			GroupLayout thisLayout = new GroupLayout((JComponent)this);
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