package de.klierlinge.partydj.logging;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.LogEvent;

public class LoggedMessage
{
    private final Level level;
    private final String logger;
    private final String logMessage;
    private final String exceptionMessage;
    private final String stackTrace;
    private final long timestamp;
    private final String thread;

    public LoggedMessage(final LogEvent event)
    {
        level = event.getLevel();
        logger = event.getLoggerName();
        exceptionMessage = event.getThrownProxy() == null ? null : event.getThrownProxy().getMessage();
        stackTrace = event.getThrownProxy() == null ? null : event.getThrownProxy().getExtendedStackTraceAsString();
        logMessage = event.getMessage().getFormattedMessage();
        timestamp = event.getTimeMillis();
        thread = event.getThreadName();
    }

    public Level getLevel()
    {
        return level;
    }

    public String getLogger()
    {
        return logger;
    }

    public String getExceptionMessage()
    {
        return exceptionMessage;
    }

    public String getStackTrace()
    {
        return stackTrace;
    }

    public String getLogMessage()
    {
        return logMessage;
    }

    public long getTimestamp()
    {
        return timestamp;
    }

    public String getThread()
    {
        return thread;
    }

    @Override
    public String toString()
    {
        return logMessage;
    }
}
