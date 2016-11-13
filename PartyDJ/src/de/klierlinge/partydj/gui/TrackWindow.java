package de.klierlinge.partydj.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.lang.reflect.InvocationTargetException;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.TableCellRenderer;
import de.klierlinge.partydj.basics.Controller;
import de.klierlinge.partydj.common.Track;

public class TrackWindow extends JFrame
{
	private static final long serialVersionUID = 5955216047024593003L;
	protected final static Controller controller = Controller.getInstance();

	protected Mp3TagTableModel tableModel;
	protected JTable table;
	protected JScrollPane scrollPane;
	
	public TrackWindow()
	{
		this(controller.getPlayer().getCurrentTrack());
	}
	
	public TrackWindow(final Track track)
	{
		super("Track Details");
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		
		tableModel = new Mp3TagTableModel(track);
		
		if(SwingUtilities.isEventDispatchThread())
		{
			initGUI();
		}
		else
		{
			try
			{
				SwingUtilities.invokeAndWait(new Runnable()
				{
					@Override public void run()
					{
						try
						{
							initGUI();}
							catch (final Exception e)
							{
								e.printStackTrace();
								controller.logError(Controller.NORMAL_ERROR, this, e, "Fehler bei Laden des Fensters.");
							}
					}
				});
			}
			catch (final InterruptedException e)
			{
				controller.logError(Controller.NORMAL_ERROR, this, e, "Fehler bei Laden des Fensters.");
			}
			catch (final InvocationTargetException e)
			{
				controller.logError(Controller.NORMAL_ERROR, this, e, "Fehler bei Laden des Fensters.");
			}
		}
	}
	
	protected void initGUI()
	{
		table = new JTable(tableModel);
		table.getColumnModel().getColumn(0).setCellRenderer(new TableCellRenderer()
		{
			@Override
			public Component getTableCellRendererComponent(final JTable table1, final Object value, final boolean isSelected, final boolean hasFocus, final int row, final int column)
			{
				final JLabel lbl = new JLabel(value.toString());
				final Font tableFont = table1.getFont();
				final int fontStyle;
				final int fontSize;
				if(table1.getModel().getValueAt(row, 1) == null)
				{
					fontStyle = Font.BOLD;
					fontSize = tableFont.getSize() + 2;
				}
				else
				{
					fontStyle = Font.PLAIN;
					fontSize = tableFont.getSize();
				}
				final Font font = new Font(tableFont.getName(), fontStyle, fontSize);
				lbl.setFont(font);
				return lbl;
			}
		});
		table.getColumnModel().getColumn(0).setMinWidth(150);
		table.getColumnModel().getColumn(0).setMaxWidth(200);
		table.getColumnModel().getColumn(0).setPreferredWidth(150);
		table.getColumnModel().getColumn(1).setPreferredWidth(800);
		scrollPane = new JScrollPane(table);
		add(scrollPane);

		setMinimumSize(new Dimension(400, 200));
		setSize(new Dimension(1000, 700));
		setVisible(true);
	}
	
	public void loadTrack(final Track track) 
	{
		tableModel = new Mp3TagTableModel(track);
		table.setModel(tableModel);
	}
}
