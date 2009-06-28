package gui.settings.tools;

import javax.swing.JOptionPane;
import basics.Controller;
import gui.PDJList;
import gui.StatusDialog;
import gui.StatusDialog.StatusSupportedFunction;
import lists.DbMasterListModel;
import lists.EditableListModel;
import lists.ListException;
import lists.SearchListModel;

public class RemoveMP3s implements StatusSupportedFunction
	{
		protected final PDJList pdj;
		protected int count;
		protected boolean goOn = true;
		
		public RemoveMP3s(PDJList list)
		{
			pdj = list;
		}
		
		@Override
		public synchronized void runFunction(StatusDialog sd)
		{
			if(pdj.getListModel() instanceof EditableListModel)
			{
				int[] indices = pdj.getSelectedIndices();
				pdj.setSelectedIndices(new int[0]);
				sd.setBarMaximum(indices.length);
				try
				{
					EditableListModel elm = (EditableListModel) pdj.getListModel();
		        	for(int i = 0; i < indices.length && goOn; i++)
					{		
						elm.remove(indices[0]);
						int[] toSelect = pdj.getSelectedIndices();
						if(toSelect.length > 0)
							toSelect[0] = -1;
						pdj.setSelectedIndices(toSelect);
						count++;
						sd.setBarPosition(count);
						sd.setLabel(count + " von Liste entfernt!");
					}
				}
				catch (ListException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else if(pdj.getListModel() instanceof DbMasterListModel || pdj.getListModel() instanceof SearchListModel)
			{
				int[] indices = pdj.getSelectedIndices();
				if(JOptionPane.NO_OPTION == JOptionPane.showConfirmDialog(null, "Alle " + indices.length + " wirklich löschen?", "PartyDJ", JOptionPane.YES_NO_OPTION))
					return;
				for(int i = indices.length; i > 0; i--)
				{
					try
					{
						Controller.getInstance().getData().deleteTrack(pdj.getListModel().getElementAt(indices[0]));
						int[] toSelect = pdj.getSelectedIndices();
						toSelect[toSelect.length - 1] = -1;
						pdj.setSelectedIndices(toSelect);
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