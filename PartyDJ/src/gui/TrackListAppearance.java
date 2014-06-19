package gui;

import java.awt.Color;
import java.awt.Font;
import javax.swing.JList;
import javax.swing.border.Border;

/**Beschreibt das Aussehen einer PDJList
 * 
 * @author Eraser
 *
 */
public class TrackListAppearance
{
	protected final Color[][][] colors = new Color[3][2][2];
	protected Font font;
	protected Border focusBorder;
	
	/** Erstellt eine TrackListAppearance mit vorgegebenem Aussehen.
	 * @param nnf Normal, Normal, Foreground.
	 * @param nnb Normal, Normal, Background.
	 * @param nsf Normal, Selected, Foreground.
	 * @param nsb Normal, Selected, Background.
	 * @param pnf Playing, Normal, Foreground.
	 * @param pnb Playing, Normal, Background.
	 * @param psf Playing, Selected, Foreground.
	 * @param psb Playing, Selected, Background.
	 * @param rnf Problem, Normal, Foreground.
	 * @param rnb Problem, Normal, Background.
	 * @param rsf Problem, Selected, Foreground.
	 * @param rsb Problem, Selected, Background.
	 * @param font Schriftart.
	 * @param focusBorder Rahmen um den ausgewählten Eintrag.
	 */
	public TrackListAppearance(final Color nnf, final Color nnb, final Color nsf, final Color nsb,
	                           final Color pnf, final Color pnb, final Color psf, final Color psb,
	                           final Color rnf, final Color rnb, final Color rsf, final Color rsb,
	                           final Font font, final Border focusBorder)
	{
		setColor(TrackState.Normal, EntryState.Normal, Part.Foreground, nnf);
		setColor(TrackState.Normal, EntryState.Normal, Part.Background, nnb);
		setColor(TrackState.Normal, EntryState.Selected, Part.Foreground, nsf);
		setColor(TrackState.Normal, EntryState.Selected, Part.Background, nsb);
		setColor(TrackState.Playing, EntryState.Normal, Part.Foreground, pnf);
		setColor(TrackState.Playing, EntryState.Normal, Part.Background, pnb);
		setColor(TrackState.Playing, EntryState.Selected, Part.Foreground, psf);
		setColor(TrackState.Playing, EntryState.Selected, Part.Background, psb);
		setColor(TrackState.Problem, EntryState.Normal, Part.Foreground, rnf);
		setColor(TrackState.Problem, EntryState.Normal, Part.Background, rnb);
		setColor(TrackState.Problem, EntryState.Selected, Part.Foreground, rsf);
		setColor(TrackState.Problem, EntryState.Selected, Part.Background, rsb);
		this.font = font;
		this.focusBorder = focusBorder;
	}
	
	public TrackListAppearance setColor(final TrackState trackState, final EntryState entryState, final Part part, final Color color)
	{
		colors[trackState.toInt()][entryState.toInt()][part.toInt()] = color;
		return this;
	}
	
	public Color getColor(final TrackState trackState, final EntryState entryState, final Part part)
	{
		return colors[trackState.toInt()][entryState.toInt()][part.toInt()];
	}
	
	public TrackListAppearance setFont(final Font font)
	{
		this.font = font;
		return this;
	}
	
	public Font getFont()
	{
		return font;
	}
	
	public TrackListAppearance setFocusBorder(final Border focusBorder)
	{
		this.focusBorder = focusBorder;
		return this;
	}
	
	public Border getFocusBorder()
	{
		return focusBorder;
	}
	
	public static TrackListAppearance getSimpleAppearance()
	{
		return new TrackListAppearance(Color.black, Color.white,
		                               Color.white, Color.blue,
		                               Color.blue, Color.white,
		                               Color.cyan, Color.blue,
		                               Color.gray, Color.white,
		                               Color.gray, Color.blue,
		                               new Font(Font.DIALOG, Font.PLAIN, 12),
		                               null);
	}
	
	public static TrackListAppearance getGreenAppearance(final int fontSize)
	{
		return new TrackListAppearance(Color.green, Color.black,
		                               Color.green, Color.blue.darker(),
		                               Color.cyan, Color.black,
		                               Color.cyan, Color.blue.darker(),
		                               Color.gray, Color.black,
		                               Color.gray, Color.blue.darker(),
		                               new Font(Font.DIALOG, Font.PLAIN, fontSize),
		                               null);
	}
	
	public static TrackListAppearance getFormJList(final JList<Color> list)
	{
		return new TrackListAppearance(list.getForeground(), list.getBackground(),
		                               list.getSelectionForeground(), list.getSelectionBackground(),
		                               list.getForeground().brighter(), list.getBackground(),
		                               list.getSelectionForeground().brighter(), list.getSelectionBackground(),
		                               list.getForeground().darker(), list.getBackground(),
		                               list.getSelectionForeground().darker(), list.getSelectionBackground(),
		                               list.getFont(),
		                               null);
	}
	
	public enum TrackState
	{
		Normal,
		Playing,
		Problem;
		
		private int toInt()
		{
			switch(this)
			{
			case Normal:
				return 0;
			case Playing:
				return 1;
			case Problem:
				return 2;
			}
			return -1;
		}
	}
	public enum EntryState
	{
		Normal,
		Selected;
		
		private int toInt()
		{
			switch(this)
			{
			case Normal:
				return 0;
			case Selected:
				return 1;
			}
			return -1;
		}
	}
	public enum Part
	{
		Foreground,
		Background;
		
		private int toInt()
		{
			switch(this)
			{
			case Foreground:
				return 0;
			case Background:
				return 1;
			}
			return -1;
		}
	}
}
