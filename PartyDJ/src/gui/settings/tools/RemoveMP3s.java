package gui.settings.tools;

import basics.Controller;
import common.Track;
import data.IData;
import lists.EditableListModel;
import lists.ListException;
import lists.data.DbMasterListModel;
import lists.data.DbTrack;
import gui.PDJList;
import gui.StatusDialog;
import gui.StatusDialog.StatusSupportedFunction;
import java.util.List;
import javax.swing.JOptionPane;

public class RemoveMP3s implements StatusSupportedFunction
{
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
				Controller.getInstance().logError(Controller.NORMAL_ERROR, this, e, null);
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
					Controller.getInstance().logError(Controller.NORMAL_ERROR, this, e, null);
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