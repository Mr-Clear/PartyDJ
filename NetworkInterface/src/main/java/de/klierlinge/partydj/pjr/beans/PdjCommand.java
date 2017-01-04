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
		/** Spielt das aktuelle Lied von Anfang an.*/
		Start,
		/** Stopt den Player und spult zum Anfang des Liedes.*/
		Stop,
		/** Startet den player an der aktuellen Position.*/
		Play,
		/** Spielt den nächsten Track.*/
		Next,
		/** Spielt den vorherigen Track.*/
		Previous,
		/** Stopt den Player.*/
		Pause,
		/** Führt je nach Zustand Stop() oder Play() aus.*/
		PlayPause,
		/** Blendet an der aktuellen Position langsam ein.*/
		FadeIn,
		/** Blendet an der aktuellen Position langsam aus.*/
		FadeOut,
		/** Führt je nach Zustand FadeIn() oder FadeOut() aus.*/
		FadeInOut
	}
}
