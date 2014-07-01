package network.remoteV2.beans;

import flexjson.JSON;

public class PdjCommand extends Message
{
	public final Command comm;

	/* Required for flexjson. */
	public PdjCommand()
	{
		comm = null;
	}
	
	public PdjCommand(Command comm)
	{
		this.comm = comm;
	}
	
	@JSON
	public Command getComm()
	{
		return comm;
	}

	@Override
	public String getType()
	{
		return "Command";
	}
	
	@Override
	public String toString()
	{
		return comm.toString();
	}
	
	public enum Command
	{
		Play, Pause, Stop, Next, Previous
	}
}
