package network;

import java.io.Serializable;

class WinLircReceiverKeyAction implements Serializable
{
	private static final long serialVersionUID = 5011820361635800264L;
	public WinLircReceiverKeyAction(){}
	public WinLircReceiverKeyAction(String command, boolean repeat)
	{
		this.command = command;
		this.repeat = repeat;
	}
	WinLircReceiverKeyAction(String fromString)
	{
		String[] split = fromString.split(" ");
		repeat = Boolean.parseBoolean(split[0]);
		command = split[1];
	}
	
	public String command;
	public boolean repeat;
	
	@Override
	public String toString()
	{
		return repeat + " " + command;
	}
}