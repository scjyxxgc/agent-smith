package trainer;

import java.util.HashMap;
import java.util.Map;

import trainer.Constants.LogBidBundleReportParams;
import trainer.Constants.LogQueryType;

public class BidBundleReportLog
{
	private Map<String, Map<LogQueryType, Map<LogBidBundleReportParams, String[]>>> allParticipantsBidBundleReports;
	
	public BidBundleReportLog()
	{
		this.allParticipantsBidBundleReports = new HashMap<String, Map<LogQueryType, Map<LogBidBundleReportParams, String[]>>>();
	}

	public void addParticipantBidBundleReport(String participant, LogQueryType queryType, int day, String theAd, String theBid, String theLimit)
	{
		if(allParticipantsBidBundleReports.containsKey(participant)==false)
			allParticipantsBidBundleReports.put(participant, new HashMap<LogQueryType, Map<LogBidBundleReportParams, String[]>>());
		
		if(allParticipantsBidBundleReports.get(participant).containsKey(queryType)==false)
		{
			allParticipantsBidBundleReports.get(participant).put(queryType, new HashMap<LogBidBundleReportParams, String[]>());
			for (int i = 0; i < LogBidBundleReportParams.values().length; i++)
			{
				allParticipantsBidBundleReports.get(participant).get(queryType).put(LogBidBundleReportParams.values()[i], new String[arch.AgentConstants.TAU_SIMDAYS+1]);
			}
		}
			
		allParticipantsBidBundleReports.get(participant).get(queryType).get(LogBidBundleReportParams.ad)[day] = theAd;
		allParticipantsBidBundleReports.get(participant).get(queryType).get(LogBidBundleReportParams.bid)[day] = theBid;
		allParticipantsBidBundleReports.get(participant).get(queryType).get(LogBidBundleReportParams.limit)[day] = theLimit;
	}
	
	/**
	 * Returns full map, which is built as follows:
	 * 
	 * All Participant --> Query Type --> Bid Bundle Report Parameter --> Results in String array arranged by day
	 * 
	 * Query Type = n_n, n_d etc', from the enum at Constant.LogQueryType
	 * Bid Bundle Report Parameter = ad, bid and limit, from the enum at LogBidBundleReportParams
	 * 
	 * @return allParticipantsBidBundleReports
	 */
	public Map<String, Map<LogQueryType, Map<LogBidBundleReportParams, String[]>>> getAllParticipantsBidBundleReports()
	{
		return allParticipantsBidBundleReports;
	}
	
	/**
	 * Returns medium map, which is built as follows:
	 * 
	 * Participant from input --> Query Type --> Bid Bundle Report Parameter --> Results in String array arranged by day
	 * 
	 * Query Type = n_n, n_d etc', from the enum at Constant.LogQueryType
	 * Bid Bundle Report Parameter = ad, bid and limit, from the enum at LogBidBundleReportParams
	 * 
	 * @return allParticipantsBidBundleReports.get(Participant)
	 */
	public Map<LogQueryType, Map<LogBidBundleReportParams, String[]>> getSpecificParticipantAllBidBundleReport(String Participant)
	{
		return allParticipantsBidBundleReports.get(Participant);
	}
		
	/**
	 * Returns smallest map, which is built as follows:
	 * 
	 * Participant from input --> Query Type from input --> Bid Bundle Report Parameter --> Results in String array arranged by day
	 * 
	 * Query Type = n_n, n_d etc', from the enum at Constant.LogQueryType
	 * Bid Bundle Report Parameter = ad, bid and limit, from the enum at LogBidBundleReportParams
	 * 
	 * @return allParticipantsBidBundleReports.get(Participant).get(queryType)
	 */
	public Map<LogBidBundleReportParams, String[]> getSpecificParticipantSpecificBidBundleReport(String Participant, LogQueryType queryType)
	{
		return allParticipantsBidBundleReports.get(Participant).get(queryType);
	}	
}
