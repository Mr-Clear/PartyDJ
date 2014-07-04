package network.remoteV2.beans;

public class Test extends Message
{
	public final boolean echo;
	public final String content;
	
	public Test(boolean echo, String content)
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
	public String getType()
	{
		return "Test";
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
