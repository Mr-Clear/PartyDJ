package de.klierlinge.partydj.gui;

import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import de.klierlinge.partydj.gui.TrackListAppearance.EntryState;
import de.klierlinge.partydj.gui.TrackListAppearance.Part;
import de.klierlinge.partydj.gui.TrackListAppearance.TrackState;
import de.klierlinge.partydj.gui.dnd.ListDropMode;
import de.klierlinge.partydj.lists.TrackListModel;

public class PDJListTabs extends JTabbedPane
{
	private static final long serialVersionUID = -854445021765643771L;
	private final TrackListAppearance appearance;
	
	public PDJListTabs(final TrackListAppearance appearance)
	{
		this.appearance = appearance;
		setForeground(appearance.getColor(TrackState.Normal, EntryState.Normal, Part.Foreground));
		setBackground(appearance.getColor(TrackState.Normal, EntryState.Normal, Part.Background));
	}
	
	public PDJList newTab(final String title, final TrackListModel l, final ListDropMode ldMode)
	{
		final PDJList list = new PDJList(l, ldMode, title);

		list.setAppearance(TrackListAppearance.getGreenAppearance(22));
		list.setAppearance(appearance);
		final JScrollPane scrollPane = new JScrollPane(list);
		
		this.addTab(title, scrollPane);
		return list;
	}

	public TrackListAppearance getAppearance()
	{
		return appearance;
	}
	
	
}
