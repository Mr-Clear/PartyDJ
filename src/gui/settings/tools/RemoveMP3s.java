package gui.settings.tools;

import javax.swing.JOptionPane;
import basics.Controller;
import gui.PDJList;
import gui.StatusDialog;
import gui.StatusDialog.StatusSupportedFunction;
import lists.EditableListModel;
import lists.ListException;
import lists.data.DbMasterListModel;
import lists.data.DbTrack;
import lists.data.SearchListModel;

public class RemoveMP3s implements StatusSupportedFunction
	{
		protected final PDJList list;
		protected int count;
		protected boolean goOn = true;
		
		public RemoveMP3s(PDJList list)
		{
			this.list = list;
		}
		
		@Override
		public synchronized void runFunction(StatusDialog sd)
		{
			if(list.getListModel() instanceof EditableListModel)
			{
				int[] indices = list.getSelectedIndices();
				list.setSelectedIndices(new int[0]);
				sd.setBarMaximum(indices.length);
				try
				{
					EditableListModel elm = (EditableListModel) list.getListModel();
		        	for(int i = 0; i < indices.length && goOn; i++)
					{		
						elm.remove(indices[0]);
						int[] toSelect = list.getSelectedIndices();
						if(toSelect.length > 0)
							toSelect[0] = -1;
						list.setSelectedIndices(toSelect);
						count++;
						sd.setBarPosition(count);
						sd.setLabel(count + " von Liste entfernt!");
					}
				}
				catch (ListException e)
				{
					Controller.getInstance().logError(Controller.NORMAL_ERROR, this, e, null);
				}
			}
			else if(list.getListModel() instanceof DbMasterListModel || list.getListModel() instanceof SearchListModel)
			{
				int[] indices = list.getSelectedIndices();
				if(JOptionPane.NO_OPTION == JOptionPane.showConfirmDialog(null, "Alle " + indices.length + " wirklich löschen?", "PartyDJ", JOptionPane.YES_NO_OPTION))
					return;
				for(int i = indices.length; i > 0; i--)
				{
					try
					{
						Controller.getInstance().getData().deleteTrack((DbTrack)list.getListModel().getElementAt(indices[0]));
						int[] toSelect = list.getSelectedIndices();
						toSelect[toSelect.length - 1] = -1;
						list.setSelectedIndices(toSelect);
						count++;
						sd.setBarPosition(count);
						sd.setLabel(count + " von Liste entfernt!");
					}
					catch (ListException e)
					{
						e.printStackTrace();
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