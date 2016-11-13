package de.klierlinge.partydj.common;

import java.util.Random;
import de.klierlinge.partydj.gui.PDJList;
import de.klierlinge.partydj.lists.EditableListModel;
import de.klierlinge.partydj.lists.ListException;

/**
 * Sortiert eine PDJList mit dem Quick-Sort-Verfahren.
 * 
 * @author Sam
 */
public final class Sort
{
	private Sort(){}
	
	public static void quickSort(final PDJList list, final SortMode sm) throws ListException
	{
		if(sm == null || list == null)
			throw new NullPointerException();
		
		if(!(list.getListModel() instanceof EditableListModel))
			throw new IllegalArgumentException("Liste nicht editierbar.");
		
		final EditableListModel lm = (EditableListModel)list.getListModel();
		if(lm.getSize() <= 1)
			return;
	
		TrackComperator comperator = null;
		
		switch(sm)
		{
		case NAME:
			comperator = new CompareName();
			break;
		case DURATION:
			comperator = new CompareDuration();
			break;
		case PATH:
			comperator = new ComparePath();
			break;
		}
		synchronized(lm)
		{
			qSort(0, lm.getSize() - 1, lm, comperator);
			lm.swap(0, 0, false);
		}
	}
	
	public static void shuffle(final PDJList list) throws ListException
	{
		synchronized(list.getListModel())
		{
			if(list.getListModel() instanceof EditableListModel && list.getListModel().getSize() > 1)
			{
				final EditableListModel lm = (EditableListModel) list.getListModel();
				final Random random = new Random();
				for(int i = 0; i < lm.getSize(); i++)
					lm.swap(i, random.nextInt(lm.getSize() - 1), i < lm.getSize() - 1);
			}
		}
	}
	
	private static void qSort(final int lo, final int hi, final EditableListModel lm, final TrackComperator comperator) throws ListException
	{
		if(lo < hi)
		{
			final int pivotPos = partition(lo, hi, lm, comperator);
			qSort(lo, pivotPos - 1, lm, comperator);
			qSort(pivotPos + 1, hi, lm, comperator);
		}
	}
	
	private static int partition(final int lo, final int hi, final EditableListModel lm, final TrackComperator comperator) throws ListException
	{
		int left = lo;
		int right = hi;
				
		final Track pivot = lm.getElementAt(hi);
		
		while(left < right)
		{
			while(left < right && comperator.compare(pivot, lm.getElementAt(left)) > 0)
				left++;
			
			while(left < right && comperator.compare(pivot, lm.getElementAt(right)) <= 0)
				right--;
			
			if(left < right)
				lm.swap(left, right, true);
		}
		lm.swap(left, hi, true);
		
		return left;
	}
}

interface TrackComperator
{
	/**@param a Erster zu vergleichendenr Track.
	 * @param b Zweiter zu vergleichendenr Track.
	 * @return   Wenn a < b, dann kleiner 0.
	 * 			 Wenn a > b, dann größer 0.
	 * 			 Wenn a == b, dann 0.
	 */
	int compare(Track a, Track b);
}

class CompareDirect implements TrackComperator
{
	@Override
	public int compare(final Track a, final Track b)
	{
		return a.compareTo(b);
	}
}

class CompareName implements TrackComperator
{
	@Override
	public int compare(final Track a, final Track b)
	{
		return a.name.compareToIgnoreCase(b.name);
	}
}

class CompareDuration implements TrackComperator
{
	@Override
	/**@return  Wenn Dauer a < b, dann -1.
	 * 			Wenn Dauer a > b, dann 1.
	 * 			Wenn Dauer a == b, dann 0.
	 */
	public int compare(final Track a, final Track b)
	{
		return (int)Math.round(a.getDuration() - b.getDuration());
	}
}

class ComparePath implements TrackComperator
{
	@Override
	public int compare(final Track a, final Track b)
	{
		return a.getPath().compareToIgnoreCase(b.getPath());
	}
}