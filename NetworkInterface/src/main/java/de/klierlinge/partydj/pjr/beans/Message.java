package de.klierlinge.partydj.pjr.beans;

import flexjson.JSON;
import flexjson.JSONDeserializer;
import flexjson.locators.TypeLocator;

/** Basisklasse aller Nachrichten. */
public abstract class Message
{
	/**
	 * @return Typ der Nachricht.
	 */
	@JSON
	public abstract MessageType getType();
	
	/** Konfiguriert Felxjson, um mit der Typhirachie zurecht zu kommen. 
	 * @param deserializer Zu konfigurierender JSONDeserializer.*/
	public static void configureDeserializer(final JSONDeserializer<Message> deserializer)
	{
		final TypeLocator<String> typeLocator = new TypeLocator<>("type");
		for(final MessageType child : MessageType.values())
		{
			try
			{
			    final Class<? extends Message> c = MessageType.childToClass(child);
				typeLocator.add((c.newInstance()).getType().toString(), c);
			}
			catch(InstantiationException | IllegalAccessException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		deserializer.use(".", typeLocator);
	}
	
	public static enum MessageType
	{
	    TrackList,
	    PdjCommand,
	    Test,
	    DataRequest,
	    Setting,
	    InitialData,
		Track,
		LiveData,
	    ;
	    
	    static Class<? extends Message> childToClass(final MessageType child)
	    {
	        switch(child)
	        {
            case TrackList:
                return TrackList.class;
            case PdjCommand:
                return PdjCommand.class;
            case Test:
                return Test.class;
            case DataRequest:
                return DataRequest.class;
            case Setting:
                return Setting.class;
            case InitialData:
                return InitialData.class;
            case Track:
                return Track.class;
            case LiveData:
                return LiveData.class;
	        }
	        throw new RuntimeException("Unknown message type.");
	    }
	}
	
	@Override
	public String toString()
	{
	    return getType().toString();
	}
}