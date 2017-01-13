package de.klierlinge.partydj.basics;

import org.apache.logging.log4j.core.LogEvent;

public interface ErrorListener
{
	void errorOccurred(LogEvent event);
}
