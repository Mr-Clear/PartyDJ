package data;

/**Der SettingListener empfängt Änderungen der Einstellungen.
 * 
 * @author Eraser
 */
public interface SettingListener
{
	/**Wird aufgerufen, wenn eine Einstellung geändert wurde.
	 * 
	 * @param name Name der Einstellung.
	 * @param value Wert auf den geändert wurde.
	 */
	public void settingChanged(String name, String value);
}
