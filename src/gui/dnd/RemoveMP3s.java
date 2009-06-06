package gui.dnd;

import javax.swing.JOptionPane;
import basics.Controller;
import gui.PDJList;
import gui.StatusDialog;
import gui.StatusDialog.StatusSupportedFunction;
import lists.DbMasterListModel;
import lists.EditableListModel;
import lists.ListException;

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
				sd.setBarMaximum(indices.length);
				try
				{
					EditableListModel elm = (EditableListModel) pdj.getListModel();
		        	for(int i = indices.length; i > 0 && goOn; i--)
					{
						elm.remove(indices[i-1]);
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
			else if(pdj.getListModel() instanceof DbMasterListModel)
			{
				int[] indices = pdj.getSelectedIndices();
				if(JOptionPane.NO_OPTION == JOptionPane.showConfirmDialog(null, "Alle " + indices.length + " wirklich löschen?", "PartyDJ", JOptionPane.YES_NO_OPTION))
					return;
				for(int i = indices.length; i > 0; i--)
				{
					try
					{
						Controller.getInstance().getData().deleteTrack(pdj.getListModel().getElementAt(indices[i-1]));
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