package de.klierlinge.partydj.network;

import java.util.HashMap;
import java.util.Map;
import de.klierlinge.partydj.basics.Controller;

class WinLircReceiverSelfSavingMap extends HashMap<String, WinLircReceiverKeyAction>
{
	private static final long serialVersionUID = 362498820763181265L;	// Von HashMap kopiert.
	
	public WinLircReceiverSelfSavingMap()
	{
		final String data = Controller.getInstance().getData().readSetting("WinLIRC_Action_Map");
		if(data != null)
		{
			final String[] pairs = data.split("; ");
			for(final String pair : pairs)
			{
				final int p = pair.indexOf(" ?-> ");
				if(p > 0)
				{
					final String key = pair.substring(0, p);
					final String value = pair.substring(p + 5);
					put(key, new WinLircReceiverKeyAction(value));
				}
			}
		}
	}
	
	@Override
	public WinLircReceiverKeyAction put(final String key, final WinLircReceiverKeyAction value)
	{
		final WinLircReceiverKeyAction ret = super.put(key, value);
		save();
		return ret;
	}
	
	@Override
	public void putAll(final Map<? extends String, ? extends WinLircReceiverKeyAction> m)
	{
		super.putAll(m);
		save();
	}
	
	@Override
    public WinLircReceiverKeyAction remove(final Object key)
	{
		final WinLircReceiverKeyAction ret = super.remove(key);
		save();
		return ret;
    }
	
	@Override
	public void clear()
	{
		super.clear();
		save();
	}
	
	protected void save()
	{
		final StringBuilder sb = new StringBuilder();
		for(final Map.Entry<String, WinLircReceiverKeyAction> entry : entrySet())
		{
			sb.append(entry.getKey());
			sb.append(" ?-> ");
			sb.append(entry.getValue().toString());
			sb.append("; ");
		}
		if(sb.length() > 0)
			sb.setLength(sb.length() - 2);
		
		Controller.getInstance().getData().writeSetting("WinLIRC_Action_Map", sb.toString());
	}
}
