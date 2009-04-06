package common;

import java.util.ArrayList;
import lists.EditableListModel;
import lists.ListException;
import gui.PDJList;

public class QuickSort
{
	private static PDJList list = null;
	
	public static void setSource(PDJList list)
	{
		QuickSort.list = list;
	}
	
	public static void sort(int left, int right)
	{
		if(list != null)
		{
			if(left < right)
			{
				int divisor = divide(left, right);
				sort(left, divisor - 1);
				sort(divisor + 1, right);
			}
		}
	}
	private static int divide(int left, int right)
	{
		int l = left;
		int r = right - 2;
		
		System.out.println("right =  " + right + "   r =   " + r);
		System.out.println("left =  " + left + "   l =   " + l);
		
		Track[] tracks = list.getSelectedValues();
		ArrayList<String> data = new ArrayList<String>();
		EditableListModel lm = null;
			if(list.getListModel() instanceof EditableListModel)
				lm = (EditableListModel)list.getListModel();
		
		for(int i = 0; i < tracks.length; i++)
		{
			data.add(tracks[i].name);
		}
		String pivot = data.get(right - 1);
		
		while(l < r)
		{
			System.out.println("while-Schleife: l < r" + (pivot.charAt(0) < data.get(l).charAt(0)) + pivot.charAt(0) + data.get(l).charAt(0));
			while(l < r && pivot.charAt(0) < data.get(l).charAt(0))//pivot.compareTo(data.get(l)) > 0)
			{
				l++;
				System.out.println("while-Schleife: compareTo1   " + l);
			}

			while(r > l && pivot.charAt(0) > data.get(l).charAt(0))//data.get(r).compareTo(pivot) >= 0)
			{
				r--;
				System.out.println("while-Schleife: compareTo2   " + r);
			}
			
			if(l < r)
			{
				try
				{
					lm.swap(l, r);
					System.out.println("swapped");
				}
				catch (ListException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}
		//if(data.get(l).compareTo(pivot) > 0)
		{
			try
			{
				lm.swap(l, right - 1);
				System.out.println("pivot swap");
			}
			catch (ListException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return l;
	}
}
