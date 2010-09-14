package it.cnr.isti.labse.cmb.settings;

public class DroolsUtils {
	
	public static boolean strictlyFollows(int aEventID, int bEventID)
	{
		if (bEventID - aEventID == 1)
		{
			return true;
		}
		else
			return false;
	}

	public static boolean strictlyFollows(int aEventID, int bEventID, int cEventID)
	{
		if ((bEventID - aEventID == 1) && (cEventID - bEventID == 1))
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
}
