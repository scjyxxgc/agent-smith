package trainer;

import java.util.HashMap;
import java.util.Map;

import trainer.Constants.LogQueryType;
import trainer.Constants.LogSalesReportParams;

public class SalesReportLog
{
	private Map<String, Map<LogQueryType, Map<LogSalesReportParams, String[]>>> allParticipantsSalesReports;
	
	public SalesReportLog()
	{
		this.allParticipantsSalesReports = new HashMap<String, Map<LogQueryType, Map<LogSalesReportParams, String[]>>>();
	}

	public void addParticipantSalesReport(String participant, LogQueryType queryType, int day, String theImpressions, String theRevenue)
	{
		if(allParticipantsSalesReports.containsKey(participant)==false)
			allParticipantsSalesReports.put(participant, new HashMap<LogQueryType, Map<LogSalesReportParams, String[]>>());
		
		if(allParticipantsSalesReports.get(participant).containsKey(queryType)==false)
		{
			allParticipantsSalesReports.get(participant).put(queryType, new HashMap<LogSalesReportParams, String[]>());
			for (int i = 0; i < LogSalesReportParams.values().length; i++)
			{
				allParticipantsSalesReports.get(participant).get(queryType).put(LogSalesReportParams.values()[i], new String[arch.AgentConstants.TAU_SIMDAYS+1]);
			}
		}
			
		allParticipantsSalesReports.get(participant).get(queryType).get(LogSalesReportParams.impressions)[day] = theImpressions;
		allParticipantsSalesReports.get(participant).get(queryType).get(LogSalesReportParams.revenue)[day] = theRevenue;
	}
	
	/**
	 * Returns full map, which is built as follows:
	 * 
	 * All Participant --> Query Type --> Sales Report Parameter --> Results in String array arranged by day
	 * 
	 * Query Type = n_n, n_d etc', from the enum at Constant.LogQueryType
	 * Sales Report Parameter = impressions and revenue, from the enum at LogSalesReportParams
	 * 
	 * @return allParticipantsSalesReports
	 */
	public Map<String, Map<LogQueryType, Map<LogSalesReportParams, String[]>>> getAllParticipantsSalesReports()
	{
		return allParticipantsSalesReports;
	}
	
	/**
	 * Returns medium map, which is built as follows:
	 * 
	 * Participant from input --> Query Type --> Sales Report Parameter --> Results in String array arranged by day
	 * 
	 * Query Type = n_n, n_d etc', from the enum at Constant.LogQueryType
	 * Sales Report Parameter = impressions and revenue, from the enum at LogSalesReportParams
	 * 
	 * @return allParticipantsSalesReports.get(Participant)
	 */
	public Map<LogQueryType, Map<LogSalesReportParams, String[]>> getSpecificParticipantAllSalesReport(String Participant)
	{
		return allParticipantsSalesReports.get(Participant);
	}
		
	/**
	 * Returns smallest map, which is built as follows:
	 * 
	 * Participant from input --> Query Type from input --> Sales Report Parameter --> Results in String array arranged by day
	 * 
	 * Query Type = n_n, n_d etc', from the enum at Constant.LogQueryType
	 * Sales Report Parameter = impressions and revenue, from the enum at LogSalesReportParams
	 * 
	 * @return allParticipantsSalesReports.get(Participant).get(queryType)
	 */
	public Map<LogSalesReportParams, String[]> getSpecificParticipantSpecificSalesReport(String Participant, LogQueryType queryType)
	{
		return allParticipantsSalesReports.get(Participant).get(queryType);
	}	
}
