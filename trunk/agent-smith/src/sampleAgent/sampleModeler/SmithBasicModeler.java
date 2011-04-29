package sampleAgent.sampleModeler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import trainer.GameLogDataStruct;
import trainer.Constants.LogBidBundleReportParams;
import trainer.Constants.LogQueryType;
import trainer.Constants.LogSlotInfoReportParams;


import edu.umich.eecs.tac.props.Ad;
import edu.umich.eecs.tac.props.Query;
import edu.umich.eecs.tac.props.QueryReport;
import edu.umich.eecs.tac.props.SalesReport;
import arch.Modeler;

public class SmithBasicModeler extends Modeler {

	double decay;
    protected static double DECAY_DEFAULT = 0.1;
    private static final double PRECISION = 0.0001;
    private static final int numOfPlayers = 8;

	protected Queue<SmithBasicModelerQuery> querySpace;
	protected HashMap<Query, HashMap<Double, Integer>> avgBidPositions;
	
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
	
	public HashMap<Double, Integer> estimateBestBid (int gameId, LogQueryType qt) {
		ArrayList<Double>[] posBidArray = new ArrayList[numOfPlayers + 1];
		int pos = 0;
		double bid = 0.0;
		HashMap<Double, Integer> avgBidPos = new HashMap<Double, Integer>();
		
		String[] bidResults = GameLogDataStruct.getInstance().getGamesReports().get(gameId).getBidBundleReport().getSpecificParticipantAllBidBundleReport("myAgent").get(qt).get(LogBidBundleReportParams.bid);
		String[] slotInfoResults = GameLogDataStruct.getInstance().getGamesReports().get(gameId).getSlotInfoReport().getSpecificParticipantAllSlotInfoReport("myAgent").get(LogSlotInfoReportParams.regularSlots);
		
		//init
		for (int i = 0; i <= numOfPlayers; i++)
		{
			posBidArray[i] = new ArrayList<Double>();
		}
		
		for (int i = 0; i <= bidResults.length; i++)
		{
			pos = Integer.parseInt(slotInfoResults[i]);
			bid = Double.parseDouble(bidResults[i]);
			
			posBidArray[pos].add(bid);
		}
		
		for (int i = 1; i <= numOfPlayers; i++)
		{
			double avg = 0.0;
			double sum = 0.0;
			for (int j = 0; j <= posBidArray[i].size(); j++)
			{
				sum += posBidArray[i].get(j);
			}
			avg = sum / posBidArray[i].size();
			avgBidPos.put(avg, i);
		}
		
		return avgBidPos;
	}
	
	public int getEstimatedPosByBid(Query query, double bid){
		HashMap<Double, Integer> localMap;
		if (avgBidPositions.containsKey(query) == false){
			//TODO: change game ID from 0 to an actual game ID
			avgBidPositions.put(query, estimateBestBid(0, convertToQueryLogType(query)));
		}
		
		localMap = avgBidPositions.get(query);
		double tmpLow = 0.0;
		double tmpHigh = 0.0;
		for (int index = 0; index < localMap.size(); index++){
			if (bid >= (Double)localMap.keySet().toArray()[index]){
				tmpLow = (Double)localMap.keySet().toArray()[index];
				try {
					tmpHigh = (Double)localMap.keySet().toArray()[index + 1];
				}
				catch (ArrayIndexOutOfBoundsException e){
					return numOfPlayers;
				}
			}
		}
		
		if ((bid - tmpLow) < (tmpHigh - bid)){
			return localMap.get(tmpLow);
		}
		else {
			return localMap.get(tmpHigh);
		}
	}

	private LogQueryType convertToQueryLogType(Query query) {
		for (LogQueryType qt : LogQueryType.values())
		{
			if ((qt.getComponent().equalsIgnoreCase(query.getComponent())) &&
				(qt.getManufacturer().equalsIgnoreCase(query.getManufacturer()))){
				return qt;
			}
		}
		return null;
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
				//result.setPosition(mquery.estPositions[day]);
				result.setPosition(getEstimatedPosByBid(mquery.getQuery(), bid));
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
