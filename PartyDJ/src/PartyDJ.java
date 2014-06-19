import basics.Controller;
import javax.swing.JOptionPane;

/**Main-Klasse.
 * Startet den PartyDJ.
 * 
 * @author Eraser
 *
 */
public class PartyDJ
{
	/** Main-Funktion.
	 *  Startet den PartyDJ.
	 *  @param args Befehlszeilenargumente des PartyDJ.
	 */
	public static void main(final String[] args)
	{
		if(args.length > 0 && args[0].equalsIgnoreCase("network"))
		{
			new network.remote.LightNetworkRemote(args);
		}
		else
		{
			try
			{
				new basics.PartyDJ(args);
			}
			catch (final Exception e)
			{
				e.printStackTrace();
				JOptionPane.showMessageDialog(null, "Fehler beim Erstellen des PartyDJ-Controllers:\n" + e.getMessage(), "PartyDJ", JOptionPane.ERROR_MESSAGE);
				Controller.getInstance().closePartyDJ();
			}
		}
	}
}
