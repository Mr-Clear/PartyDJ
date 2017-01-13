package de.klierlinge.partydj.logging;

import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import de.klierlinge.partydj.basics.Controller;

/**
 * Sends log events to PartyDj Controller.
 */
@Plugin(name = "ParyDjLogAppender", category = "Core", elementType = "appender", printObject = true)
public class ParyDjLogAppender extends AbstractAppender
{
	private Controller controller;

	@PluginFactory
	public static ParyDjLogAppender createAppender(@PluginAttribute("name") String name, @PluginElement("Filters") Filter filter)
	{
		return new ParyDjLogAppender(name, filter);
	}

	public ParyDjLogAppender(String name, Filter filter)
	{
		super(name, filter, null);
	}

	@Override
	public void append(LogEvent event)
	{
		if (controller == null)
		{
            if(!Controller.isLoaded())
            {
                LOGGER.debug("Can't log event in PartyDj because controller is not loaded: " + event.getMessage());
                return;
            }
			controller = Controller.getInstance();
		}
		controller.logEvent(event);
	}
}
