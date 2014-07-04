package network.remoteV2.beans;

import flexjson.JSON;
import flexjson.JSONDeserializer;
import flexjson.locators.TypeLocator;

/** Basisklasse aller Nachrichten. */
public abstract class Message
{
	/**
	 * @return Name der Nachricht.
	 */
	@JSON
	public abstract String getType();
	
	/** Liste aller möglichen Nachrichten. */
	private static final Class<?>[] CHILDREN = new Class<?>[]{TrackList.class, PdjCommand.class, Test.class};
	
	/** Konfiguriert Felxjson, um mit der Typhirachie zurecht zu kommen. 
	 * @param deserializer Zu konfigurierender JSONDeserializer.*/
	public static void configureDeserializer(JSONDeserializer<Message> deserializer)
	{
		TypeLocator<String> typeLocator = new TypeLocator<>("type");
		for(Class<?> c : CHILDREN)
		{
			try
			{
				typeLocator.add(((Message)c.newInstance()).getType(), c);
			}
			catch(InstantiationException | IllegalAccessException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		deserializer.use(".", typeLocator);
	}
}