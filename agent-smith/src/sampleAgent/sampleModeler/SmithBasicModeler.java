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
	private static final int NUM_OF_PLAYERS = 8;
	private int simID = 0;
	private int joinNumber = 0;

	protected Queue<SmithBasicModelerQuery> querySpace;
	protected HashMap<Query, HashMap<Double, Integer>> avgBidPositionsByLog;
	protected HashMap<Query, HashMap<Double, Integer>> avgBidPositionsByCurrGame;

	public void simulationReady() {
		Set<Query> querySet = aaAgent.getQuerySet();
		simID = this.aaAgent.getStartInfo().getSimulationID();
		//TODO - @sekely - update and/or init joinNumber
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
		avgBidPositionsByLog = new HashMap<Query, HashMap<Double,Integer>>();
		avgBidPositionsByCurrGame = new HashMap<Query, HashMap<Double,Integer>>();
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
			
			query.gameBids.add(queryReport.getPosition(query.getQuery(), this.aaAgent.getAdvertiserInfo().getAdvertiserId()));
			query.gamePos.add((int)queryReport.getPosition(query.getQuery(), this.aaAgent.getAdvertiserInfo().getAdvertiserId()));
		}
	}

	public HashMap<Double, Integer> estimatePosByLog (int gameId, LogQueryType qt) {
		String[] strBidResults;
		String[] strSlotInfoResults;

		Double[]  bidResults;
		Integer[] slotInfoResults;

		if (null != GameLogDataStruct.getInstance().getGamesReports().get(gameId)){
			strBidResults		= GameLogDataStruct.getInstance().getGamesReports().get(gameId).getBidBundleReport().getSpecificParticipantAllBidBundleReport("Agent-Smith").get(qt).get(LogBidBundleReportParams.bid);
			strSlotInfoResults	= GameLogDataStruct.getInstance().getGamesReports().get(gameId).getSlotInfoReport().getSpecificParticipantAllSlotInfoReport("Agent-Smith").get(LogSlotInfoReportParams.regularSlots);
		} else {
			strBidResults		= new String[0];
			strSlotInfoResults	= new String[0];
		}

		bidResults 		= new Double[strBidResults.length];
		slotInfoResults = new Integer[strSlotInfoResults.length];

		for (int i = 0; i < bidResults.length; i++)
		{
			if (null != strSlotInfoResults[i]){
				slotInfoResults[i] = Integer.parseInt(strSlotInfoResults[i]);
			} else {
				slotInfoResults[i] = (int) Math.ceil(NUM_OF_PLAYERS / 2);
			}

			if (null != strBidResults[i]){
				bidResults[i] = Double.parseDouble(strBidResults[i]);
			} else {
				bidResults[i] = 3.0;
			}
		}
		return avgBidPosCalc(bidResults, slotInfoResults);
	}

	public HashMap<Double, Integer> estimatePosByCurrGame (SmithBasicModelerQuery query){
		Double[]	bidResults		= new Double[query.gameBids.size()];
		Integer[]	slotInfoResults	= new Integer[query.gamePos.size()];
		
		return avgBidPosCalc(query.gameBids.toArray(bidResults), query.gamePos.toArray(slotInfoResults));
	}

	private HashMap<Double, Integer> avgBidPosCalc(Double[] bidResults, Integer[] slotInfoResults) {
		ArrayList<Double>[] posBidArray = new ArrayList[NUM_OF_PLAYERS + 1];
		HashMap<Double, Integer> avgBidPos = new HashMap<Double, Integer>();
		int pos = 0;
		double bid = 0.0;
		//init
		for (int i = 0; i <= NUM_OF_PLAYERS; i++)
		{
			posBidArray[i] = new ArrayList<Double>();
		}


		for (int i = 0; i < bidResults.length; i++)
		{
			pos = slotInfoResults[i];
			bid = bidResults[i];
			posBidArray[pos].add(bid);
		}

		for (int i = 1; i <= NUM_OF_PLAYERS; i++)
		{
			double avg = 0.0;
			double sum = 0.0;
			for (int j = 0; j < posBidArray[i].size(); j++)
			{
				if (i < posBidArray.length && null != posBidArray[i].get(j)){
					sum += posBidArray[i].get(j);
				}
			}
			avg = sum / posBidArray[i].size();
			avgBidPos.put(avg, i);
		}

		return avgBidPos;
	}

	public double getEstimatedPosByBid(SmithBasicModelerQuery query, double bid, int day){
		HashMap<Double, Integer> localMap = new HashMap<Double, Integer>();
		int daySum = 0;
		double avgPos = 0.0;
		double avgPosLog = 0.0;
		double avgPosCurr = 0.0;
		double CURR_FACTOR = 0.0;
		double LOG_FACTOR = 0.0;
		// if we played at least once
		if (joinNumber > 0)
		{
			if (avgBidPositionsByLog.containsKey(query.getQuery()) == false){
				avgBidPositionsByLog.put(query.getQuery(), estimatePosByLog(simID-1, convertToQueryLogType(query.getQuery())));
			}
			daySum = 60 + day;
			LOG_FACTOR = 0.5;
			CURR_FACTOR = 0.5;
		} else {
			daySum = day;
			LOG_FACTOR = 0.0;
			CURR_FACTOR = 1.0;
		}
		
		if (avgBidPositionsByCurrGame.containsKey(query.getQuery()) == false){
			avgBidPositionsByCurrGame.put(query.getQuery(), estimatePosByCurrGame(query));
		}
		

		if (avgBidPositionsByLog.containsKey(query) == true){
			localMap.putAll(avgBidPositionsByLog.get(query));
			double tmpLowLog = 0.0;
			double tmpHighLog = 0.0;
			for (int index = 0; index < localMap.size(); index++){
				if (bid >= (Double)localMap.keySet().toArray()[index]){
					tmpLowLog = (Double)localMap.keySet().toArray()[index];
					try {
						tmpHighLog = (Double)localMap.keySet().toArray()[index + 1];
					}
					catch (ArrayIndexOutOfBoundsException e){
						tmpHighLog = tmpLowLog;
					}
				}
			}

			if (Math.abs(bid - tmpLowLog) <= Math.abs(tmpHighLog - bid)){
				avgPosLog = localMap.get(tmpLowLog);
			}
			else {
				avgPosLog = localMap.get(tmpHighLog);
			}
		}
			
		if (avgBidPositionsByCurrGame.containsKey(query) == true){
			localMap.putAll(avgBidPositionsByCurrGame.get(query));
			double tmpLowCurr = 0.0;
			double tmpHighCurr = 0.0;
			for (int index = 0; index < localMap.size(); index++){
				if (bid >= (Double)localMap.keySet().toArray()[index]){
					tmpLowCurr = (Double)localMap.keySet().toArray()[index];
					try {
						tmpHighCurr = (Double)localMap.keySet().toArray()[index + 1];
					}
					catch (ArrayIndexOutOfBoundsException e){
						tmpHighCurr = tmpLowCurr;
					}
				}
			}

			if (Math.abs(bid - tmpLowCurr) <= Math.abs(tmpHighCurr - bid)){
				avgPosCurr = localMap.get(tmpLowCurr);
			}
			else {
				avgPosCurr = localMap.get(tmpHighCurr);
			}
		}
		
		avgPos = (avgPosLog * 0.5 * LOG_FACTOR) + (avgPosCurr * day/daySum * CURR_FACTOR);
		return avgPos;
		
	}

	private LogQueryType convertToQueryLogType(Query query) {
		boolean compo = false;
		boolean manuf = false;
		LogQueryType retVal = null;

		for (LogQueryType qt : LogQueryType.values())
		{
			if (qt.getComponent() == null){
				if (query.getComponent() == null){
					compo = true;
				} else {
					compo = false;
				}
			} else {
				if (qt.getComponent().equalsIgnoreCase(query.getComponent())){
					compo = true;
				} else {
					compo = false;
				}
			}

			if (qt.getManufacturer() == null){
				if (query.getManufacturer() == null){
					manuf = true;
				} else {
					manuf = false;
				}
			} else {
				if (qt.getManufacturer().equalsIgnoreCase(query.getManufacturer())){
					manuf = true;
				} else {
					manuf = false;
				}
			}

			if (true == compo && true == manuf){
				retVal = qt;
			}
		}
		return retVal;
	}

	public void handleSalesReport(SalesReport salesReport, int yday) {
		int conv = 0;
		double rev = 0.0;

		for(SmithBasicModelerQuery query : querySpace) { 
			conv = salesReport.getConversions(query.getQuery());
			rev = salesReport.getRevenue(query.getQuery());

			query.setConversions(yday, conv);
			query.setRevenue(yday, rev);
		}
	}

	public ModelerResult model(Query query, double bid, Ad ad, int day) {
		ModelerResult result = new ModelerResult(0.0,0.0,0.0);

		for(SmithBasicModelerQuery mquery : querySpace) {      	
			if (mquery.getQuery().equals(query)) {
				result.setImpressions(mquery.estImpressions[day]);
				result.setCpc(mquery.estCpc[day]);
				result.setPosition(getEstimatedPosByBid(mquery, bid, day));
			}
		}
		return result;
	}

	public void simulationFinished() {
		querySpace.clear();
		avgBidPositionsByLog.clear();
		avgBidPositionsByCurrGame.clear();
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
