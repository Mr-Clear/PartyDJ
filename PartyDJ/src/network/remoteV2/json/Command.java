package network.remoteV2.json;

import flexjson.JSON;

public class Command implements Message
{
	String c;
	
	public Command()
	{
		// TODO Auto-generated constructor stub
	}
	
	public Command(String c)
	{
		this.c = c;
	}
	
	@JSON
	public String getC()
	{
		return c;
	}

	@Override
	public String getType()
	{
		return "Command";
	}
	
	@Override
	public String toString()
	{
		return c;
	}
}
