package sampleAgent.sampleOptimizer;

/**
 * Query info for a BasicOptimizer
 * Each query maintains its score and computes related daily allocation 
 *
 * @author Mariano Schain
 */

import edu.umich.eecs.tac.props.Ad;
import edu.umich.eecs.tac.props.Product;
import edu.umich.eecs.tac.props.Query;
import arch.AgentComponentQuery;

public class AgentSmithOptimizerQuery extends AgentComponentQuery
{
	/* attributes */
	public Ad ad;
	public Product product;
	public int bestBidIndex;
	public double[] bids;
	public double dailyLimit;

	/* latest data reported for yesterday */
	public double yesterdayRevenue;
	public int yesterdayConversions;
	public double yesterdayCost;

	/* latest estimates */
	protected double[] estImpressions;
	protected double[] estCpc;
	protected double[] estConversions;
	protected double[] estClicks;
	protected double[] estProfits;
	
	protected int NUMBER_OF_BIDS = 100;

	public AgentSmithOptimizerQuery(Query q)
	{
		super(q);

		product = new Product();
		ad = new Ad();

		if ((q.getManufacturer() != null) && (q.getComponent() != null))
		{ /* Targeted Ad */
			product.setManufacturer(q.getManufacturer());
			product.setComponent(q.getComponent());
			ad.setProduct(product);
		} else
		{ /* generic Ad */
			ad.setProduct(null);
		}
		
		bids = new double[NUMBER_OF_BIDS];
		estImpressions = new double[NUMBER_OF_BIDS];
		estCpc = new double[NUMBER_OF_BIDS];
		estConversions = new double[NUMBER_OF_BIDS];
		estClicks = new double[NUMBER_OF_BIDS];
		estProfits = new double[NUMBER_OF_BIDS];
	
		bids[0]=0;
		for (int i = 1; i < bids.length; i++)
		{
			bids[i] = bids[i-1] + 0.1;
		}
	}

	/*
	 * populate values from configuration file
	 */
	public void setBestBidIndex(int bidIndex)
	{
		bestBidIndex = bidIndex;
	}

	public void setEstimates(int bidIndex, double impressions, double cpc, double conversions, double clicks, double profit)
	{
		estImpressions[bidIndex] = impressions;
		estCpc[bidIndex] = cpc;
		estConversions[bidIndex] = conversions;
		estClicks[bidIndex] = clicks;
		estProfits[bidIndex] = profit;
	}


	public Ad getAd()
	{
		return ad;
	}

	public Product getProduct()
	{
		return product;
	}

	public double[] getAllBids()
	{
		return bids;
	}

	
	public double getBid()
	{
		return bids[bestBidIndex];
	}

	public void nextDay(int day)
	{
		dailyLimit = 0.0;
	}

	protected void calculateDailyLimit(int theBestBidIndex)
	{
		dailyLimit = estCpc[theBestBidIndex] * estClicks[theBestBidIndex] / estConversions[theBestBidIndex];
	}

}
