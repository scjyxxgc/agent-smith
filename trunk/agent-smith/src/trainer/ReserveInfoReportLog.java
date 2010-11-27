package trainer;

import java.util.HashMap;
import java.util.Map;

import trainer.Constants.LogReserveInfoReportParams;

public class ReserveInfoReportLog
{
	private Map<String, Map<LogReserveInfoReportParams, String[]>> allParticipantsReserveInfoReports;
	
	public ReserveInfoReportLog()
	{
		this.allParticipantsReserveInfoReports = new HashMap<String, Map<LogReserveInfoReportParams, String[]>>();
	}

	public void addParticipantReserveInfoReport(String participant, int day, String thePromotedReserve, String theRegularReserve)
	{
		if(allParticipantsReserveInfoReports.containsKey(participant)==false)
		{
			allParticipantsReserveInfoReports.put(participant, new HashMap<LogReserveInfoReportParams, String[]>());
				
			for (int i = 0; i < LogReserveInfoReportParams.values().length; i++)
			{
				allParticipantsReserveInfoReports.get(participant).put(LogReserveInfoReportParams.values()[i], new String[arch.AgentConstants.TAU_SIMDAYS+1]);
			}
		}
			
		allParticipantsReserveInfoReports.get(participant).get(LogReserveInfoReportParams.promotedReserve)[day] = thePromotedReserve;
		allParticipantsReserveInfoReports.get(participant).get(LogReserveInfoReportParams.regularReserve)[day] = theRegularReserve;
	}
	
	/**
	 * Returns full map, which is built as follows:
	 * 
	 * All Participant --> Reserve Info Report Parameter --> Results in String array arranged by day
	 * 
	 * Reserve Info Report Parameter = promotedReserve, regularReserve, from the enum at Constants.LogReserveInfoReportParams
	 * 
	 * @return allParticipantsReserveInfoReports
	 */
	public Map<String, Map<LogReserveInfoReportParams, String[]>> getAllParticipantsReserveInfoReports()
	{
		return allParticipantsReserveInfoReports;
	}
	
	/**
	 * Returns medium map, which is built as follows:
	 * 
	 * Participant from input -->  Reserve Info Parameter --> Results in String array arranged by day
	 * 
	 * Reserve Info Report Parameter = promotedReserve, regularReserve, from the enum at Constants.LogReserveInfoReportParams
	 * 
	 * @return allParticipantsReserveInfoReports.get(Participant)
	 */
	public Map<LogReserveInfoReportParams, String[]> getSpecificParticipantAllReserveInfoReport(String Participant)
	{
		return allParticipantsReserveInfoReports.get(Participant);
	}	
}
