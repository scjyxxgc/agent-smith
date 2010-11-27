package trainer;

import java.util.HashMap;
import java.util.Map;

import trainer.Constants.LogProduct;
import trainer.Constants.LogRetailCatalogReportParams;

public class RetailCatalogReportLog
{
	private Map<String, Map<LogProduct, Map<LogRetailCatalogReportParams, String[]>>> allParticipantsRetailCatalogReports;
	
	public RetailCatalogReportLog()
	{
		this.allParticipantsRetailCatalogReports = new HashMap<String, Map<LogProduct, Map<LogRetailCatalogReportParams, String[]>>>();
	}

	public void addParticipantRetailCatalogReport(String participant, LogProduct product, int day, String theSalesProfit)
	{
		if(allParticipantsRetailCatalogReports.containsKey(participant)==false)
			allParticipantsRetailCatalogReports.put(participant, new HashMap<LogProduct, Map<LogRetailCatalogReportParams, String[]>>());
		
		if(allParticipantsRetailCatalogReports.get(participant).containsKey(product)==false)
		{
			allParticipantsRetailCatalogReports.get(participant).put(product, new HashMap<LogRetailCatalogReportParams, String[]>());
			for (int i = 0; i < LogRetailCatalogReportParams.values().length; i++)
			{
				allParticipantsRetailCatalogReports.get(participant).get(product).put(LogRetailCatalogReportParams.values()[i], new String[arch.AgentConstants.TAU_SIMDAYS+1]);
			}
		}
			
		allParticipantsRetailCatalogReports.get(participant).get(product).get(LogRetailCatalogReportParams.salesProfit)[day] = theSalesProfit;
	}
	
	/**
	 * Returns full map, which is built as follows:
	 * 
	 * All Participant --> Product --> Retail Catalog Parameter --> Results in String array arranged by day
	 * 
	 * Product = n_n, n_d etc', from the enum at Constant.LogProduct
	 * Retail Catalog Report Parameter = salesProfit, from the enum at LogRetailCatalogReportParams
	 * 
	 * @return allParticipantsRetailCatalogReports
	 */
	public Map<String, Map<LogProduct, Map<LogRetailCatalogReportParams, String[]>>> getAllParticipantsRetailCatalogReports()
	{
		return allParticipantsRetailCatalogReports;
	}
	
	/**
	 * Returns medium map, which is built as follows:
	 * 
	 * Participant from input --> Product --> Retail Catalog Parameter --> Results in String array arranged by day
	 * 
	 * Product = n_n, n_d etc', from the enum at Constant.LogProduct
	 * Retail Catalog Report Parameter = salesProfit, from the enum at LogRetailCatalogReportParams
	 * 
	 * @return allParticipantsRetailCatalogReports.get(Participant)
	 */
	public Map<LogProduct, Map<LogRetailCatalogReportParams, String[]>> getSpecificParticipantAllRetailCatalogReport(String Participant)
	{
		return allParticipantsRetailCatalogReports.get(Participant);
	}
		
	/**
	 * Returns smallest map, which is built as follows:
	 * 
	 * Participant from input --> Product from input --> Retail Catalog Parameter --> Results in String array arranged by day
	 * 
	 * Product = n_n, n_d etc', from the enum at Constant.LogProduct
	 * Retail Catalog Report Parameter = salesProfit, from the enum at LogRetailCatalogReportParams
	 * 
	 * @return allParticipantsRetailCatalogReports.get(Participant).get(queryType)
	 */
	public Map<LogRetailCatalogReportParams, String[]> getSpecificParticipantSpecificRetailCatalogReport(String Participant, LogProduct product)
	{
		return allParticipantsRetailCatalogReports.get(Participant).get(product);
	}	
}
