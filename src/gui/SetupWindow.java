package gui;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JToggleButton;
import javax.swing.LayoutStyle;

import javax.swing.WindowConstants;


public class SetupWindow extends javax.swing.JFrame
{
	private static final long serialVersionUID = 8084307658077082675L;
	private JLabel jLabel1;
	private JToggleButton addFile;
	private JToggleButton addFolder;
	private JButton close;
	private JToggleButton importPDJ;

	
	public SetupWindow() 
	{
		super();
		initGUI();
	}
	
	private void initGUI()
	{
		GroupLayout thisLayout = new GroupLayout((JComponent)getContentPane());
		getContentPane().setLayout(thisLayout);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		this.setTitle("PartyDJ Setup");
		{
			jLabel1 = new JLabel();
			jLabel1.setText("In PartyDJ wurden noch keine Lieder eingefügt.");
		}
		{
			addFile = new JToggleButton();
			addFile.setText("Datei Einfügen");
		}
		{
			addFolder = new JToggleButton();
			addFolder.setText("Verzeichnis Einfügen");
		}
		{
			importPDJ = new JToggleButton();
			importPDJ.setText("Aus altem PartyDJ importieren");
		}
		{
			close = new JButton();
			close.setText("Fenster Schließen");
		}
		thisLayout.setVerticalGroup(thisLayout.createSequentialGroup()
			.addContainerGap()
			.addComponent(jLabel1, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
			.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
			.addGroup(thisLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
			    .addComponent(addFile, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
			    .addComponent(importPDJ, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
			.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
			.addGroup(thisLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
			    .addComponent(addFolder, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
			    .addComponent(close, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
			.addContainerGap());
		thisLayout.setHorizontalGroup(thisLayout.createSequentialGroup()
			.addContainerGap()
			.addGroup(thisLayout.createParallelGroup()
			    .addGroup(GroupLayout.Alignment.LEADING, thisLayout.createSequentialGroup()
			        .addComponent(addFolder, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
			        .addGap(47)
			        .addComponent(close, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
			        .addGap(0, 0, Short.MAX_VALUE))
			    .addGroup(GroupLayout.Alignment.LEADING, thisLayout.createSequentialGroup()
			        .addComponent(addFile, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
			        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
			        .addComponent(importPDJ, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
			        .addGap(0, 0, Short.MAX_VALUE))
			    .addGroup(GroupLayout.Alignment.LEADING, thisLayout.createSequentialGroup()
			        .addComponent(jLabel1, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
			        .addGap(0, 28, Short.MAX_VALUE)))
			.addContainerGap(36, 36));
		pack();
	}

}
