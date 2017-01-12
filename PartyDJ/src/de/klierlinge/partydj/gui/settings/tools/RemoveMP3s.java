package de.klierlinge.partydj.gui.settings.tools;

import java.util.List;
import javax.swing.JOptionPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.klierlinge.partydj.basics.Controller;
import de.klierlinge.partydj.common.Track;
import de.klierlinge.partydj.data.IData;
import de.klierlinge.partydj.gui.PDJList;
import de.klierlinge.partydj.gui.StatusDialog;
import de.klierlinge.partydj.gui.StatusDialog.StatusSupportedFunction;
import de.klierlinge.partydj.lists.EditableListModel;
import de.klierlinge.partydj.lists.ListException;
import de.klierlinge.partydj.lists.data.DbMasterListModel;
import de.klierlinge.partydj.lists.data.DbTrack;

public class RemoveMP3s implements StatusSupportedFunction
{
	private static final Logger log = LoggerFactory.getLogger(RemoveMP3s.class);
	protected final PDJList list;
	protected int count;
	protected volatile boolean goOn = true;
	
	public RemoveMP3s(final PDJList list)
	{
		this.list = list;
	}
	
	@Override
	public void runFunction(final StatusDialog sd)
	{
		if(list.getListModel() instanceof EditableListModel)
		{
			final int[] indices = list.getSelectedIndices();
			list.setSelectedIndices(new int[0]);
			sd.setBarMaximum(indices.length);
			try
			{
				final EditableListModel elm = (EditableListModel) list.getListModel();
	        	for(int i = 0; i < indices.length && goOn; i++)
				{		
					elm.remove(indices[0], i < indices.length - 1);
					final int[] toSelect = list.getSelectedIndices();
					if(toSelect.length > 0)
						toSelect[0] = -1;
					list.setSelectedIndices(toSelect);
					count++;
					sd.setBarPosition(count);
					sd.setLabel(count + " von Liste entfernt!");
				}
	        	if(!goOn)
	        		elm.remove(-1, false);
			}
			catch (final ListException e)
			{
				log.error("Remove MP3 failed.", e);
			}
		}
		else if(list.getListModel() instanceof DbMasterListModel)
		{
			final List<Track> tracks = list.getSelectedValuesList();
			if(JOptionPane.NO_OPTION == JOptionPane.showConfirmDialog(null, "Alle " + tracks.size() + " wirklich löschen?", "PartyDJ", JOptionPane.YES_NO_OPTION))
				return;
			sd.setBarMaximum(tracks.size());
			final IData data = Controller.getInstance().getData();
			for(int i = 0; i < tracks.size(); i++)
			{
				try
				{
					if(tracks.get(i) instanceof DbTrack)
						data.deleteTrack((DbTrack)tracks.get(i), i <= tracks.size() - 1);
					else
						data.deleteTrack(data.getTrack(tracks.get(i).getPath(), false), i <= tracks.size() - 1);

					count++;
					sd.setBarPosition(count);
					sd.setLabel(count + " von Liste entfernt!");
				}
				catch (final ListException e)
				{
					log.error("Delete MP3 failed.", e);
				}
			}
			
		}
	}

	@Override
	public void stopTask()
	{
		goOn = false;
	}
}