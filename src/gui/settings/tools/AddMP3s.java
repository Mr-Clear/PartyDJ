package gui.settings.tools;

import gui.PDJList;
import gui.StatusDialog;
import gui.StatusDialog.StatusSupportedFunction;
import java.awt.dnd.DropTargetDropEvent;
import java.io.File;
import java.util.List;
import javax.swing.JOptionPane;
import lists.DbMasterListModel;
import lists.EditableListModel;
import lists.ListException;
import common.DbTrack;
import common.Track;

/**Fügt Tracks in eine Liste ein.
 * 
 * @author Eraser
 *
 */
public class AddMP3s implements StatusSupportedFunction
{
	protected final DropTargetDropEvent  dtde;
	protected int j;
	protected List<?> data;
	protected boolean goOn = true;
	protected PDJList list;
	
	public AddMP3s(DropTargetDropEvent dropTargetDropEvent, List<?> mp3s, int toAdd)
	{
		dtde = dropTargetDropEvent;
		data = mp3s;
		j = toAdd;
		list  = (PDJList)dtde.getDropTargetContext().getComponent();
	}
	
	@Override
	public synchronized void runFunction(StatusDialog sd)
	{
		int count = 0;
		if(sd != null)
			sd.setBarMaximum(j);
		try
		{				
			for(int i = 0; i < data.size() && goOn; i++)
			{
				if(data.get(i) instanceof File && !((File)data.get(i)).isDirectory())
				{
					String filePath = ((File)data.get(i)).getAbsolutePath();
					
					if(!filePath.toLowerCase().endsWith(".mp3"))
						break;
					
					if(list.getListDropMode() == null)
					{
						dtde.dropComplete(false);
						return;
					}
					
					if(list.getListModel() instanceof EditableListModel)
					{
						((EditableListModel)list.getListModel()).add(new DbTrack(filePath, false));
						count++;
					}
					else if(list.getListModel() instanceof DbMasterListModel)
					{
						new DbTrack(filePath, false);
					}

					if(sd != null)
					{
						sd.setLabel(count + ": " + filePath);
						sd.setBarPosition(count);
						sd.stopTimer();
					}
				}
				else if(data.get(i) instanceof Track)
				{
					if(list.getListDropMode() == null)
					{
						dtde.dropComplete(false);
						return;
					}

					if(list.getListModel() instanceof EditableListModel)
					{
						((EditableListModel)list.getListModel()).add((Track) data.get(i));
						count++;
					}
					
					if(sd != null)
					{
						sd.setLabel(count + ": " + ((Track) data.get(i)).getName());
						sd.setBarPosition(count);
					}
				}
			}
		}
		catch(ListException le)
		{
			le.printStackTrace();
		}
		if(sd != null && data.get(0) instanceof File)
			JOptionPane.showMessageDialog(sd, count + " Tracks eingefügt.", "Datei einfügen", JOptionPane.INFORMATION_MESSAGE);
	}

	@Override
	public void stopTask()
	{
		goOn = false;
	}
}
