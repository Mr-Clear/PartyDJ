package gui;

import gui.dnd.DragDropHandler;
import gui.dnd.TrackSelection;
import java.awt.Color;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FilenameFilter;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import players.PlayerException;
import basics.Controller;
import lists.DbMasterListModel;
import lists.EditableListModel;
import lists.ListException;
import common.Sort;
import common.SortMode;
import common.Track;

//TODO Mehrfachauswahl

public class PopupMenuGenerator
{
	public static JPopupMenu listPopupMenu(PDJList list, Track track)
	{
		ActionListener listener = new PopupMenuItemListener(list, track);
		boolean listEditable = list.getListModel() instanceof EditableListModel;
		JPopupMenu menu = new JPopupMenu();
		
		JMenuItem newItem = new JMenuItem(list.getSelectedValue().toString());
		newItem.setForeground(new Color(0, 128, 0));
		newItem.setActionCommand("Play");
		newItem.addActionListener(listener);
		menu.add(newItem);
		
		menu.addSeparator();
		
		newItem = new JMenuItem("Bearbeiten...");
		newItem.setActionCommand("Edit");
		newItem.addActionListener(listener);
		menu.add(newItem);
		
		if(list.getListModel() instanceof DbMasterListModel)
		{
			newItem = new JMenuItem("Aus Hauptliste entfernen");
			newItem.setActionCommand("DeleteFromMasterList");
			newItem.addActionListener(listener);
			menu.add(newItem);
		}
		else
		{
			newItem = new JMenuItem("Shuffle");
			newItem.setActionCommand("shuffle");
			newItem.addActionListener(listener);
			menu.add(newItem);
			
			newItem = new JMenuItem("Nach Namen sortieren");
			newItem.setActionCommand("sortName");
			newItem.addActionListener(listener);
			menu.add(newItem);
			
			newItem = new JMenuItem("Nach Dauer sortieren");
			newItem.setActionCommand("sortDuration");
			newItem.addActionListener(listener);
			menu.add(newItem);
			
			newItem = new JMenuItem("Entfernen [Entf]");
			newItem.setActionCommand("Delete");
			newItem.setEnabled(listEditable);
			newItem.addActionListener(listener);
			menu.add(newItem);
			
			newItem = new JMenuItem("Ausschneiden [Strg + X]");
			newItem.setActionCommand("Cut");
			newItem.setEnabled(listEditable);
			newItem.addActionListener(listener);
			menu.add(newItem);
		}
		
		newItem = new JMenuItem("Kopieren [Strg + C]");
		newItem.setActionCommand("Copy");
		newItem.addActionListener(listener);
		menu.add(newItem);

		newItem = new JMenuItem("Einfügen [Strg + V]");
		newItem.setActionCommand("Paste");
		newItem.setEnabled(listEditable);
		newItem.addActionListener(listener);
		menu.add(newItem);
		
		menu.addSeparator();
		
		JMenu fileMenu = new JMenu("Aus Datei einfügen");
		fileMenu.setActionCommand("File");
		fileMenu.setEnabled(listEditable);
		fileMenu.addActionListener(listener);
		fileMenu.addMenuListener(new FileMenuListener(list));
		menu.add(fileMenu);
		
		newItem = new JMenuItem("Datei öffnen...");
		newItem.setActionCommand("OpenFile");
		newItem.addActionListener(listener);
		fileMenu.add(newItem);	
		return menu;
	}
	

}

class PopupMenuItemListener implements ActionListener
{
	private PDJList list;
	private Track track;
	
	PopupMenuItemListener(PDJList list, Track track)
	{
		this.list = list;
		this.track = track;
	}
	
	public void actionPerformed(ActionEvent e)
	{
		String command = e.getActionCommand();
		
		if(command.equals("Play"))
			try
			{
				Controller.getInstance().getPlayer().start(track);
			}
			catch (PlayerException e2)
			{
				e2.printStackTrace();
				JOptionPane.showMessageDialog(null, "Track kann nicht wiedergegeben werden:\n" + track, "PartyDJ", JOptionPane.ERROR_MESSAGE);
			}
		else if(command.equals("Edit"))
			new EditTrackWindow(track);
		
		else if(command.equals("DeleteFromMasterList"))
		{
			if(JOptionPane.NO_OPTION == JOptionPane.showConfirmDialog(null, "Track wirklich vollständig aus PartyDJ entfernen?", "PartyDJ", JOptionPane.YES_NO_OPTION))
				return;
			try
			{
				Controller.getInstance().getData().deleteTrack(track);
			}
			catch (ListException e1)
			{
				e1.printStackTrace();
				JOptionPane.showMessageDialog(null, "Track konnte nicht entfernt werden:\n" + track.name + "\n\n" + e1.getMessage(), "PartyDJ", JOptionPane.ERROR_MESSAGE);
			}
		}		
		else if(command.equals("Delete"))
		{
			if(list.getListModel() instanceof EditableListModel)
				try
				{
					for(int i = list.getSelectedIndices().length; i > 0; i--)
					{
						((EditableListModel)(list.getListModel())).remove(list.getSelectedIndices()[i-1]);
					}
				}
				catch (ListException e1)
				{
					e1.printStackTrace();
					JOptionPane.showMessageDialog(null, "Track konnte nicht entfernt werden:\n" + track.name + "\n\n" + e1.getMessage(), "PartyDJ", JOptionPane.ERROR_MESSAGE);
				}	
		}
		
		else if(command.equals("Copy"))
		{
			TrackTransfer transfer = new TrackTransfer();
			transfer.setClipboardContents(list.getSelectedValues());
		}
			
		else if(command.equals("Cut"))
		{
			TrackTransfer transfer = new TrackTransfer();
			transfer.setClipboardContents(list.getSelectedValues());
			
			new DragDropHandler().exportDone(list, new TrackSelection(list.getSelectedValues()),  javax.swing.TransferHandler.MOVE );
		}
		
		else if(command.equals("Paste"))
			new DragDropHandler().importData(list, Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null));
		
			
			
		else if(command.equals("OpenFile"))
		{
			final JFileChooser fileChooser = new JFileChooser("Datei öffnen:");
			fileChooser.setDialogType(JFileChooser.OPEN_DIALOG); 
			fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			fileChooser.setCurrentDirectory(new File("M:/Playlists"));	//TODO Besser nicht konstant
			
			final int result = fileChooser.showOpenDialog(null);
	        if (result == JFileChooser.CANCEL_OPTION)
	        	return;
	        
            File file = fileChooser.getSelectedFile();
            System.out.println(file.getPath());
            // TODO Datei in Liste laden
		}
		else if(command.equals("sortName"))
			Sort.quickSort(list, SortMode.NAME);
			
		else if(command.equals("sortDuration"))
			Sort.quickSort(list, SortMode.DURATION);
		
		else if(command.equals("shuffle"))
			Sort.shuffle(list);
			
	}
}

class FileMenuListener implements MenuListener
{
	@SuppressWarnings("unused")
	private PDJList list;
	FileMenuListener(PDJList list)
	{
		this.list = list;
	}
	
	public void menuCanceled(MenuEvent arg0){}
	public void menuDeselected(MenuEvent arg0){}

	public void menuSelected(MenuEvent e)
	{
		JMenu menu = (JMenu)e.getSource();
		
		if(menu.getSubElements()[0].getSubElements().length > 1)	//Dateien nur einmal einlesen 
			return;
		
		String path = "M:/Playlists";	//TODO Besser nicht konstant
		File folder = new File(path);
		
		if(!folder.isDirectory())
			throw new IllegalArgumentException("Angegebener Pfad ist kein Ordner:\n" + path);

		
		String[] files = folder.list(new FilenameFilter (){
						public boolean accept(File dir, String name)
						{
							return (name.toLowerCase().endsWith(".m3u"));
						}});
		
		if(files.length > 0)
		{
			FileMenuItemListener listener = new FileMenuItemListener();
			menu.addSeparator();
			
			for(String file : files)
			{
				JMenuItem newItem = new JMenuItem(file);
				newItem.setActionCommand(path + "/" + file);
				newItem.addActionListener(listener);
				menu.add(newItem);
			}
		}

	}
}

class FileMenuItemListener implements ActionListener
{
	public void actionPerformed(ActionEvent e)
	{
		System.out.println(e.getActionCommand());
		// TODO Datei in Liste laden
	}
}

class TrackTransfer implements ClipboardOwner
{

	public void lostOwnership(Clipboard clipboard, Transferable contents){}
	
	public void setClipboardContents(Object[] track)
	{
		TrackSelection trackSelection = new TrackSelection(track);
	    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
	    clipboard.setContents(trackSelection, this);
	}
	
}