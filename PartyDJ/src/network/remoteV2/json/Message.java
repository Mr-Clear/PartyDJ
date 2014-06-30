package network.remoteV2.json;

import flexjson.JSON;

public interface Message
{
	@JSON
	String getType();
}