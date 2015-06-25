package competition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Scanner;

import org.w3c.dom.css.Counter;

public class google {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		Scanner in = new Scanner(System.in);
		int t = Integer.parseInt(in.nextLine());
		for (int i = 1; i <= t; i++) {
			int n = Integer.parseInt(in.nextLine());
			String string = in.nextLine();
			String[] str = string.split(" ");
			ArrayList<Integer> arr = new ArrayList<Integer>();
			
			for (int j = 0; j < n; j++) {
				arr.add(Integer.parseInt(str[j]));
			}
			int max = Collections.max(arr);
			
			int pos = arr.indexOf(max);
			int counter = 0;
			while(max > 0)
			{
				if(max==2)
				{
					counter=counter+2;
					break;
				}
				counter++;
				if(max % 2 == 0 )
				{
					arr.remove(pos);
					
					arr.add(max/2);
					arr.add(max/2);
				}
				else {
					ArrayList<Integer> temp = new ArrayList<Integer>();
					temp.addAll(arr);
					arr.clear();
					for (Iterator iterator = temp.iterator(); iterator.hasNext();) {
						Integer integer = (Integer) iterator.next();
						if((integer-1) > 0)
							arr.add(integer -1);
						
					}
				}
				if((n = arr.size()) >0)
				{
					max = Collections.max(arr);
					pos = arr.indexOf(max);
					n = arr.size();
				}
				else {
					break;
				}
			}
			System.out.println("Case #"+i+": "+counter);
		}
		
		
		
	}

}
