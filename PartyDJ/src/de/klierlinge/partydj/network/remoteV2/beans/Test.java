package de.klierlinge.partydj.network.remoteV2.beans;

public class Test extends Message
{
	public final boolean echo;
	public final String content;
	
	public Test(final boolean echo, final String content)
	{
		this.echo = echo;
		this.content = content;
	}
	
	/** Required for flexjson. */
	public Test()
	{
		echo = false;
		content = null;
	}
	
	@Override
	public Child getType()
	{
		return Message.Child.Test;
	}
	
	public boolean getEcho()
	{
		return echo;
	}
	
	public String getContent()
	{
		return content;
	}

	@Override
	public String toString()
	{
		return content + "(" + echo + ")";
	}
}
