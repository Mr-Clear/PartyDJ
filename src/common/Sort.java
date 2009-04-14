package common;

import lists.EditableListModel;
import lists.ListException;
import gui.PDJList;

public class Sort
{
	private static PDJList list = null;
	private static EditableListModel lm;
	
	public Sort(PDJList list)
	{
		Sort.list = list;
	}
	
	public static void setSource(PDJList list)
	{
		Sort.list = list;
	}
	
	public static void quickSort(int lo, int hi)
	{
		if(list != null)
		{
			if(list.getListModel() instanceof EditableListModel)
			{
				lm = (EditableListModel) list.getListModel();
				if(lo < hi)
				{
					try
					{
						int pivotPos = partition(lo, hi);
						quickSort(lo, pivotPos - 1);
						quickSort(pivotPos + 1, hi);
					}
					catch (ListException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
			}
		}
		else
			System.out.println("Sort.quickSort() kein passendes if. lo:  " + lo + "  hi:  " + hi + "  listModel:  " + list.getListModel());
	}
	
	private static int partition(int lo, int hi) throws ListException
	{
		int left = lo;
		int right = hi;
		String pivot = lm.getElementAt(hi).name;
		
		while(left < right)
		{
			while(left < right && pivot.compareToIgnoreCase(lm.getElementAt(left).name) > 0)
				left++;
			
			while(left < right && pivot.compareToIgnoreCase(lm.getElementAt(right).name) <= 0)
				right--;
			
			if(left < right)
			{
				lm.swap(left, right);
			}
		}
		
		lm.swap(left, hi);
		
		return left;
	}
}
