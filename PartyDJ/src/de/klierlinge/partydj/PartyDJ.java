package de.klierlinge.partydj;
import javax.swing.JOptionPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.klierlinge.partydj.basics.Controller;

/**Main-Klasse.
 * Startet den PartyDJ.
 * 
 * @author Eraser
 *
 */
public class PartyDJ
{
	private static final Logger log = LoggerFactory.getLogger(PartyDJ.class);
	
	/** Main-Funktion.
	 *  Startet den PartyDJ.
	 *  @param args Befehlszeilenargumente des PartyDJ.
	 */
	public static void main(final String[] args)
	{
		log.info("Start");
		try
		{
			new de.klierlinge.partydj.basics.PartyDJ(args);
		}
		catch (final Exception e)
		{
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Fehler beim Erstellen des PartyDJ-Controllers:\n" + e.getMessage(), "PartyDJ", JOptionPane.ERROR_MESSAGE);
			Controller.getInstance().closePartyDJ();
		}
	}
}
