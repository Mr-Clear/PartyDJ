package gui;

import lists.TrackListModel;
import gui.TrackListAppearance.EntryState;
import gui.TrackListAppearance.Part;
import gui.TrackListAppearance.TrackState;
import gui.dnd.ListDropMode;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

public class PDJListTabs extends JTabbedPane
{
	private static final long serialVersionUID = -854445021765643771L;
	private final TrackListAppearance appearance;
	
	public PDJListTabs(TrackListAppearance appearance)
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
