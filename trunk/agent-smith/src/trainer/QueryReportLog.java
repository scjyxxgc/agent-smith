package trainer;

import java.util.HashMap;
import java.util.Map;

import trainer.Constants.LogQueryReportParams;
import trainer.Constants.LogQueryType;

public class QueryReportLog
{
	private Map<String, Map<LogQueryType, Map<LogQueryReportParams, String[]>>> AllParticipantsQueryReports;
	
	public QueryReportLog()
	{
		this.AllParticipantsQueryReports = new HashMap<String, Map<LogQueryType, Map<LogQueryReportParams, String[]>>>();
	}

	public void addParticipantQueryReport(String participant, LogQueryType queryType, int day, String theAd, String thePromotedImpressions, String theRegularImpressions, String theClicks, String theCpc, String thePosition)
	{
		if(AllParticipantsQueryReports.containsKey(participant)==false)
			AllParticipantsQueryReports.put(participant, new HashMap<LogQueryType, Map<LogQueryReportParams, String[]>>());
		
		if(AllParticipantsQueryReports.get(participant).containsKey(queryType)==false)
		{
			AllParticipantsQueryReports.get(participant).put(queryType, new HashMap<LogQueryReportParams, String[]>());
			for (int i = 0; i < LogQueryReportParams.values().length; i++)
			{
				AllParticipantsQueryReports.get(participant).get(queryType).put(LogQueryReportParams.values()[i], new String[arch.AgentConstants.TAU_SIMDAYS+1]);
			}
		}
			
		AllParticipantsQueryReports.get(participant).get(queryType).get(LogQueryReportParams.ad)[day] = theAd;
		AllParticipantsQueryReports.get(participant).get(queryType).get(LogQueryReportParams.promotedImpressions)[day] = thePromotedImpressions;
		AllParticipantsQueryReports.get(participant).get(queryType).get(LogQueryReportParams.regularImpressions)[day] = theRegularImpressions;
		AllParticipantsQueryReports.get(participant).get(queryType).get(LogQueryReportParams.clicks)[day] = theClicks;
		AllParticipantsQueryReports.get(participant).get(queryType).get(LogQueryReportParams.cpc)[day] = theCpc;
		AllParticipantsQueryReports.get(participant).get(queryType).get(LogQueryReportParams.position)[day] = thePosition;
	}
	
	/**
	 * Returns full map, which is built as follows:
	 * 
	 * All Participant --> Query Type --> Query Report Parameter --> Results in String array arranged by day
	 * 
	 * Query Type = n_n, n_d etc', from the enum at Constant.LogQueryType
	 * Query Report Parameter = ad, cpc, position etc', from the enum at Constant.LogQueryReportParams
	 * 
	 * @return AllParticipantsQueryReports
	 */
	public Map<String, Map<LogQueryType, Map<LogQueryReportParams, String[]>>> getAllParticipantsQueryReports()
	{
		return AllParticipantsQueryReports;
	}
	
	/**
	 * Returns medium map, which is built as follows:
	 * 
	 * Participant from input --> Query Type --> Query Report Parameter --> Results in String array arranged by day
	 * 
	 * Query Type = n_n, n_d etc', from the enum at Constant.LogQueryType
	 * Query Report Parameter = ad, cpc, position etc', from the enum at Constant.LogQueryReportParams
	 * 
	 * @return AllParticipantsQueryReports.get(Participant)
	 */
	public Map<LogQueryType, Map<LogQueryReportParams, String[]>> getSpecificParticipantAllQueryReport(String Participant)
	{
		return AllParticipantsQueryReports.get(Participant);
	}
		
	/**
	 * Returns smallest map, which is built as follows:
	 * 
	 * Participant from input --> Query Type from input --> Query Report Parameter --> Results in String array arranged by day
	 * 
	 * Query Type = n_n, n_d etc', from the enum at Constant.LogQueryType
	 * Query Report Parameter = ad, cpc, position etc', from the enum at Constant.LogQueryReportParams
	 * 
	 * @return AllParticipantsQueryReports.get(Participant).get(queryType)
	 */
	public Map<LogQueryReportParams, String[]> getSpecificParticipantSpecificQueryReport(String Participant, LogQueryType queryType)
	{
		return AllParticipantsQueryReports.get(Participant).get(queryType);
	}	
}
