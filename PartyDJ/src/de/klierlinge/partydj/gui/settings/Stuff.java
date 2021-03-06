package de.klierlinge.partydj.gui.settings;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.LayoutStyle.ComponentPlacement;
import de.klierlinge.partydj.basics.Controller;
import de.klierlinge.partydj.gui.ClassicWindow;
import de.klierlinge.partydj.gui.ListWindow;
import de.klierlinge.partydj.lists.ListException;
import javax.swing.SwingUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Debug Hilfe. Bietet Platz für verschiedene Einstellungen.
 * 
 * @author Eraser
 * 
 * @see SettingWindow
 */
public class Stuff extends javax.swing.JPanel
{
	private static final long serialVersionUID = 5155888057108134828L;
	private static final Logger log = LoggerFactory.getLogger(Stuff.class);
	private JButton btnDerbyDebug;
	private JButton btnWacken;
	private JButton btnClassicWindow;

	protected final transient Controller controller = Controller.getInstance();
	private JButton btnErrorWindow;

	public Stuff()
	{
		super();

		SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				initGUI();
			}
		});

	}

	private void initGUI()
	{
		setPreferredSize(new Dimension(400, 300));
		{
			btnErrorWindow = new JButton("Fehlerfenster");
			btnErrorWindow.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(final ActionEvent e)
				{
					controller.showErrorWindow();
				}
			});
			{
				btnClassicWindow = new JButton();
				btnClassicWindow.setText("Classic Window");
				btnClassicWindow.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(final ActionEvent evt)
					{
						controller.registerWindow(new ClassicWindow());
					}
				});
			}
			{
				btnDerbyDebug = new JButton();
				btnDerbyDebug.setText("Datenbank Debug-Fenster ...");
				btnDerbyDebug.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(final ActionEvent e)
					{
						controller.registerWindow(new de.klierlinge.partydj.data.derby.DebugWindow());
					}
				});
			}
			{
				btnWacken = new JButton();
				btnWacken.setText("Wacken Liste");
				btnWacken.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(final ActionEvent evt)
					{
						try
						{
							Controller.getInstance().registerWindow(new ListWindow("Wacken"));
						}
						catch (final ListException e)
						{
							log.error("Liste konnte nicht erstellt werden.", e);
						}
					}
				});
			}
		}
		final GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(btnDerbyDebug)
						.addComponent(btnClassicWindow)
						.addComponent(btnWacken)
						.addComponent(btnErrorWindow))
					.addContainerGap(215, Short.MAX_VALUE))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(5)
					.addComponent(btnDerbyDebug)
					.addGap(5)
					.addComponent(btnClassicWindow)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnWacken)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnErrorWindow)
					.addContainerGap(186, Short.MAX_VALUE))
		);
		setLayout(groupLayout);
	}
}
