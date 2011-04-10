package sampleAgent.sampleModeler;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;



import edu.umich.eecs.tac.props.Ad;
import edu.umich.eecs.tac.props.Query;
import edu.umich.eecs.tac.props.QueryReport;
import edu.umich.eecs.tac.props.SalesReport;
import arch.Modeler;

public class SmithBasicModeler extends Modeler {

	double decay;
    protected static double DECAY_DEFAULT = 0.1;
    private static final double PRECISION = 0.0001;

	protected Queue<SmithBasicModelerQuery> querySpace;
	
	public void simulationReady() {
		Set<Query> querySet = aaAgent.getQuerySet();
		
		for(Query query : querySet) { 
        	querySpace.add(new SmithBasicModelerQuery(query));        	
        }
	}
	
	public void nextDay(int day) {
		for(SmithBasicModelerQuery query : querySpace) { 
        	query.nextDay(day);        	
        }
	}

	
	public SmithBasicModeler() {
		querySpace = new LinkedList<SmithBasicModelerQuery>();
	}
	
	public void handleQueryReport(QueryReport queryReport, int yday) {
		for(SmithBasicModelerQuery query : querySpace) {      	
        	query.cpc[yday] = queryReport.getCPC(query.getQuery());
        	query.impressions[yday] = queryReport.getImpressions(query.getQuery());
        	
        	/* update estimates */
        	if (!query.cpc[yday].isNaN()) 
        		query.estCpc[yday+2] = decay*query.cpc[yday] + (1-decay)*query.estCpc[yday+1];
        	else
        		query.estCpc[yday+2] = query.estCpc[yday+1];
        	
        	query.estImpressions[yday+2] = decay*query.impressions[yday] + (1-decay)*query.estImpressions[yday+1];
        	
        	query.setClicks(yday, queryReport.getClicks(query.getQuery()));
       	}
	}

	public void handleSalesReport(SalesReport salesReport, int yday) {
		int conv = 0;
		double rev = 0.0;
		double ratio1 = 0.0;
		double ratio2 = 0.0;
		
		for(SmithBasicModelerQuery query : querySpace) { 
        	conv = salesReport.getConversions(query.getQuery());
        	rev = salesReport.getRevenue(query.getQuery());
        	
        	query.setConversions(yday, conv);
        	query.setRevenue(yday, rev);
        	
        	/* update cumulative clicks and conversions probability */
        	if (query.getClicks(yday) != null)
        	{	
        		ratio1 = query.getClicks(yday) / query.impressions[yday];
        		ratio2 = query.getConversions(yday) / query.getClicks(yday);
        	}
        	else
        	{
        		ratio1=0.0;
        		ratio2=0.0;
        	}
        	query.addToCumulativeClicksProbability(ratio1);
        	query.addToCumulativeConversionsProbability(ratio2);
        	
        	if (yday > 0)
        	{
        		/* update estimated clicks and conversions probability */
        		query.setEstClicksProbability(query.getCumulativeClicksProbability() / yday);
        		query.setEstConversionsProbability(query.getCumulativeConversionsProbability() / yday);
        	}
		}
	}
	
	public ModelerResult model(Query query, double bid, Ad ad, int day) {
		ModelerResult result = new ModelerResult(0.0,0.0,0.0);
		
		for(SmithBasicModelerQuery mquery : querySpace) {      	
			if (mquery.getQuery().equals(query)) {
				result.setImpressions(mquery.estImpressions[day]);
				result.setCpc(mquery.estCpc[day]);
				result.setPosition(mquery.estPositions[day]);
			}
		}
		return result;
	}
	
	public void simulationFinished() {
		querySpace.clear();		
	}
	
	public void simulationSetup() {
		decay = aaConfig.getPropertyAsDouble("Decay", DECAY_DEFAULT);
	}
	
	public void analizePositions(QueryReport queryReport){
		//assemble a HashMap of all average positions per query
		HashMap<Double, String> allAvgPositions = new HashMap<Double, String>();
		PositionAnalyzer posAn = null;
		for (SmithBasicModelerQuery query : querySpace) {
			Set<String> advertisers = queryReport.advertisers(query.getQuery());
			int localImpressions = queryReport.getImpressions(query.getQuery());
			int maxImps = 3 * localImpressions; //TODO: get the actual number
			double avgPos = 0.0;
			for (String adv : advertisers){
				if (adv != null){
					avgPos = queryReport.getPosition(query.getQuery(), adv);
					if (!Double.isNaN(avgPos)){
						//truncate avgPos before inserting to map
						avgPos = (int)(avgPos / PRECISION);
						avgPos = avgPos * PRECISION;
						allAvgPositions.put(avgPos, adv);
					}
				}
			}
			posAn = new PositionAnalyzer(allAvgPositions, localImpressions, maxImps);
			// update query.estPositions[yday]
		}
	}
}
