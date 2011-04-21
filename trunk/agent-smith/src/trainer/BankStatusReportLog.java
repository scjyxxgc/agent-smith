package trainer;

import java.util.HashMap;
import java.util.Map;

public class BankStatusReportLog
{
	private Map<String, double[]> allParticipantsBankStatusReports;
	
	public BankStatusReportLog()
	{
		this.allParticipantsBankStatusReports = new HashMap<String, double[]>();
	}

	public void addParticipantBankStatusReport(String participant, int day, double bankStatus)
	{
		if(allParticipantsBankStatusReports.containsKey(participant)==false)
		{
			allParticipantsBankStatusReports.put(participant, new double[arch.AgentConstants.TAU_SIMDAYS+1]);
		}
			
		allParticipantsBankStatusReports.get(participant)[day] = bankStatus;
	}
	
	/**
	 * Returns full map, which is built as follows:
	 * 
	 * All Participant -->  Bank Status in double[] array arranged by day
	 * 
	 * 
	 * @return allParticipantsBankStatusReports
	 */
	public Map<String, double[]> getAllParticipantsBankStatusReports()
	{
		return allParticipantsBankStatusReports;
	}
	
	/**
	 * Returns double[] array for all days with the bank status of the provided Participant
	 * 
	 * @return bank status for the Participant for all days
	 */
	public double[] getSpecificParticipantAllBankStatusReport(String Participant)
	{
		return allParticipantsBankStatusReports.get(Participant);
	}	
}
