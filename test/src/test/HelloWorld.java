package test;

import java.util.Map.Entry;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;
import java.util.Enumeration;
import java.util.ListIterator; 
import java.util.BitSet;
import java.util.Collection;
import java.util.TreeMap;
import java.util.Collections;
import java.util.Set;
import java.util.Map;






public class HelloWorld {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("hello World"+ args[0]+ args[1]);
		
		//Iterator iterator = c.iterator()
		
		Enumeration days;
		Vector daysname = new Vector();
		daysname.add("M");
		daysname.add("T");
		daysname.add("W");
		daysname.add("Thrus");
		daysname.add("Fri");
		daysname.add("Sat");
		daysname.add("Sun");
		days = daysname.elements();
		while (days.hasMoreElements())
		{
			System.out.println(days.nextElement()+ "  ");
			
		}
		
  
		Iterator iter = daysname.iterator();
		
		while (iter.hasNext())
		{
			Object element = iter.next();
			System.out.print("\n"+ element +"  ");
		}
		
		iter.remove();
		iter = daysname.iterator();
		while (iter.hasNext())
		{
			
			System.out.print("\n"+ iter.next()+"  ");
		}
		
		System.out.println("===================================================");
		
		TreeMap<String, String> treeMap = new TreeMap<String,String>();
	    treeMap.put("1", "One");
	    treeMap.put("2", "Two");
	    treeMap.put("3", "Three");
	    Set<Entry<String,String>> c = treeMap.entrySet();
	  //  Collection c = treeMap.values();
	    Iterator itr = c.iterator();

	    while (itr.hasNext()){
	    	
	      System.out.println(itr.next());
	    }
		
	}

}
