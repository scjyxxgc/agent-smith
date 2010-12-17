package sampleAgent.sampleEstimator;

import static arch.AgentConstants.TAU_SIMDAYS;

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
    //CHANGE!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    double [] b1 = new double[TAU_SIMDAYS+3]; //for query.estConvRate
    double [] b2 = new double[TAU_SIMDAYS+3]; //for query.estClickRate
   
   	protected Queue<MyBasicEstimatorQuery> querySpace;
	/**public class BasicEstimatorQuery extends AgentComponentQuery {
	public Double 	[]estSales; []estConvRate; []estClickRate;
    public int 		[]sales; []clicks;
	public Double 	[]convRate; []clickRate;  []profitPerUnitSold;
	**/
	
	public MyBasicEstimator() {
		querySpace = new LinkedList<MyBasicEstimatorQuery>();		
	}

	public void simulationReady() {
		Set<Query> querySet = aaAgent.getQuerySet();
		
		for(Query query : querySet) { 
        	querySpace.add(new MyBasicEstimatorQuery(query));        	
        }
		
		//CHANGE!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		 for (int i=0; i<61; i++){
		    	b1[i]=0;
		    	b2[i]=0;
		    }
	}
	
	public void nextDay(int day) {
		for(MyBasicEstimatorQuery query : querySpace) { 
        	query.nextDay(day);        	
        }
	}
	
	
	public void handleQueryReport(QueryReport queryReport, int yday) {
		int impressions;
		//CHANGE!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		double gamma = 0.2;
		//double [5] b1 = {0}; !!! will be lost each exit from function
		
		for(MyBasicEstimatorQuery query : querySpace) {      	
        
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

        	//query.estConvRate[yday+2] = decay*query.convRate[yday]+(1-decay)*query.estConvRate[yday+1];
        	query.estConvRate[yday+2] = decay*query.convRate[yday]+(1-decay)*(query.estConvRate[yday+1]+b1[yday+1]);
        	b1[yday+2]=gamma*(query.estConvRate[yday+2]-query.estConvRate[yday+1])+(1-gamma)*b1[yday+1];
        	
        	//query.estClickRate[yday+2] = decay*query.clickRate[yday]+(1-decay)*query.estClickRate[yday+1];
        	query.estClickRate[yday+2] = decay*query.clickRate[yday]+(1-decay)*(query.estClickRate[yday+1]+b2[yday+1]);
        	b2[yday+2]=gamma*(query.estClickRate[yday+2]-query.estClickRate[yday+1])+(1-gamma)*b2[yday+1];
        	
        	
       	}
	}

	public void handleSalesReport(SalesReport salesReport, int yday) {
		for(MyBasicEstimatorQuery query : querySpace) {      	
        	query.sales[yday] = salesReport.getConversions(query.getQuery());
        	
        	query.estSales[yday+2] = decay*query.sales[yday]+(1-decay)*query.estSales[yday+1];

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
