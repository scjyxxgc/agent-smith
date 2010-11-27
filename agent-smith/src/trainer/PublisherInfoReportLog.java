package trainer;

import java.util.HashMap;
import java.util.Map;

import trainer.Constants.LogPublisherInfoReportParams;

public class PublisherInfoReportLog
{
	private Map<String, Map<LogPublisherInfoReportParams, String[]>> allParticipantsPublisherInfoReports;
	
	public PublisherInfoReportLog()
	{
		this.allParticipantsPublisherInfoReports = new HashMap<String, Map<LogPublisherInfoReportParams, String[]>>();
	}

	public void addParticipantPublisherInfoReport(String participant, int day, String theSquashingParameter)
	{
		if(allParticipantsPublisherInfoReports.containsKey(participant)==false)
		{
			allParticipantsPublisherInfoReports.put(participant, new HashMap<LogPublisherInfoReportParams, String[]>());
				
			for (int i = 0; i < LogPublisherInfoReportParams.values().length; i++)
			{
				allParticipantsPublisherInfoReports.get(participant).put(LogPublisherInfoReportParams.values()[i], new String[arch.AgentConstants.TAU_SIMDAYS+1]);
			}
		}
			
		allParticipantsPublisherInfoReports.get(participant).get(LogPublisherInfoReportParams.squashingParameter)[day] = theSquashingParameter;
	}
	
	/**
	 * Returns full map, which is built as follows:
	 * 
	 * All Participant --> Publisher Info Report Parameter --> Results in String array arranged by day
	 * 
	 * Publisher Info Report Parameter = squashingParameter, from the enum at Constants.LogPublisherInfoReportParams
	 * 
	 * @return allParticipantsPublisherInfoReports
	 */
	public Map<String, Map<LogPublisherInfoReportParams, String[]>> getAllParticipantsPublisherInfoReports()
	{
		return allParticipantsPublisherInfoReports;
	}
	
	/**
	 * Returns medium map, which is built as follows:
	 * 
	 * Participant from input -->  Publisher Info Parameter --> Results in String array arranged by day
	 * 
	 * Publisher Info Report Parameter = squashingParameter, from the enum at Constants.LogPublisherInfoReportParams
	 * 
	 * @return allParticipantsPublisherInfoReports.get(Participant)
	 */
	public Map<LogPublisherInfoReportParams, String[]> getSpecificParticipantAllPublisherInfoReport(String Participant)
	{
		return allParticipantsPublisherInfoReports.get(Participant);
	}	
}
