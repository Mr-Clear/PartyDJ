package de.klierlinge.partydj.gui.settings.tools;

import java.awt.dnd.DropTargetDropEvent;
import java.io.File;
import java.util.List;
import javax.swing.JOptionPane;
import de.klierlinge.partydj.basics.Controller;
import de.klierlinge.partydj.common.Track;
import de.klierlinge.partydj.data.IData;
import de.klierlinge.partydj.gui.PDJList;
import de.klierlinge.partydj.gui.StatusDialog;
import de.klierlinge.partydj.gui.StatusDialog.StatusSupportedFunction;
import de.klierlinge.partydj.lists.EditableListModel;
import de.klierlinge.partydj.lists.ListException;
import de.klierlinge.partydj.lists.data.DbMasterListModel;

/**Fügt Tracks in eine Liste ein.
 * 
 * @author Eraser
 *
 */
public class AddMP3s implements StatusSupportedFunction
{
	protected final DropTargetDropEvent  dtde;
	protected int j;
	protected List<?> mp3s;
	protected volatile boolean goOn = true;
	protected PDJList list;
	protected final static IData data = Controller.getInstance().getData();
	
	public AddMP3s(final DropTargetDropEvent dropTargetDropEvent, final List<?> mp3s, final int toAdd)
	{
		dtde = dropTargetDropEvent;
		this.mp3s = mp3s;
		j = toAdd;
		list = (PDJList)dtde.getDropTargetContext().getComponent();
	}
	
	@Override
	public void runFunction(final StatusDialog sd)
	{
		int count = 0;
		if(sd != null)
			sd.setBarMaximum(j);
		try
		{				
			for(int i = 0; i < mp3s.size() && goOn; i++)
			{
				if(mp3s.get(i) instanceof File && !((File)mp3s.get(i)).isDirectory())
				{
					final String filePath = ((File)mp3s.get(i)).getAbsolutePath();
					
					if(!filePath.toLowerCase().endsWith(".mp3"))
						break;
					
					if(list.getListDropMode() == null)
					{
						dtde.dropComplete(false);
						return;
					}
					
					if(list.getListModel() instanceof EditableListModel)
					{
						((EditableListModel)list.getListModel()).add(new Track(filePath, false), i < mp3s.size() - 1);
						count++;
					}
					else if(list.getListModel() instanceof DbMasterListModel)
					{
						data.addTrack(new Track(filePath, false), i < mp3s.size() - 1);
					}

					if(sd != null)
					{
						sd.setLabel(count + ": " + filePath);
						sd.setBarPosition(count);
						sd.stopTimer();
					}
				}
				else if(mp3s.get(i) instanceof Track)
				{
					if(list.getListDropMode() == null)
					{
						dtde.dropComplete(false);
						return;
					}

					if(list.getListModel() instanceof EditableListModel)
					{
						((EditableListModel)list.getListModel()).add((Track) mp3s.get(i), i < mp3s.size() - 1);
						count++;
					}
					
					if(sd != null)
					{
						sd.setLabel(count + ": " + ((Track) mp3s.get(i)).getName());
						sd.setBarPosition(count);
					}
				}
			}
			
			if(!goOn)
				((EditableListModel)list.getListModel()).add(null, false);
		}
		catch(final ListException e)
		{
			Controller.getInstance().logError(Controller.IMPORTANT_ERROR, this, e, "Track konnte nicht eingefügt werden.");
		}
		if(sd != null && mp3s.get(0) instanceof File)
			JOptionPane.showMessageDialog(sd, count + " Tracks eingefügt.", "Datei einfügen", JOptionPane.INFORMATION_MESSAGE);
	}

	@Override
	public void stopTask()
	{
		goOn = false;
	}
}
