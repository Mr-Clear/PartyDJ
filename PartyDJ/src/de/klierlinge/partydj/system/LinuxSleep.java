package de.klierlinge.partydj.system;

import java.io.IOException;
import java.nio.charset.Charset;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.TimeUnit;
import org.apache.commons.io.IOUtils;

public class LinuxSleep
{
	/**
	 * Sends the comuter into sleep. It's written for the NAS.
	 * 
	 * It requires the script {@code /home/thomas/sleep.sh}. Content of the script is: "{@code echo mem >/sys/power/state}".
	 * The script must be able to be started with {@code sudo} without password promot.
	 * 
	 * @return Duration of sleep.
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static Duration sleep() throws IOException, InterruptedException
	{
		final Instant begin = Instant.now();
		final Process process = new ProcessBuilder("/usr/bin/sudo", "/home/thomas/sleep.sh").start(); // This call should not return before the computer goes to sleep.
		if(!process.waitFor(1, TimeUnit.SECONDS))
		{
			process.destroy();
			throw new IOException("Sleep-process did not finish in time.\n" + IOUtils.toString(process.getInputStream(), Charset.defaultCharset()));
		}
		if(process.exitValue() != 0)
		{
			throw new IOException("Sleep-process returned error:\n" + IOUtils.toString(process.getErrorStream(), Charset.defaultCharset()));
		}
		
		return Duration.between(begin, Instant.now());
	}
}
