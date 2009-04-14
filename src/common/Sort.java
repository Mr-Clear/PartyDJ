package common;

import lists.EditableListModel;
import lists.ListException;
import gui.PDJList;
import gui.SortMode;

public class Sort
{
	public static void quickSort(PDJList list, SortMode sm)
	{
		if(sm == null || list == null)
			throw new NullPointerException();
	
		TrackComperator comperator = null;
		
		switch(sm)
		{
			case NAME:		comperator = new CompareName();
							break;
			case DURATION:	comperator = new CompareDuration();
							break;
		}
		qSort(0, list.getListModel().getSize() - 1, list, comperator);
	}
	
	private static void qSort(int lo, int hi, PDJList list, TrackComperator comperator)
	{
		if(list.getListModel() instanceof EditableListModel)
		{
				if(lo < hi)
				{
					try
					{
						int pivotPos = partition(lo, hi, list, comperator);
						qSort(lo, pivotPos - 1, list, comperator);
						qSort(pivotPos + 1, hi, list, comperator);
					}
					catch (ListException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
		}
	}
	
	private static int partition(int lo, int hi, PDJList list, TrackComperator comperator) throws ListException
	{
		int left = lo;
		int right = hi;
		EditableListModel lm = null;
		
		if(list.getListModel() instanceof EditableListModel)
			lm = (EditableListModel) list.getListModel();
		
		Track pivot = lm.getElementAt(hi);
		
		while(left < right)
		{
			while(left < right && comperator.compare(pivot, lm.getElementAt(left)) > 0)
				left++;
			
			while(left < right && comperator.compare(pivot, lm.getElementAt(right)) <= 0)
				right--;
			
			if(left < right)
				lm.swap(left, right);
		}
		lm.swap(left, hi);
		
		return left;
	}
}

interface TrackComperator
{
	/**@return   Wenn a < b, dann kleiner 0.
	 * 			 Wenn a > b, dann größer 0.
	 * 			 Wenn a == b, dann 0.
	 * @param a
	 * @param b
	 * @return
	 */
	int compare(Track a, Track b);
}

class CompareName implements TrackComperator
{
	@Override
	public int compare(Track a, Track b)
	{
		return a.name.compareTo(b.name);
	}
}

class CompareDuration implements TrackComperator
{
	@Override
	public int compare(Track a, Track b)
	{
		if(a.duration < b.duration)
			return -1;
		else if(a.duration > b.duration)
			return 1;
		else
			return 0;
	}
}