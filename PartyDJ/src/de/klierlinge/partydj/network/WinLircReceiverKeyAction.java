package de.klierlinge.partydj.network;

import java.io.Serializable;

public class WinLircReceiverKeyAction implements Serializable
{
	private static final long serialVersionUID = 5011820361635800264L;

	protected String command;
	protected boolean repeat;
	
	public WinLircReceiverKeyAction() {}
	public WinLircReceiverKeyAction(final String command, final boolean repeat)
	{
		this.command = command;
		this.repeat = repeat;
	}
	WinLircReceiverKeyAction(final String fromString)
	{
		final String[] split = fromString.split(" ");
		repeat = Boolean.parseBoolean(split[0]);
		command = split[1];
	}
	
	@Override
	public String toString()
	{
		return repeat + " " + command;
	}
}