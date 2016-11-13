package de.klierlinge.partydj.pjr.beans;

import flexjson.JSON;

public class PdjCommand extends Message
{
	public final Command commmand;

	/* Required for flexjson. */
	public PdjCommand()
	{
		commmand = null;
	}
	
	public PdjCommand(final Command command)
	{
		this.commmand = command;
	}
	
	@JSON
	public Command getCommmand()
	{
		return commmand;
	}

	@Override
	public MessageType getType()
	{
		return Message.MessageType.PdjCommand;
	}
	
	@Override
	public String toString()
	{
		return commmand.toString();
	}
	
	public enum Command
	{
		Play, Pause, Stop, Next, Previous
	}
}
