package it.cnr.isti.labse.cmb.settings;

public class DroolsUtils {
	
	public static boolean strictlyFollows(int firstEventID, int secondEventID)
	{
		if (secondEventID - firstEventID == 1)
		{
			return true;
		}
		else
			return false;
	}
	
	public static long latency(long firstEvent, long secondEvent)
	{
		return secondEvent-firstEvent;
	}
	
	public static boolean strictlyFollows(int firstEventID, int secondEventID, int thirdEventID)
	{
		if ((secondEventID - firstEventID == 1) && (thirdEventID - secondEventID == 1))
		{
			
			return true;
		}
		else
			return false;
	}
	
	public static double evaluateProb(int eventToEvaluateOccurrencies, int totalEventsNumber)
	{
		if (eventToEvaluateOccurrencies != 0 && totalEventsNumber != 0)
		{
			return (eventToEvaluateOccurrencies*100) / totalEventsNumber;
		}
		else
			return 0;
	}
	
	public static double ratio (int one, int two)
	{
		if (one != 0 && two != 0)
		{
			return (double)one/two;
		}
		return 0;
	}
}
