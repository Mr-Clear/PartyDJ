package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Shape;
import java.awt.Window;
import java.awt.geom.RoundRectangle2D;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JWindow;



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
		this.setLocationRelativeTo(null);
		
		JLabel picture = null;
		try
		{
			picture = new JLabel(new ImageIcon("Resources/Schriftzug.png"));
		}
		catch (NullPointerException e)
		{
			picture = new JLabel("Hier fehlt ein Bild :(");
		}
		picture.setHorizontalAlignment(JLabel.CENTER);
		
		info = new JLabel("Lade PartyDJ");
		info.setFont(new Font(Font.SERIF, 0, 24));
		info.setForeground(Color.GREEN);
		info.setHorizontalAlignment(JLabel.CENTER);
		
		timer = new JLabel("0");
		timer.setFont(new Font(Font.SERIF, 0, 10));
		timer.setVerticalAlignment(JLabel.BOTTOM);
		timer.setForeground(Color.GREEN);
		timer.setHorizontalAlignment(JLabel.LEFT);
		
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.add(info, BorderLayout.CENTER);
		panel.add(timer, BorderLayout.EAST);
		panel.setBackground(getBackground());
		
		add(picture, BorderLayout.CENTER);
		add(panel, BorderLayout.SOUTH);
		
		new Timer().schedule(new UpdateTask(), 0, 40);

		
		// Ecken Abrunden
		try 
		{
			Class<?> utils = Class.forName("com.sun.awt.AWTUtilities");

			Method method = utils.getMethod("setWindowShape", Window.class, Shape.class);
			Shape shape = new RoundRectangle2D.Double(0, 0, this.getWidth(), this.getHeight(), 20, 20); 
			method.invoke(null, this, shape);
		}
		catch (NoSuchMethodException e){}
		catch (ClassNotFoundException e){}
		catch (IllegalArgumentException e){}
		catch (IllegalAccessException e){}
		catch (InvocationTargetException e){}
		
		setOpacity(0.7f);
		
		setVisible(true);
	}
	
	public void setInfo(String infoString)
	{
		info.setText(infoString);
		timer.setText(Double.toString(getElapsedTime() / 1000d));
	}
	
	public void setOpacity(float opacity)
	{
		com.sun.awt.AWTUtilities.setWindowOpacity(this, opacity);
	}
	
	public void close()
	{
		dispose();
	}

	public long getElapsedTime()
	{
		return System.currentTimeMillis() - startTime;
	}
	
	private class UpdateTask extends TimerTask  
	{
	   public void run()
	   {
			timer.setText(Double.toString(getElapsedTime() / 1000d));
	   }
	}
}
