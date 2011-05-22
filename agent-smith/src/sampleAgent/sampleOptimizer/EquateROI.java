package sampleAgent.sampleOptimizer;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import edu.umich.eecs.tac.props.Query;

public class EquateROI
{
	private float targetROI;
	private float INC_ROI;
	private float INC_BID;
	private float INC_BID_MP;
	private float INC_BID_HP;
	
	private float burstIdentifyFactor;
	private float burstIncBidFactor;
	
	private int totalSales = 0;
	private int yesterday = 0;
	private int startToCheckBurstFromDay = 5;
	private int checkForBurstFromDay;
	private boolean isBurst = false;
	
	private int positionThreshold;

	private double totalImpressionsForAllDaysSoFar;
	
	Map<Query, Integer> querySales;
	Map<Query, Integer> queryClicks;
	Map<Query, Integer> queryImpressions;
	Map<Query, Boolean> queryIsPriorityManufacturer;
	Map<Query, Boolean> queryIsPriorityComponent;
	Map<Query, Double> queryBid;
	Map<Query, Double> queryPosition;
	
	public EquateROI(float startTargetRoi, float incRoiFactor, float incBidFactor, float incBidFactorMediumPriority,float incBidFactorHighPriority, int positionThreshold, float burstIncBidFactor, float burstIdentifyFactor)
	{
		targetROI = startTargetRoi; // maybe we can optimize using previous game reports

		INC_ROI = incRoiFactor;
		INC_BID = incBidFactor;
		INC_BID_MP = incBidFactorMediumPriority;
		INC_BID_HP = incBidFactorHighPriority;
		
		this.burstIncBidFactor = burstIncBidFactor;
		this.burstIdentifyFactor = burstIdentifyFactor;
		
		querySales = new HashMap<Query, Integer>();
		queryClicks = new HashMap<Query, Integer>();
		queryImpressions = new HashMap<Query, Integer>();
		queryIsPriorityManufacturer = new HashMap<Query, Boolean>();
		queryIsPriorityComponent = new HashMap<Query, Boolean>();
		queryBid = new HashMap<Query, Double>();
		queryPosition = new HashMap<Query, Double>();
		
		this.positionThreshold = positionThreshold;
		
		 totalImpressionsForAllDaysSoFar = 0;
		checkForBurstFromDay = startToCheckBurstFromDay;
	}

	public void add(Query theQuery, int sales, int numOfClicks, boolean isPriorityManufacturer, boolean isPriorityComponent, double position, int numOfImpressions, int yesterday)
	{
		//System.out.println("(" + theQuery.getManufacturer() + "," + theQuery.getComponent() + ")" + " - ADDING: isPriorityManufacturer= " + isPriorityManufacturer + ", isPriorityComponent= " + isPriorityComponent);
		querySales.put(theQuery, sales);
		queryClicks.put(theQuery, numOfClicks);
		queryImpressions.put(theQuery, numOfImpressions);
		queryIsPriorityManufacturer.put(theQuery, isPriorityManufacturer);
		queryIsPriorityComponent.put(theQuery, isPriorityComponent);
		queryPosition.put(theQuery, position);
		
		if(queryBid.containsKey(theQuery) == false)
			queryBid.put(theQuery, 0.0);
		
		totalSales += sales;
		
		this.yesterday = yesterday;
	}
	
	public void clear()
	{
		querySales.clear();
		queryClicks.clear();
		queryIsPriorityManufacturer.clear();
		queryIsPriorityComponent.clear();
		queryPosition.clear();
		queryImpressions.clear();
		//queryBid.clear();
		
		totalSales = 0;
	}
	
	public Map<Query, Double> calcBids(int theCapacity)
	{
		double bid = 0;
		double cpc = 0;
		double usp = 0;
		float prConv = 0;
		double totalImpressionsForDay=0;

		System.out.println("Capacity= " + theCapacity + ", Total Sales= " + totalSales);
		
		if(totalSales > theCapacity)
		{
			targetROI = targetROI * INC_ROI;
		} else if(totalSales < theCapacity)
		{
			targetROI = targetROI / INC_ROI;
		}
		
		Set<Query> queries = querySales.keySet();
		for (Query query : queries)
		{
			totalImpressionsForDay+=(float)queryImpressions.get(query);
		}
		
		isBurst=false;
		if(yesterday>checkForBurstFromDay)
		{
			isBurst = totalImpressionsForDay > ((totalImpressionsForAllDaysSoFar/(double)yesterday)*burstIdentifyFactor);
			checkForBurstFromDay = yesterday + 1;
		}
		totalImpressionsForAllDaysSoFar+=totalImpressionsForDay;
		
		for (Query query : queries)
		{
			if(querySales.get(query)>0)
			{			
				usp = AgentSmithOptimizer.UNIT_SALES_PROFIT;
				if(queryIsPriorityManufacturer.get(query))
					usp = usp*(1.0+AgentSmithOptimizer.MANUFACTURER_SPECIALIST_BONUS);
				if(queryIsPriorityComponent.get(query))
					usp = usp*(1.0+AgentSmithOptimizer.COMPONENT_SPECIALIST_BONUS);
					
				prConv = (float)querySales.get(query)/(float)queryClicks.get(query);
				cpc = ((usp - targetROI) * prConv)<0?0:((usp - targetROI) * prConv);
				
				if(queryIsPriorityManufacturer.get(query) || queryIsPriorityComponent.get(query))
				{
					if(queryPosition.get(query) < positionThreshold)
						bid = (cpc + INC_BID);
					else
						bid = (queryIsPriorityManufacturer.get(query) && queryIsPriorityComponent.get(query))?(cpc + INC_BID_HP):(cpc + INC_BID_MP);
				}
				else
					bid = (cpc + INC_BID);
			}
			else
			{
				if(queryIsPriorityManufacturer.get(query) || queryIsPriorityComponent.get(query))
				{
					if(queryPosition.get(query) < positionThreshold)
						bid = queryBid.get(query) + INC_BID;
					else
						bid = (queryIsPriorityManufacturer.get(query) && queryIsPriorityComponent.get(query))?(queryBid.get(query) + INC_BID_HP):(queryBid.get(query) + INC_BID_MP);
				}
				else
					bid = queryBid.get(query) + INC_BID;
			}
			
			if(isBurst)
				bid = bid * burstIncBidFactor;
			
			queryBid.put(query, bid);
			
			System.out.println("(" + query.getManufacturer() + "," + query.getComponent() + ") - " + "TARGET ROI= " + targetROI + ", USP= " + usp + ", prConv = " + prConv + ", cpc = " + cpc + ", bid = " + bid + ", position = " + queryPosition.get(query) + ", isBurst = " + isBurst);
		}
		
		return queryBid;
	}
	
	/**
	 * @return the isBurst
	 */
	public boolean getIsBurst()
	{
		return isBurst;
	}
	
}
