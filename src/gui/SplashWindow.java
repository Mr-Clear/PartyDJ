package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Shape;
import java.awt.Window;
import java.awt.geom.RoundRectangle2D;
import java.lang.reflect.Method;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JWindow;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;


/**
 * Splash-Fenster das wärend dem Start des PartyDJ angezeigt wird.
 * 
 * @author Eraser
 *
 */
public class SplashWindow extends JWindow
{
	private static final long serialVersionUID = 2036905786871782351L;
	private JLabel info;
	private JLabel timer;
	private long startTime;

	public SplashWindow()
	{
		startTime = System.currentTimeMillis();

		setLayout(new BorderLayout());
		getContentPane().setBackground(Color.DARK_GRAY);
		setSize(757, 321 + 32);
		setLocationRelativeTo(null);
		
		JLabel picture = null;
		try
		{
			picture = new JLabel(new ImageIcon("Resources/Schriftzug.png"));
		}
		catch (NullPointerException e)
		{
			picture = new JLabel("Hier fehlt ein Bild :(");
		}
		picture.setHorizontalAlignment(SwingConstants.CENTER);
		
		info = new JLabel("Lade PartyDJ");
		info.setFont(new Font(Font.SERIF, 0, 24));
		info.setForeground(Color.GREEN);
		info.setHorizontalAlignment(SwingConstants.CENTER);
		
		timer = new JLabel("0");
		timer.setFont(new Font(Font.SERIF, 0, 10));
		timer.setVerticalAlignment(SwingConstants.BOTTOM);
		timer.setForeground(Color.GREEN);
		timer.setHorizontalAlignment(SwingConstants.LEFT);
		
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.add(info, BorderLayout.CENTER);
		panel.add(timer, BorderLayout.EAST);
		panel.setBackground(getBackground());
		
		add(picture, BorderLayout.CENTER);
		add(panel, BorderLayout.SOUTH);
		
		new Timer().schedule(new UpdateTask(), 0, 40);

		
		// Ecken Abrunden
		//com.sun.awt.AWTUtilities.setWindowShape(me, new RoundRectangle2D.Double(0, 0, me.getWidth(), me.getHeight(), 20, 20));				
		try 
		{
			Class<?> utils = Class.forName("com.sun.awt.AWTUtilities");
			Method method = utils.getMethod("setWindowShape", Window.class, Shape.class);
			Shape s = new RoundRectangle2D.Double(0, 0, this.getWidth(), this.getHeight(), 20, 20);
			method.invoke(this, this, s);
		}
		catch(Exception ignored){}

		setOpacity(.8f);
		setVisible(true);
	}
	
	public void setInfo(final String infoString)
	{
		SwingUtilities.invokeLater(new Runnable(){
			@Override
			public void run()
			{
				info.setText(infoString);
				timer.setText(Double.toString(getElapsedTime() / 1000d));
			}});
		
	}
	
	public void setOpacity(final float opacity)
	{
		SwingUtilities.invokeLater(new Runnable(){
			@Override
			public void run()
			{
				//com.sun.awt.AWTUtilities.setWindowOpacity(SplashWindow.this, opacity);
				try 
				{
					Class<?> utils = Class.forName("com.sun.awt.AWTUtilities");
					Method method = utils.getMethod("setWindowOpacity", Window.class, float.class);
					method.invoke(this, SplashWindow.this, opacity);
				}
				catch(Exception ignored){}
			}});
		
	}
	
	public void close()
	{
		SwingUtilities.invokeLater(new Runnable(){
			@Override
			public void run()
			{
				dispose();
			}});
	}

	public long getElapsedTime()
	{
		return System.currentTimeMillis() - startTime;
	}
	
	private class UpdateTask extends TimerTask  
	{
		@Override
		public void run()
		{
			timer.setText(Double.toString(getElapsedTime() / 1000d));
		}
	}
}
