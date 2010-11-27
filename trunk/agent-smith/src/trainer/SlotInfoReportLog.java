package trainer;

import java.util.HashMap;
import java.util.Map;

import trainer.Constants.LogSlotInfoReportParams;

public class SlotInfoReportLog
{
	private Map<String, Map<LogSlotInfoReportParams, String[]>> allParticipantsSlotInfoReports;
	
	public SlotInfoReportLog()
	{
		this.allParticipantsSlotInfoReports = new HashMap<String, Map<LogSlotInfoReportParams, String[]>>();
	}

	public void addParticipantSlotInfoReport(String participant, int day, String thePromotedSlots, String theRegularSlots, String thePromotedSlotBonus)
	{
		if(allParticipantsSlotInfoReports.containsKey(participant)==false)
		{
			allParticipantsSlotInfoReports.put(participant, new HashMap<LogSlotInfoReportParams, String[]>());
				
			for (int i = 0; i < LogSlotInfoReportParams.values().length; i++)
			{
				allParticipantsSlotInfoReports.get(participant).put(LogSlotInfoReportParams.values()[i], new String[arch.AgentConstants.TAU_SIMDAYS+1]);
			}
		}
			
		allParticipantsSlotInfoReports.get(participant).get(LogSlotInfoReportParams.promotedSlots)[day] = thePromotedSlots;
		allParticipantsSlotInfoReports.get(participant).get(LogSlotInfoReportParams.regularSlots)[day] = theRegularSlots;
		allParticipantsSlotInfoReports.get(participant).get(LogSlotInfoReportParams.promotedSlotBonus)[day] = thePromotedSlotBonus;
	}
	
	/**
	 * Returns full map, which is built as follows:
	 * 
	 * All Participant --> Slot Info Report Parameter --> Results in String array arranged by day
	 * 
	 * Slot Info Report Parameter = promotedSlots, regularSlots and promotedSlotBonus, from the enum at LogSlotInfoReportParams
	 * 
	 * @return allParticipantsSlotInfoReports
	 */
	public Map<String, Map<LogSlotInfoReportParams, String[]>> getAllParticipantsSlotInfoReports()
	{
		return allParticipantsSlotInfoReports;
	}
	
	/**
	 * Returns medium map, which is built as follows:
	 * 
	 * Participant from input -->  Slot Info Parameter --> Results in String array arranged by day
	 * 
	 * Slot Info Report Parameter = promotedSlots, regularSlots and promotedSlotBonus from the enum at LogSlotInfoReportParams
	 * 
	 * @return allParticipantsSlotInfoReports.get(Participant)
	 */
	public Map<LogSlotInfoReportParams, String[]> getSpecificParticipantAllSlotInfoReport(String Participant)
	{
		return allParticipantsSlotInfoReports.get(Participant);
	}	
}
