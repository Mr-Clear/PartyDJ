package network;

import java.util.HashMap;
import java.util.Map;
import basics.Controller;

class WinLircReceiverSelfSavingMap extends HashMap<String, WinLircReceiverKeyAction>
{
	private static final long serialVersionUID = 362498820763181265L;	// Von HashMap kopiert.
	
	public WinLircReceiverSelfSavingMap()
	{
		String data = Controller.getInstance().getData().readSetting("WinLIRC_Action_Map");
		if(data != null)
		{
			String pairs[] = data.split("; ");
			for(String pair : pairs)
			{
				int p = pair.indexOf(" ?-> ");
				if( p > 0)
				{
					String key = pair.substring(0, p);
					String value = pair.substring(p + 5);
					put(key, new WinLircReceiverKeyAction(value));
				}
			}
		}
	}
	
	@Override
	public WinLircReceiverKeyAction put(String key, WinLircReceiverKeyAction value)
	{
		WinLircReceiverKeyAction ret = super.put(key, value);
		save();
		return ret;
	}
	
	@Override
	public void putAll(Map<? extends String, ? extends WinLircReceiverKeyAction> m)
	{
		super.putAll(m);
		save();
	}
	
	@Override
    public WinLircReceiverKeyAction remove(Object key)
	{
		WinLircReceiverKeyAction ret = super.remove(key);
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
		StringBuilder sb = new StringBuilder();
		for(String key : keySet())
		{
			sb.append(key);
			sb.append(" ?-> ");
			sb.append(get(key).toString());
			sb.append("; ");
		}
		if(sb.length() > 0)
			sb.setLength(sb.length() - 2);
		
		Controller.getInstance().getData().writeSetting("WinLIRC_Action_Map", sb.toString());
	}
}
