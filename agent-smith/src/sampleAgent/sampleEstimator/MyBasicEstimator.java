package sampleAgent.sampleEstimator;

import static arch.AgentConstants.TAU_SIMDAYS; //=60

import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;



import edu.umich.eecs.tac.props.Ad;
import edu.umich.eecs.tac.props.BidBundle;
import edu.umich.eecs.tac.props.Query;
import edu.umich.eecs.tac.props.QueryReport;
import edu.umich.eecs.tac.props.SalesReport;
import arch.Estimator;
import arch.IModeler.ModelerResult;


public class MyBasicEstimator extends Estimator {
	
	double decay;
    protected static double DECAY_DEFAULT = 0.1;
    protected static int NUM_OF_DAYS = 5;
    
    //60 days arrays
    double [] convRateArr = new double[TAU_SIMDAYS+3]; //for query.estConvRate
    double [] clickRateArr = new double[TAU_SIMDAYS+3]; //for query.estClickRate
    double [] salesArr = new double[TAU_SIMDAYS+3]; //for query.estSales
    //5 days window
    double [] convRateWindow = new double[NUM_OF_DAYS]; //for query.estConvRate
    double [] clickRateWindow = new double[NUM_OF_DAYS]; //for query.estClickRate
    double [] salesWindow = new double[NUM_OF_DAYS]; //for query.estSales
    //estimated 5 days window
    double [] estConvRateWindow = new double[NUM_OF_DAYS]; //for query.estConvRate
    double [] estClickRateWindow = new double[NUM_OF_DAYS]; //for query.estClickRate
    double [] estSalesWindow = new double[NUM_OF_DAYS]; //for query.estSales
    
    	protected Queue<MyBasicEstimatorQuery> querySpace;
	/**public class BasicEstimatorQuery extends AgentComponentQuery {
	public Double 	[]estSales; []estConvRate; []estClickRate;
    public int 		[]sales; []clicks;
	public Double 	[]convRate; []clickRate;  []profitPerUnitSold;
	**/
   	
 /* EXPLANATION OF MY COMPUTING REGRESSION FUNCTION:
  * Goal: Estimate y=ax+b when y is ConvRate or ClickRate or Sales.
		* Let's take ConvRate as example. We have only 1 unknown, ConvRate, and 
		* want to find out how it changes through time, with help of its' 
		* previous known values. Thus, in this algorithm, y=ConvRate and
		* x=time (look at the program I found to understand better, if you wish).
 		* If the server saves 5 day window, I understood that when we evaluate
		* estConvRate[yday+2] we have convRate[yday-2], convRate[yday-1], convRate[yday].
		* (Want to ask it our other members to be sure.) I decided that yday-2 will be
		* our x=1, as the beginning of the "keta of zir" x we look at. Thus we want to
		* find y at x=5 (yday+2).   	
		* yday-2=1 yday-1=2 yday=3 yday+1=4(we have only estimation) yday+2=5
		* 
		* numDays = numDays we use, max is 3 days in 5 day window
		*  or 1 - 2 days if there has been a boost recently
		*  THIS FUNCTION DOESN'T TAKE INTO ACCOUNT, DOESN'T SUIT
		*  USING DATA (FROM DAYS BEFORE BOOST) AFTER THE BOOST
		*  
		*  IF no boost AND we have min 5 days behind us since the competition's start:
		*  xVals = {1, 2, 3}; since x=1 for [yday-2] and then increases +1 each day
		* numDays=3 
*/
   	
/////////////////////////////////////////////////////////////////////////////////////////////////////
    	//estimate y = ax + b
    	//y is estimated ConvRate or ClickRate or Sales.
    	//x previous actual result
    	//if no boost and we have minimum 5 days behind: xVals = {x1, x2, x3} and numDays = 3 ??????????????

public double LinearRegression(double[] yArr, double[] xVals, int numDays)
{
   		
	double sumx = 0.0;
	double sumy = 0.0;
   	for (int i = 0; i <numDays; i++)
   	{ 
   		sumx += xVals[i];
   		sumy += yArr[i];
   	}
   	   		
   	double xbar = sumx / numDays;
   	double ybar = sumy / numDays;
   	double xxbar = 0.0;
   	double xybar = 0.0;
   	double xdelta = 0.0;
   	double ydelta = 0.0;
    
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

   		
	public MyBasicEstimator() {
		querySpace = new LinkedList<MyBasicEstimatorQuery>();		
	}

	public void simulationReady() {
		Set<Query> querySet = aaAgent.getQuerySet();
		
		for(Query query : querySet) { 
        	querySpace.add(new MyBasicEstimatorQuery(query));        	
        }
		 
		for (int i=0; i<60; i++)
		 {
			 convRateArr[i]=0;
			 clickRateArr[i]=0;
			 salesArr[i]=0; 
		 }
		
		//CHANGE1 - putting zeroes in other arrays too
		for (int i=0; i<NUM_OF_DAYS; i++)
		 {
			convRateWindow[i]=0;
			clickRateWindow[i]=0;
			salesWindow[i]=0;
			estConvRateWindow[i]=0;
			estClickRateWindow[i]=0;
			estSalesWindow[i]=0;
		 }
				
	}
	
	public void nextDay(int day) {
		for(MyBasicEstimatorQuery query : querySpace) { 
        	query.nextDay(day);        	
        }
	}
		
	public void handleQueryReport(QueryReport queryReport, int yday) {
		int impressions;
		//double gamma = 0.2;
				
		for(MyBasicEstimatorQuery query : querySpace) 
		{      	
        	query.clicks[yday] = queryReport.getClicks(query.getQuery());
         	impressions = queryReport.getImpressions(query.getQuery());
         		
        	/* update estimates */
        	
        	if (impressions == 0) 
        		query.clickRate[yday] = 0.0;
           	else 
           		query.clickRate[yday] = (double)(query.clicks[yday])/(double)(impressions);

        	if (query.clicks[yday] == 0) 
           		 query.convRate[yday] = 0.0;
        	else
           		 query.convRate[yday] = (double)(query.sales[yday])/(double)(query.clicks[yday]);

        	//query.estConvRate[yday+2] = decay*query.convRate[yday]+(1-decay)*(query.estConvRate[yday+1]+b1[yday+1]);
        	//b1[yday+2]=gamma*(query.estConvRate[yday+2]-query.estConvRate[yday+1])+(1-gamma)*b1[yday+1];
        	
        	//build window and estimates
        	for(int i = 0; i < NUM_OF_DAYS-2; i++) 
        	{
        		if (yday<=3)
    			    {
    				convRateWindow[i] = 0;
    				estConvRateWindow[i] = 0;
    			    }
    			else {
        		convRateWindow[i] = query.convRate[yday-3 + i];
        		estConvRateWindow[i] = query.estConvRate[yday-3 + i];
        	          }
        	}//end for
        	//query.estConvRate[yday+2] = LinearRegression(estConvRateWindow, convRateWindow, NUM_OF_DAYS);
        	query.estConvRate[yday+2] = LinearRegression(estConvRateWindow, convRateWindow, NUM_OF_DAYS-2);
        	
        	//query.estClickRate[yday+2] = decay*query.clickRate[yday]+(1-decay)*(query.estClickRate[yday+1]+b2[yday+1]);
        	//b2[yday+2]=gamma*(query.estClickRate[yday+2]-query.estClickRate[yday+1])+(1-gamma)*b2[yday+1];
        	
        	//build window and estimates
        	/*for(int i = 0; i < NUM_OF_DAYS; i++) 
        	{
        		//if (yday<2) ?????
        		if (yday<3)
        			{
        				clickRateWindow[i] = 0;
        				estClickRateWindow[i] = 0;
        			}
        			else {
        		clickRateWindow[i] = query.clickRate[yday-2 + i];
        		estClickRateWindow[i] = query.estClickRate[yday-2 + i];
        	              }
        	}//end for
        	*/
        	for(int i = 0; i < NUM_OF_DAYS-2; i++) 
        	{
        		//if (yday<2) ?????
        		if (yday<=3)
        			{
        				clickRateWindow[i] = 0;
        				estClickRateWindow[i] = 0;
        			}
        			else {
        		clickRateWindow[i] = query.clickRate[yday-3 + i];
        		estClickRateWindow[i] = query.estClickRate[yday-3 + i];
        	              }
        	}//end for
        	query.estClickRate[yday+2] = LinearRegression(estClickRateWindow, clickRateWindow, NUM_OF_DAYS-2);
		}
	}

	public void handleSalesReport(SalesReport salesReport, int yday) {
			//double gamma = 0.2;
		
		for(MyBasicEstimatorQuery query : querySpace) {      	
        	query.sales[yday] = salesReport.getConversions(query.getQuery());
        	
        	//query.estSales[Day+2] = decay*query.sales[yday]+(1-decay)*(query.estSales[yday+1]+b3[yday+1]);
        	//b3[yday+2]=gamma*(query.estSales[yday+2]-query.estSales[yday+1])+(1-gamma)*b3[yday+1];
        	
        	//build window and estimates
        	/*for(int i = 0; i < NUM_OF_DAYS; i++) 
        	{
        		if (yday<3)
    			{
        			salesWindow[i] = 0;
        			estSalesWindow[i] = 0;
    			}
    			else {
        		salesWindow[i] = query.sales[yday-2 + i];
        		estSalesWindow[i] = query.estSales[yday-2 + i];
        	}
        	}//end for
        	*/
        	for(int i = 0; i < NUM_OF_DAYS-2; i++) 
        	{
        		if (yday<=3)
    			{
        			salesWindow[i] = 0;
        			estSalesWindow[i] = 0;
    			}
    			else {
        		salesWindow[i] = query.sales[yday-3 + i];
        		estSalesWindow[i] = query.estSales[yday-3 + i];
        	}
        	}//end for
        	query.estSales[yday+2] = LinearRegression(estSalesWindow, salesWindow, NUM_OF_DAYS-2);
        	
        	if (query.sales[yday]!=0)
        		query.profitPerUnitSold[yday] = salesReport.getRevenue(query.getQuery())/query.sales[yday];
        	else 
        		query.profitPerUnitSold[yday] = 0.0;
    	}
	}
	
	
	public void simulationFinished() {
		querySpace.clear();		
	}
	
	/*
	 * it is assumed that day is "tomorrow" and therefore we only have profit data for [day-2]
	 * @see arch.IEstimator#estimateQuery(edu.umich.eecs.tac.props.Query, double, edu.umich.eecs.tac.props.Ad, int, int, double, double, double, double)
	 */
	public QueryEstimateResult estimateQuery(Query query, double bid, Ad ad, double limit, int day) {
		QueryEstimateResult result = new QueryEstimateResult(0.0,0.0,0.0,0.0,0.0);
		ModelerResult modelerResult;
		double cpc;
		double impressions;
		double clicks;
		double conversions;
		
		for(MyBasicEstimatorQuery equery : querySpace) {      	
			if (equery.getQuery().equals(query)) {
				modelerResult = aaModeler.model(query, bid,  ad,  day);
				
				cpc = modelerResult.getCpc();
				result.setCpc(cpc);
				
				impressions = modelerResult.getImpressions();
				result.setImpressions(impressions);
				
				clicks = impressions*equery.estClickRate[day];				
				result.setClicks(clicks);
				
				conversions = clicks*equery.estConvRate[day];
				result.setConversions(conversions);
				
				result.setProfits(conversions*equery.profitPerUnitSold[day-2] - clicks*cpc);
			}
		}
		return result;
	}

	
	public BundleEstimateResult estimateBundle(BidBundle bidBundle, int day) {
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
				
		for(MyBasicEstimatorQuery query : querySpace) {      	
			bid = bidBundle.getBid(query.getQuery());
			if (!bid.isNaN()) {
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
	
	public void simulationSetup() {
		decay = aaConfig.getPropertyAsDouble("Decay", DECAY_DEFAULT);
	}
	
}

