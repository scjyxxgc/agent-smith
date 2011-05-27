                                   
package sampleAgent.sampleEstimator;


import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import edu.umich.eecs.tac.props.Ad;
import edu.umich.eecs.tac.props.BidBundle;
import edu.umich.eecs.tac.props.Query;
import edu.umich.eecs.tac.props.QueryReport;
import edu.umich.eecs.tac.props.SalesReport;
import arch.Estimator;
import arch.IModeler.ModelerResult;


//simple DES implementation with weighted position fit
public class MyBasicEstimator extends Estimator
{
	// where defined ?
	double decay;
	
	protected static double GAMMA = 0.8;
	protected static double DECAY_DEFAULT = 0.05;
	protected Queue<MyBasicEstimatorQuery> querySpace;
	
	//LR
	protected static int windowSize = 3;
	protected static double DIFF = 0.25;
	protected static int isBURST = 0;
	
	//builder
	public MyBasicEstimator() 
	{		
		querySpace = new LinkedList<MyBasicEstimatorQuery>();		
	}

	//prepare simulation
	public void simulationReady() 
	{		
		Set<Query> querySet = aaAgent.getQuerySet();		
		for(Query query : querySet) 
		{ 
        	querySpace.add(new MyBasicEstimatorQuery(query)); 
	    }
	}
	
	//simulation iterator
	public void nextDay(int day) 
	{
		for(MyBasicEstimatorQuery query : querySpace) 
			query.nextDay(day);
	}
	
	//LR
	public double LinearRegression(double[] yArr, double[] xVals, int numDays)
	{
	   	double sumx = 0.001;
		double sumy = 0.001;
	   	for (int i = 0; i <numDays; i++)
	   	{ 
	   		sumx += xVals[i];
	   		sumy += yArr[i];
	   	}
	   	   		
	   	double xbar = sumx / numDays;
	   	double ybar = sumy / numDays;
	   	double xxbar = 0.001;
	   	double xybar = 0.001;
	   	double xdelta = 0.001;
	   	double ydelta = 0.001;
	    
	   	for (int i = 0; i < numDays; i++) 
	   	{
	   		xdelta = (xVals[i] - xbar);
	   		ydelta = (yArr[i] - ybar);
	   		xxbar += Math.pow(xdelta,2);
	   		xybar += xdelta * ydelta;
		}

		double beta1 = xybar / xxbar;
		double beta0 = ybar - beta1 * xbar;
	   	
		//y = beta1*x + beta0
		return (beta1*(numDays+2)+beta0);
	}
	
	
	//estimate and set clicks and conversions rate values 
	public void handleQueryReport(QueryReport queryReport, int yday) 
	{
		int impressions;
		
		//LR
		double[] estVals = new double[windowSize];
		double[] realVals = new double[windowSize];
		double des, lr;
		isBURST = 0;
		
		for(MyBasicEstimatorQuery query : querySpace) 
		{      	
			query.clicks[yday] = queryReport.getClicks(query.getQuery());
			impressions = queryReport.getImpressions(query.getQuery());
         		
        	//set clicks and conversions rate        
	        if (impressions == 0) 
	        	query.clickRate[yday] = 0.0;
           	else 
           		query.clickRate[yday] = ((double)(query.clicks[yday]))/((double)(impressions));
        
	        if (query.clicks[yday] == 0) 
	        	query.convRate[yday] = 0.0;
	        else
	        	query.convRate[yday] = ((double)(query.sales[yday]))/((double)(query.clicks[yday]));


	        //set estimated conversions rate
			query.estConvRate[yday+2] = decay*query.convRate[yday]+(1-decay)*(query.estConvRate[yday+1]+query.b1[yday+1]);
			query.b1[yday+2] = GAMMA*(query.estConvRate[yday+2]-query.estConvRate[yday+1])+(1-GAMMA)*query.b1[yday+1];
			
			des = query.estConvRate[yday+2];
			
			//LR
			if (yday>6)
			{
				for (int i=0; i<windowSize-1; i++)         
				{
					estVals[i]=query.estConvRate[yday-i];
					realVals[i]=query.convRate[yday-i];		
				}
				lr = LinearRegression(estVals, realVals,windowSize);
				//lr = query.estConvRate[yday+2];
				
				if (Math.abs(des - lr) > DIFF) 
					isBURST = 1;
	        	
			}
			
			
			//set estimated clicks rate
			query.estClickRate[yday+2] = decay*query.clickRate[yday]+(1-decay)*(query.estClickRate[yday+1]+query.b2[yday+1]);
			query.b2[yday+2] = GAMMA*(query.estClickRate[yday+2]-query.estClickRate[yday+1])+(1-GAMMA)*query.b2[yday+1];
			
			des = query.estClickRate[yday+2];
			
			//LR
			if (yday>6)
			{
				for (int i=0; i<windowSize-1; i++)         
				{
					estVals[i]=query.estClickRate[yday-i];
					realVals[i]=query.clickRate[yday-i];		
				}
				lr = LinearRegression(estVals, realVals,windowSize);
				//lr = query.estClickRate[yday+2];
				
				if (Math.abs(des - lr) > DIFF) 
					isBURST = 1;
			}
       	}
	}
	
	
	public void handleSalesReport(SalesReport salesReport, int yday) 
	{
		//LR
		double[] estVals = new double[windowSize];
		double[] realVals = new double[windowSize];
		double des, lr;
		isBURST = 0;
		
		for(MyBasicEstimatorQuery query : querySpace) 
		{   
			//set sales
        	query.sales[yday] = salesReport.getConversions(query.getQuery());  	

 
        	//set estimated sales
        	query.estSales[yday+2] = decay*query.sales[yday]+(1-decay)*(query.estSales[yday+1]+query.b3[yday+1]);
			query.b3[yday+2] = GAMMA*(query.estSales[yday+2]-query.estSales[yday+1])+(1-GAMMA)*query.b3[yday+1];
			
			des = query.estSales[yday+2];

			//LR
			if (yday>6)
			{
				for (int i=0; i<windowSize-1; i++)         
				{
					estVals[i]=query.estSales[yday-i];
					realVals[i]=query.sales[yday-i];		
				}
				lr = LinearRegression(estVals, realVals,windowSize);
				//lr = query.estSales[yday+2];
				
				if (Math.abs(des - lr) > DIFF) 
					isBURST = 1;
			}
			
			
        	if (query.sales[yday]!=0)        
				query.profitPerUnitSold[yday] = salesReport.getRevenue(query.getQuery())/((double)query.sales[yday]);        
			else
				query.profitPerUnitSold[yday] = 0.0;
		}
	}
	
	//finish simulation
	public void simulationFinished() 
	{
		querySpace.clear();		
	}
	

	/* it is assumed that day is "tomorrow" and therefore we only have profit data for [day-2]	 
	 * @see arch.IEstimator#estimateQuery(edu.umich.eecs.tac.props.Query, double, edu.umich.eecs.tac.props.Ad, int, int, double, double, double, double)
	 */
	public QueryEstimateResult estimateQuery(Query query, double bid, Ad ad, double limit, int day) 
	{
		QueryEstimateResult result = new QueryEstimateResult(0.0,0.0,0.0,0.0,0.0);		
		ModelerResult modelerResult;		
		double cpc;		
		double impressions;		
		double clicks;		
		double conversions;
		//
		double position;
		double weight1 = 1.0;
		double weight2 = 1.0;
		
		
		for(MyBasicEstimatorQuery equery : querySpace) 
		{      				
			if (equery.getQuery().equals(query)) 
			{
				//set model parameters
				modelerResult = aaModeler.model(query, bid,  ad,  day);				
				
				//set cpc
				cpc = modelerResult.getCpc();				
				result.setCpc(cpc);
				
				//set impressions
				impressions = modelerResult.getImpressions();				
				result.setImpressions(impressions);
				
				//set clicks
				clicks = impressions*equery.estClickRate[day];
				
				//if estimated number of clicks costs more than spending limit, set it to max
				if ((clicks*cpc > limit) && (cpc != 0))
					clicks=limit/cpc;			
								
				result.setClicks(clicks);
				
				//set conversions
				conversions = clicks*equery.estConvRate[day];				
				result.setConversions(conversions);	
				
				//set position
				position = modelerResult.getPosition();
								
				if (position < 1.0) {
					position = Math.round(position);
				}
				
				//set position as random if supposed to be zero
				if (position == 0.0)
				{
					Random generator = new Random( 19580427 );
					position = generator.nextDouble()*7.0 + 1.0;
				}
				
				//set weight factor
				weight1 = 1.0 / position;

				if (isBURST == 1) 
					weight1 = Math.pow(weight1, 2);
				
				if (position != 1.0) 
					weight2 = 1 - weight1;
			
				//set profits with weighted position fit
				result.setProfits(weight1*conversions*equery.profitPerUnitSold[day-2] - weight2*clicks*cpc);	
			}
		}
		return result;
	}

	
	public BundleEstimateResult estimateBundle(BidBundle bidBundle, int day) 
	{
		Double bid = 0.0;		
		Ad ad;		
		Double estQuerySales;		
		BundleEstimateResult estimated = new BundleEstimateResult(0.0,0.0);		
		ModelerResult modelerResult;
		
		double estImpressions = 0.0;		
		double estCost = 0.0;		
		double estClicks = 0.0;		
		double tconversions = 0.0;		
		double tprofits = 0.0;				

		for(MyBasicEstimatorQuery query : querySpace) 
		{      	
			bid = bidBundle.getBid(query.getQuery());			
			if (!bid.isNaN()) 
			{				
				ad = bidBundle.getAd(query.getQuery());				
				modelerResult = aaModeler.model(query.getQuery(), bid, ad, day);				
				estImpressions = modelerResult.getImpressions();				
				estCost = modelerResult.getCpc();				
				estClicks = estImpressions*query.estClickRate[day];				
				estQuerySales = estClicks*query.estConvRate[day];				
				tconversions = tconversions + estQuerySales;				
				tprofits = tprofits + estQuerySales*query.profitPerUnitSold[day-2] - estClicks*estCost;
			}	
		}
		
		estimated.setConversions(tconversions);		
		estimated.setProfits(tprofits);		
		return estimated;
	}

	//simulation's configuration
	public void simulationSetup() 
	{
		decay = aaConfig.getPropertyAsDouble("Decay", DECAY_DEFAULT);
	}
	

}
