package data;

/**Der SettingListener empf�ngt �nderungen der Einstellungen.
 * 
 * @author Eraser
 */
public interface SettingListener
{
	/**Wird aufgerufen, wenn eine Einstellung ge�ndert wurde.
	 * 
	 * @param name Name der Einstellung.
	 * @param value Wert auf den ge�ndert wurde.
	 */
	public void settingChanged(String name, String value);
}
