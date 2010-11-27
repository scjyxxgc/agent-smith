package trainer;

import java.util.HashMap;
import java.util.Map;

import trainer.Constants.LogQueryType;
import trainer.Constants.LogUserClickModelReportParams;

public class UserClickModelReportLog
{
	private Map<String, Map<LogQueryType, Map<LogUserClickModelReportParams, String[]>>> allParticipantsUserClickModel_AdvertiserEffect_Reports;
	private Map<LogQueryType, Map<LogUserClickModelReportParams, String[]>> allParticipantsUserClickModel_ContinuationProbability_Reports;
	
	public UserClickModelReportLog()
	{
		this.allParticipantsUserClickModel_AdvertiserEffect_Reports = new HashMap<String, Map<LogQueryType, Map<LogUserClickModelReportParams, String[]>>>();
		this.allParticipantsUserClickModel_ContinuationProbability_Reports = new HashMap<LogQueryType, Map<LogUserClickModelReportParams, String[]>>();
	}

	public void addParticipantUserClickModelAdvertiserEffectReport(String participant, LogQueryType queryType, int day, String theAdvertiserEffect)
	{
		if(allParticipantsUserClickModel_AdvertiserEffect_Reports.containsKey(participant)==false)
			allParticipantsUserClickModel_AdvertiserEffect_Reports.put(participant, new HashMap<LogQueryType, Map<LogUserClickModelReportParams, String[]>>());
		
		if(allParticipantsUserClickModel_AdvertiserEffect_Reports.get(participant).containsKey(queryType)==false)
		{
			allParticipantsUserClickModel_AdvertiserEffect_Reports.get(participant).put(queryType, new HashMap<LogUserClickModelReportParams, String[]>());
			for (int i = 0; i < LogUserClickModelReportParams.values().length; i++)
			{
				allParticipantsUserClickModel_AdvertiserEffect_Reports.get(participant).get(queryType).put(LogUserClickModelReportParams.values()[i], new String[arch.AgentConstants.TAU_SIMDAYS+1]);
			}
		}
			
		allParticipantsUserClickModel_AdvertiserEffect_Reports.get(participant).get(queryType).get(LogUserClickModelReportParams.advertiserEffect)[day] = theAdvertiserEffect;
	}
	
	public void addUserClickModelContinuationProbabilityReport(LogQueryType queryType, int day, String theContinuationProbability)
	{
		if(allParticipantsUserClickModel_ContinuationProbability_Reports.containsKey(queryType)==false)
		{
			allParticipantsUserClickModel_ContinuationProbability_Reports.put(queryType, new HashMap<LogUserClickModelReportParams, String[]>());
			for (int i = 0; i < LogUserClickModelReportParams.values().length; i++)
			{
				allParticipantsUserClickModel_ContinuationProbability_Reports.get(queryType).put(LogUserClickModelReportParams.values()[i], new String[arch.AgentConstants.TAU_SIMDAYS+1]);
			}
		}
			
		allParticipantsUserClickModel_ContinuationProbability_Reports.get(queryType).get(LogUserClickModelReportParams.continuationProbability)[day] = theContinuationProbability;
	}
	
	/**
	 * Returns full map, which is built as follows:
	 * 
	 * All Participant --> Query Type --> User Click Model Report Parameter --> Results in String array arranged by day
	 * 
	 * Query Type = n_n, n_d etc', from the enum at Constant.LogQueryType
	 * User Click Model Report Parameter = advertiserEffect, from the enum at Constants.LogUserClickModelReportParams
	 * 
	 * @return allParticipantsUserClickModelReports
	 */
	public Map<String, Map<LogQueryType, Map<LogUserClickModelReportParams, String[]>>> getAllParticipantsUserClickModelAdvertiserEffectReports()
	{
		return allParticipantsUserClickModel_AdvertiserEffect_Reports;
	}
	
	/**
	 * Returns medium map, which is built as follows:
	 * 
	 * Participant from input --> Query Type --> User Click Model Report Parameter --> Results in String array arranged by day
	 * 
	 * Query Type = n_n, n_d etc', from the enum at Constant.LogQueryType
	 * User Click Model Report Parameter = advertiserEffect, from the enum at Constants.LogUserClickModelReportParams
	 * 
	 * @return allParticipantsUserClickModelReports.get(Participant)
	 */
	public Map<LogQueryType, Map<LogUserClickModelReportParams, String[]>> getSpecificParticipantAllUserClickModelAdvertiserEffectReport(String Participant)
	{
		return allParticipantsUserClickModel_AdvertiserEffect_Reports.get(Participant);
	}
		
	/**
	 * Returns smallest map, which is built as follows:
	 * 
	 * Participant from input --> Query Type from input --> User Click Model Report Parameter --> Results in String array arranged by day
	 * 
	 * Query Type = n_n, n_d etc', from the enum at Constant.LogQueryType
	 * User Click Model Report Parameter = advertiserEffect, from the enum at Constants.LogUserClickModelReportParams
	 * 
	 * @return allParticipantsUserClickModelReports.get(Participant).get(queryType)
	 */
	public Map<LogUserClickModelReportParams, String[]> getSpecificParticipantSpecificUserClickModelAdvertiserEffectReport(String Participant, LogQueryType queryType)
	{
		return allParticipantsUserClickModel_AdvertiserEffect_Reports.get(Participant).get(queryType);
	}	
	
	/**
	 * Returns map, which is built as follows:
	 * 
	 * All Query Types --> User Click Model Report Parameter --> Results in String array arranged by day
	 * 
	 * Query Type = n_n, n_d etc', from the enum at Constant.LogQueryType
	 * User Click Model Report Parameter = continuationProbability, from the enum at Constants.LogUserClickModelReportParams
	 * 
	 * @return allParticipantsUserClickModelReports.get(Participant)
	 */
	public Map<LogQueryType, Map<LogUserClickModelReportParams, String[]>> getAllUserClickModelContinuationProbabilityReport()
	{
		return allParticipantsUserClickModel_ContinuationProbability_Reports;
	}
		
	/**
	 * Returns smallest map, which is built as follows:
	 * 
	 * Query Type from input --> User Click Model Report Parameter --> Results in String array arranged by day
	 * 
	 * Query Type = n_n, n_d etc', from the enum at Constant.LogQueryType
	 * User Click Model Report Parameter = continuationProbability, from the enum at Constants.LogUserClickModelReportParams
	 * 
	 * @return allParticipantsUserClickModelReports.get(Participant).get(queryType)
	 */
	public Map<LogUserClickModelReportParams, String[]> getSpecificUserClickModelContinuationProbabilityReport(LogQueryType queryType)
	{
		return allParticipantsUserClickModel_ContinuationProbability_Reports.get(queryType);
	}	
}
