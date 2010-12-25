package sampleAgent.sampleOptimizer;

import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.umich.eecs.tac.props.BidBundle;
import edu.umich.eecs.tac.props.Query;
import edu.umich.eecs.tac.props.QueryReport;
import edu.umich.eecs.tac.props.SalesReport;
import arch.Optimizer;
import arch.IEstimator.QueryEstimateResult;

public class AgentSmithOptimizer extends Optimizer
{

	private static final Logger log = Logger.getLogger("optimizer");

	/*
	 * algorithm parameters
	 */
	protected int initialBidIndex;
	protected static int INITIAL_BID_INDEX_DEFAULT = 50;

	protected double initialDailyQuerySpentLimit;
	protected static double INITIAL_DAILY_QUERY_SPENT_LIMIT_DEFAULT = 20.0;

	/*
	 * quota calculation variables
	 */
	protected int CAPACITY; 		// total capacity for capacity window
	protected int CAPACITY_WINDOW; 	// number of days in capacity window	
	protected Queue<Integer> salesWindow; // holds all sales for capacity window duration
	protected int capacityUsed; 	// capacity used for capacity window
	protected int capacityQuota;  	// estimated capacity remaining for capacity window
	protected int estimatedSales;

	/*
	 * holds information for all relevant queries
	 */
	protected Queue<AgentSmithOptimizerQuery> querySpace;

	protected QueryEstimateResult estimated;

	protected BidBundle bidBundle;
	
	protected Set<Query> querySet;

	protected int day;

	public AgentSmithOptimizer()
	{
		querySpace = new LinkedList<AgentSmithOptimizerQuery>();
		salesWindow = new LinkedList<Integer>();
		estimated = new QueryEstimateResult();
	}

	/*
	 * @see arch.IAgentComponent#simulationReady()
	 */
	public void simulationReady()
	{
		querySet = aaAgent.getQuerySet();
		day = 0;

		/* create queries space */
		for (Query query : querySet)
		{
			querySpace.add(new AgentSmithOptimizerQuery(query));
		}

		// we populate the bidBundle once upon simulationReady and update upon optimize()
		bidBundle = new BidBundle();
		for (AgentSmithOptimizerQuery optquery : querySpace)
		{
			bidBundle.addQuery(optquery.getQuery(), optquery.getAllBids()[initialBidIndex], optquery.getAd());
			bidBundle.setDailyLimit(optquery.getQuery(), Double.NaN);
			bidBundle.setCampaignDailySpendLimit(Double.NaN);
			optquery.setBestBidIndex(initialBidIndex);
			optquery.nextDay(0);
		}

		CAPACITY = aaAgent.getAdvertiserInfo().getDistributionCapacity();
		CAPACITY_WINDOW = aaAgent.getAdvertiserInfo().getDistributionWindow();
		capacityQuota = 0;
		
		log.log(Level.FINE, "Capacity: " + CAPACITY);
	}

	public void nextDay(int d)
	{
		day = d;
		for (AgentSmithOptimizerQuery query : querySpace)
		{
			query.nextDay(d);
		}
	}

	/*
	 * merely fetch the optimized info for each query (as calculated upon
	 * reception of the the status messages) and update the return bidBundle
	 * accordingly
	 * 
	 * @see arch.IOptimizer#optimize()
	 */
	public BidBundle optimize()
	{
		Query query;
		int tomorrow = day + 1;
		double dailyQuerySpentLimit;

		log.log(Level.FINE, " Bidding for day " + tomorrow);
		for (AgentSmithOptimizerQuery optquery : querySpace)
		{
			query = optquery.getQuery();
			/* dailyLimit is irrelevant until day 2 since no reports were received */
			if (tomorrow > 2)
				dailyQuerySpentLimit = optquery.dailyLimit;
			else
				dailyQuerySpentLimit = initialDailyQuerySpentLimit;

			bidBundle.setBid(query, optquery.bids[optquery.bestBidIndex]);
			bidBundle.setAd(query, optquery.ad);
			bidBundle.setDailyLimit(query, dailyQuerySpentLimit);

			Double dlimit = dailyQuerySpentLimit;
			Double dcl = optquery.estClicks[optquery.bestBidIndex];
			Double dcv = optquery.estConversions[optquery.bestBidIndex];
			double dcpc = (double) (((Double) (100 * optquery.estCpc[optquery.bestBidIndex])).intValue()) / 100;
			double dbid = (double) (((Double) (100 * bidBundle.getBid(query))).intValue()) / 100;

			log.log(Level.FINE, "(" + query.getManufacturer() + "," + query.getComponent() + ")" + " bid: " + dbid + ", dailyLimit: " + dlimit.intValue() + " (cl:" + dcl.intValue() + " cv:" + dcv.intValue() + " cpc:" + dcpc + ")");
		}

		bidBundle.setCampaignDailySpendLimit(Double.NaN);

		return bidBundle;
	}

	/**
	 * the quota of items to sell tomorrow (tday) is determined based on past
	 * sales (including yesterday sales - yesterday).
	 */
	public void handleSalesReport(SalesReport salesReport, int yesterday)
	{
		int totalYesterdaySales = 0;
		int estimatedCapacityUsed = 0;
		int today = yesterday + 1;
		int tomorrow = yesterday + 2;

		log.log(Level.FINE, " Sales report for day " + yesterday);

		if (yesterday > 0)
		{ /* sales reports are not expected for day 0 */

			for (AgentSmithOptimizerQuery optquery : querySpace)
			{
				optquery.yesterdayConversions = salesReport.getConversions(optquery.getQuery());
				optquery.yesterdayRevenue = salesReport.getRevenue(optquery.getQuery());

				log.log(Level.FINE, "(" + optquery.getQuery().getManufacturer() + "," + optquery.getQuery().getComponent() + ")" + " day " + yesterday + " reported sales: " + optquery.yesterdayConversions + ", revenue: " + optquery.yesterdayRevenue);

				totalYesterdaySales = totalYesterdaySales + optquery.yesterdayConversions;
			}

			// update capacity used form capacity window and keep sales of last (capacityWindow - 1) days */
			capacityUsed = capacityUsed + totalYesterdaySales;
			salesWindow.add(totalYesterdaySales);
			if (salesWindow.size() > CAPACITY_WINDOW - 1)
			{
				capacityUsed = capacityUsed - salesWindow.poll();
			}

			//today's sales estimation is the average of the Estimator's estimation and the quota allocated yesterday
			estimatedSales = (estimatedSales + capacityQuota) / 2;

			//if late enough, deduct oldest day in capacity window from capacity usage estimation
			if (salesWindow.size() >= CAPACITY_WINDOW - 1)
			{
				estimatedCapacityUsed = capacityUsed + estimatedSales - salesWindow.peek();
			} else
			{
				estimatedCapacityUsed = capacityUsed + estimatedSales;
			}

			capacityQuota = CAPACITY - estimatedCapacityUsed;
		}

		log.log(Level.FINE, "Day " + yesterday + " sales: " + totalYesterdaySales + ", Day " + today + " sales estimate: " + estimatedSales);
		log.log(Level.FINE, "Day " + today + " estimated capacity used: " + estimatedCapacityUsed + "  Day " + tomorrow + " capacity quota: " + capacityQuota);
	}

	public void handleQueryReport(QueryReport queryReport, int yday)
	{
		//reset the estimation for tomorrow's sales - we need this figure for the quota calculations
		estimatedSales = 0;
		log.log(Level.FINE, " Query report for day " + yday);

		//long start = System.currentTimeMillis();
		if (yday > 0)
		{
			//System.out.println("***************** Before GreedyMCKP, capacity=" + CAPACITY + " window=" + CAPACITY_WINDOW);
			GreedyMCKP gMkcp = new GreedyMCKP(CAPACITY/CAPACITY_WINDOW, querySet); // NEED TO CHANGE CAPACITY
			
			//first we score each query and ask the estimator for tomorrow's estimates
			for (AgentSmithOptimizerQuery query : querySpace)
			{
				query.yesterdayCost = queryReport.getCost(query.getQuery());
				
				for (int bidIndex = 0; bidIndex < query.bids.length; bidIndex++)
				{
					estimated = aaEstimator.estimateQuery(query.getQuery(), query.bids[bidIndex], query.ad, query.dailyLimit, yday + 2);

					double dcpc = (double) (((Double) (100 * queryReport.getCPC(query.getQuery()))).intValue()) / 100;
					log.log(Level.FINE,
							"(" + query.getQuery().getManufacturer() + "," + query.getQuery().getComponent() + ")" + " day " + yday + " reported " + " i:"
									+ queryReport.getImpressions(query.getQuery()) + " cl:" + queryReport.getClicks(query.getQuery()) + " cv:" + query.yesterdayConversions + " cpc:" + dcpc);
			
					query.setEstimates(bidIndex, estimated.getImpressions(), estimated.getCpc(), estimated.getConversions(), estimated.getClicks(), estimated.getProfits());

					gMkcp.add(query.getQuery(), bidIndex, query.bids[bidIndex], estimated.getConversions(), estimated.getProfits()); //NEED TO CHECK THE VALUE AND WEIGHT CALCULATION
				}
			}

			//finally, we can calculate bid and spend limits for the bid based on allocated units
			Map<Query, Integer> bestBidsIndexMap = gMkcp.calcSolution();
			for (AgentSmithOptimizerQuery query : querySpace)
			{
				query.setBestBidIndex(bestBidsIndexMap.get(query.getQuery()));
				
				query.calculateDailyLimit(bestBidsIndexMap.get(query.getQuery()));
				
				estimatedSales = estimatedSales + (int) (query.estConversions[query.bestBidIndex]);
			}
		}
		//long stop = System.currentTimeMillis();
		//System.out.println("***** Total run time for optimizer (in milis) = " + (stop-start));
	}

	public void simulationFinished()
	{
		querySpace.clear();
		salesWindow.clear();
	}

	public void simulationSetup()
	{
		initialBidIndex = aaConfig.getPropertyAsInt("Bid", INITIAL_BID_INDEX_DEFAULT);
		initialDailyQuerySpentLimit = aaConfig.getPropertyAsDouble("Limit", INITIAL_DAILY_QUERY_SPENT_LIMIT_DEFAULT);
	}

}
