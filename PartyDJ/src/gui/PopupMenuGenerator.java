package gui;

import basics.Controller;
import common.PlaylistWriter;
import common.PlaylistWriter.Format;
import common.Sort;
import common.SortMode;
import common.Track;
import data.SortOrder;
import gui.dnd.DragDropHandler;
import gui.dnd.TrackSelection;
import gui.settings.tools.RemoveMP3s;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FilenameFilter;
import java.util.List;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.filechooser.FileFilter;
import lists.EditableListModel;
import lists.ListException;
import lists.data.DbMasterListModel;

//TODO Mehrfachauswahl

/**
 * Erzeugt Popup-Menüs.
 * 
 * @author Eraser
 * @author Sam
 */

public final class PopupMenuGenerator
{
	private PopupMenuGenerator(){}
	
	/**Erzeugt ein Popup-Menü zu einer PDJList
	 * Beim Aufrufen in invokeLater packen!
	 * 
	 * @param list Liste zu der das Popupmenü gehört.
	 * @param track Track der in dem Popupmenü behandelt wird.
	 * @return Das Menü als PopupMenu.
	 * 
	 * @see PDJList
	 */
	public static JPopupMenu listPopupMenu(final PDJList list, final Track track)
	{
		final ActionListener listener = new ListMenuItemListener(list, track);
		final boolean listEditable = list.getListModel() instanceof EditableListModel;
		final JPopupMenu menu = new JPopupMenu();
		JMenuItem newItem;
		
		if (track != null)
		{
			newItem = new JMenuItem(list.getSelectedValue().toString());
			newItem.setActionCommand("Show");
			newItem.addActionListener(listener);
			menu.add(newItem);
			
			newItem = new JMenuItem("Bearbeiten...");
			newItem.setActionCommand("Edit");
			newItem.addActionListener(listener);
			menu.add(newItem);
		}
		
		if(list.getListModel() instanceof DbMasterListModel)
		{
			newItem = new JMenuItem("Aus Hauptliste entfernen");
			newItem.setActionCommand("DeleteFromMasterList");
			newItem.addActionListener(listener);
			menu.add(newItem);
		}
		else if(listEditable && track != null)
		{
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
		
		if(track != null)
		{
			newItem = new JMenuItem("Kopieren [Strg + C]");
			newItem.setActionCommand("Copy");
			newItem.addActionListener(listener);
			menu.add(newItem);
		}

		newItem = new JMenuItem("Einfügen [Strg + V]");
		newItem.setActionCommand("Paste");
		newItem.setEnabled(listEditable);
		newItem.addActionListener(listener);
		menu.add(newItem);
		
		menu.addSeparator();
		
		/* Sortieren */
		if(listEditable)
		{
			final JMenu sortMenu = new JMenu("Sortieren");
			
			newItem = new JMenuItem("Shuffle");
			newItem.setActionCommand("sortListShuffle");
			newItem.addActionListener(listener);
			sortMenu.add(newItem);
			
			newItem = new JMenuItem("Nach Namen sortieren");
			newItem.setActionCommand("sortListName");
			newItem.addActionListener(listener);
			sortMenu.add(newItem);
			
			newItem = new JMenuItem("Nach Pfad sortieren");
			newItem.setActionCommand("sortListPath");
			newItem.addActionListener(listener);
			sortMenu.add(newItem);
			
			newItem = new JMenuItem("Nach Dauer sortieren");
			newItem.setActionCommand("sortListDuration");
			newItem.addActionListener(listener);
			sortMenu.add(newItem);

			menu.add(sortMenu);
		}
		else if(list.getListModel() instanceof DbMasterListModel)
		{
			final JMenu sortMenu = new JMenu("Sortieren");
			
			for(final SortOrder sortOrder : new SortOrder[]{SortOrder.NAME, SortOrder.PATH, SortOrder.SIZE, SortOrder.DURATION, SortOrder.PROBLEM, SortOrder.NONE})
			{
				newItem = new JCheckBoxMenuItem(sortOrder.toString());
				newItem.setActionCommand("sortMasterList" + Integer.toString(sortOrder.toArrayIndex()));
				newItem.addActionListener(listener);
				if(sortOrder == Controller.getInstance().getListProvider().getMasterList().getSortOrder())
					newItem.setSelected(true);
				sortMenu.add(newItem);
			}
			
			menu.add(sortMenu);
		}
		
		final JMenu fileMenu = new JMenu("Aus Datei einfügen");
		fileMenu.setActionCommand("File");
		fileMenu.setEnabled(listEditable);
		fileMenu.addActionListener(listener);
		fileMenu.addMenuListener(new FileMenuListener(list));
		menu.add(fileMenu);
		
		newItem = new JMenuItem("Datei öffnen...");
		newItem.setActionCommand("OpenFile");
		newItem.addActionListener(listener);
		fileMenu.add(newItem);	
		
		newItem = new JMenuItem("Speichern...");
		newItem.setActionCommand("Save");
		newItem.addActionListener(listener);
		menu.add(newItem);
		
		return menu;
	}
}

class ListMenuItemListener implements ActionListener
{
	private final PDJList list;
	private final Track track;
	
	ListMenuItemListener(final PDJList list, final Track track)
	{
		this.list = list;
		this.track = track;
	}
	
	@Override
	public void actionPerformed(final ActionEvent e)
	{
		final String command = e.getActionCommand();
		if(command == null)
			return;
		
		if("Play".equals(command))
			track.play();
		else if("Edit".equals(command))
			new EditTrackWindow(track);
		
		else if("DeleteFromMasterList".equals(command))
		{

			new StatusDialog("Entferne MP3s", null, new RemoveMP3s(list));
		}		
		else if("Delete".equals(command))
		{
			new StatusDialog("Entferne MP3s", null, new RemoveMP3s(list));
		}
		
		else if("Copy".equals(command))
		{
			final TrackTransfer transfer = new TrackTransfer();
			transfer.setClipboardContents(list.getSelectedValuesList());
		}
			
		else if("Cut".equals(command))
		{
			final TrackTransfer transfer = new TrackTransfer();
			transfer.setClipboardContents(list.getSelectedValuesList());
			
			new DragDropHandler().exportDone(list, new TrackSelection(list.getSelectedValuesList()),  javax.swing.TransferHandler.MOVE);
		}
		
		else if("Paste".equals(command))
			new DragDropHandler().importData(list, Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null));
		
			
			
		else if("OpenFile".equals(command))
		{
			if(!(list.getListModel() instanceof EditableListModel))
				return;
			
			final JFileChooser fileChooser = new JFileChooser("Datei öffnen:");
			fileChooser.setDialogType(JFileChooser.OPEN_DIALOG); 
			fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			fileChooser.setCurrentDirectory(new File(basics.Controller.getInstance().getData().readSetting("PlayListDirectory", common.Functions.getFolder())));
			
			final int result = fileChooser.showOpenDialog(null);
	        if (result == JFileChooser.CANCEL_OPTION)
	        	return;
	        
            final File file = fileChooser.getSelectedFile();
            
    		new StatusDialog("Lese M3U", null, new lists.data.InsertM3U(((EditableListModel)list.getListModel()), file.getPath()));
   		}
		
		else if(command.startsWith("sortList"))
		{
			try
			{
				if("sortListName".equals(command))
					Sort.quickSort(list, SortMode.NAME);
					
				else if("sortListDuration".equals(command))
					Sort.quickSort(list, SortMode.DURATION);
				
				else if("sortListPath".equals(command))
					Sort.quickSort(list, SortMode.PATH);
				
				else if("sortListShuffle".equals(command))
					Sort.shuffle(list);
			}
			catch(final ListException e1)
			{
				Controller.getInstance().logError(Controller.NORMAL_ERROR, this, e1, "Fehler bei Zugriff auf Datenbank.");
			}
		}
		
		else if(command.startsWith("sortMasterList"))
		{
			final SortOrder sortOrder = SortOrder.arrayIndexToSortOrder(Integer.parseInt(command.substring(14)));
			
			try
			{
				Controller.getInstance().getListProvider().getMasterList().setSortOrder(sortOrder);
			}
			catch(final ListException e1)
			{
				Controller.getInstance().logError(Controller.NORMAL_ERROR, this, e1, "Sortieren fehlgeschlagen");
			}
		}
		
		else if("Save".equals(command))
		{
			save();
		}
		
		else if("Show".equals(command))
		{
			new TrackWindow(track);
		}
	}
	
	protected void save()
	{
		class FormatFiler extends FileFilter
		{
			protected final Format format;
			public FormatFiler(final Format format)
			{
				this.format = format;
			}
			@Override public boolean accept(final File f)
			{
				if(f.getName().toLowerCase().endsWith(format.getExtension().toLowerCase()))
					return true;
				if(f.isDirectory())
					return true;
				return false;
			}
			@Override public String getDescription()
			{
				return format.getDescription() + " (" + format.getExtension() + ")";
			}
		}
		
		final Format[] formats = PlaylistWriter.getFormats();
		final JFileChooser chooser = new JFileChooser();
		chooser.setDialogTitle("Liste als Playlist speichern.");

		final String defaultFormatName = Controller.getInstance().getData().readSetting("SavePlaylistDefaultFileFilter", "EXTM3U8");
		FormatFiler defaultFormatFilter = null;
		for(final Format format : formats)
		{
			final FormatFiler formatFilter = new FormatFiler(format);
			chooser.addChoosableFileFilter(formatFilter);
			if(defaultFormatName.equals(format.getName()))
				defaultFormatFilter = formatFilter;
		}
		
		chooser.setCurrentDirectory(new File(Controller.getInstance().getData().readSetting("SavePlaylistDefaultDir", common.Functions.getFolder())));
		chooser.setFileFilter(defaultFormatFilter);		
		
		final int rVal = chooser.showSaveDialog(null);
		if(rVal == JFileChooser.APPROVE_OPTION)
		{
			Controller.getInstance().getData().writeSetting("SavePlaylistDefaultDir", chooser.getSelectedFile().getParent());
			if(chooser.getFileFilter() instanceof FormatFiler)
			{
				final Format format = ((FormatFiler)chooser.getFileFilter()).format;
				Controller.getInstance().getData().writeSetting("SavePlaylistDefaultFileFilter", format.getName());
				String filePath = chooser.getSelectedFile().getPath();
				if(chooser.getSelectedFile().getName().indexOf('.') == -1)
					filePath += format.getExtension();
				PlaylistWriter.write((Iterable<Track>) list.getListModel(), filePath, format);
			}
			else
				PlaylistWriter.write((Iterable<Track>) list.getListModel(), chooser.getSelectedFile().getPath());
		}
	}
}

class FileMenuListener implements MenuListener
{
	private final PDJList list;
	FileMenuListener(final PDJList list)
	{
		this.list = list;
	}
	
	@Override
	public void menuCanceled(final MenuEvent arg0) { /* not to implement */ }
	@Override
	public void menuDeselected(final MenuEvent arg0) { /* not to implement */ }

	@Override
	public void menuSelected(final MenuEvent e)
	{
		final JMenu menu = (JMenu)e.getSource();
		
		if(menu.getSubElements()[0].getSubElements().length > 1)	//Dateien nur einmal einlesen 
			return;
		
		final String path = basics.Controller.getInstance().getData().readSetting("PlayListDirectory", common.Functions.getFolder());
		final File folder = new File(path);
		
		if(!folder.isDirectory())
			throw new IllegalArgumentException("Angegebener Pfad ist kein Ordner:\n" + path);

		
		final String[] files = folder.list(new FilenameFilter()
		{
			@Override public boolean accept(final File dir, final String name)
			{
				return (name.toLowerCase().endsWith(".m3u"));
			}
		});
		
		if(files.length > 0)
		{
			final FileMenuItemListener listener = new FileMenuItemListener(list);
			menu.addSeparator();
			
			for(final String file : files)
			{
				final JMenuItem newItem = new JMenuItem(file);
				newItem.setActionCommand(path + "/" + file);
				newItem.addActionListener(listener);
				menu.add(newItem);
			}
		}
	}
}

class FileMenuItemListener implements ActionListener
{
	private final PDJList list;
	FileMenuItemListener(final PDJList list)
	{
		this.list = list;
	}
	
	@Override
	public void actionPerformed(final ActionEvent e)
	{
		if(list.getListModel() instanceof EditableListModel)
		{
			new StatusDialog("Lese M3U", null, new lists.data.InsertM3U(((EditableListModel)list.getListModel()), e.getActionCommand()));
		}
	}
}

class TrackTransfer implements ClipboardOwner
{
	@Override
	public void lostOwnership(final Clipboard clipboard, final Transferable contents) { /* not to implement */ }
	
	public void setClipboardContents(final List<Track> tracks)
	{
		final TrackSelection trackSelection = new TrackSelection(tracks);
	    final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
	    clipboard.setContents(trackSelection, this);
	}
	
}