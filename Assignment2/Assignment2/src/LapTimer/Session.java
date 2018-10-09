package LapTimer;

import java.util.Iterator;
import java.util.Vector;

public class Session {

	Vector<Lap> laps = new Vector<Lap>();
	
	public Session() 
	{

		laps = new Vector<Lap>();

	}
	
	public void addLap(Lap l) 
	{
		
		laps.add(l);
		
	}
	
	public float calculateAverageTime() 
	{
		float average = 0.0f;
		float sum = 0.0f;
		/* This method should calculate the average time of all laps in the collection. It needs to return a float value */
		for(int i =0 ;i<laps.size();i++)
		{
			 sum =+ laps.get(i).getLapTime();
		}
		average = sum / laps.size();
		return average;
	}
	
	
	public Lap getFastestLap() 
	{
		
		/* This method should step through the collection, and return the Lap object whose lap time is smallest (fastest). */
		Lap fastest = laps.get(0);
		
		for(int i =0 ;i<laps.size();i++)
		{
			     if(laps.get(i).getLapTime()< fastest.getLapTime())
			    	 fastest = laps.get(i);
		}
		
		return fastest;   
		
	}
	
	public Lap getSlowestLap() 
	{
		
		/* This method should step through the collection, and return the Lap object whose lap time is largest (slowest). */
		Lap slowest = laps.get(0);
		
		for(int i = 0 ;i<laps.size();i++)
		{
			     if(laps.get(i).getLapTime()> slowest.getLapTime())
			    	 slowest = laps.get(i);
		}
		
		return slowest;   
		
	}

}
