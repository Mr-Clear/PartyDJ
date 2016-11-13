package de.klierlinge.partydj.basics;

import java.util.Date;

public interface ErrorListener
{
	void errorOccurred(int priority, Object sender, Throwable exception, String message, Date date);
}
