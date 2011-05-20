package sampleAgent.sampleModeler;

import java.awt.print.Printable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Set;

import trainer.Constants.LogQueryReportParams;
import trainer.GameLogDataStruct;
import trainer.Constants.LogBidBundleReportParams;
import trainer.Constants.LogQueryType;
import trainer.Constants.LogSalesReportParams;
import trainer.Constants.LogSlotInfoReportParams;

import static arch.AgentConstants.TAU_SIMDAYS;

import edu.umich.eecs.tac.props.Ad;
import edu.umich.eecs.tac.props.Query;
import edu.umich.eecs.tac.props.QueryReport;
import edu.umich.eecs.tac.props.SalesReport;
import arch.Modeler;

public class SmithBasicModeler extends Modeler {

	private boolean CALC_CURR_GAME = false;
	double decay;
	protected static double DECAY_DEFAULT = 0.1;
	private static final int NUM_OF_PLAYERS = 8;
	private int simID = 0;

	protected Queue<SmithBasicModelerQuery> querySpace;
	protected HashMap<Query, HashMap<Double, Integer>> avgBidPositionsByLog;
	protected HashMap<Query, HashMap<Double, Integer>> avgBidPositionsByCurrGame;
	
	//for debug use: using random pos on first game
	private Random generator = new Random( 19580427 );
	private double ranPos;

	public void simulationReady() {
		Set<Query> querySet = aaAgent.getQuerySet();
		simID = this.aaAgent.getStartInfo().getSimulationID();
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

			if (!Double.isNaN(GameLogDataStruct.getInstance().getLastBid(query.getQuery()))){
				query.gameBids.add(GameLogDataStruct.getInstance().getLastBid(query.getQuery()));
//				System.out.println("modeler: last bid was: " +GameLogDataStruct.getInstance().getLastBid(query.getQuery()));
			} else {
				query.gameBids.add(0.0);
//				System.out.println("modeler: last bid was: 0");
			}
			if (!Double.isNaN(queryReport.getPosition(query.getQuery(), this.aaAgent.getAdvertiserInfo().getAdvertiserId()))){
				query.gamePos.add(queryReport.getPosition(query.getQuery(), this.aaAgent.getAdvertiserInfo().getAdvertiserId()));
//				System.out.println("modeler: last pos was: "+queryReport.getPosition(query.getQuery(), this.aaAgent.getAdvertiserInfo().getAdvertiserId()));
			} else {
				query.gamePos.add(0.0);
//				System.out.println("modeler: last pos was: 0");
			}
		}
	}

	public HashMap<Double, Integer> estimatePosByLog (int gameId, LogQueryType qt) {
		String[] strBidResults;
		String[] strPosInfoResults;

		ArrayList<Double>  bidResults;
		ArrayList<Double> posInfoResults;


		strBidResults		= GameLogDataStruct.getInstance().getGamesReports().get(gameId).getBidBundleReport().getSpecificParticipantAllBidBundleReport("Agent-Smith").get(qt).get(LogBidBundleReportParams.bid);
		strPosInfoResults	= GameLogDataStruct.getInstance().getGamesReports().get(gameId).getQueryReport().getSpecificParticipantAllQueryReport("Agent-Smith").get(qt).get(LogQueryReportParams.position);
		
		bidResults 		= new ArrayList<Double>(strBidResults.length);
		posInfoResults = new ArrayList<Double>(strPosInfoResults.length);

		for (int i = 2; i < strPosInfoResults.length; i++)
		{
			try
			{
//				System.out.println("inserting: "+strPosInfoResults[i]);
				posInfoResults.add(Double.parseDouble(strPosInfoResults[i]));
			}catch (Exception e) {
//				System.out.println("caught exception, inserting 0.0");
				posInfoResults.add(0.0);
			}
		}
		for (int i = 0; i < strBidResults.length-2; i++){
			try
			{
//				System.out.println("inserting: "+strBidResults[i]);
				bidResults.add(Double.parseDouble(strBidResults[i]));
			}catch (Exception e) {
//				System.out.println("caught exception, inserting 0.0");
				bidResults.add(0.0);
			}			
		}
		
		//debug
//		System.out.println("printing new pos results. size is: " + posInfoResults.size());
//		for (int i = 0; i < posInfoResults.size(); i++) {
//			System.out.print(posInfoResults.get(i)+ " ");
//		}
//		System.out.println();
//		System.out.println("printing new bid results. size is: " + bidResults.size());
//		for (int i = 0; i < bidResults.size(); i++) {
//			System.out.print(bidResults.get(i)+ " ");
//		}
//		System.out.println();
		
		HashMap<Double, Integer> tmpMap = avgBidPosCalc(bidResults, posInfoResults);
//		System.out.println("printing avg map after avgBidPosCalc");
//		printMap(tmpMap);
		return tmpMap;
	}

	public HashMap<Double, Integer> estimatePosByCurrGame (SmithBasicModelerQuery query){
		HashMap<Double, Integer> tempMap = avgBidPosCalc(query.gameBids, query.gamePos);

		return tempMap;
	}

	private HashMap<Double, Integer> avgBidPosCalc(ArrayList<Double> bidResults, ArrayList<Double> slotInfoResults) {
		ArrayList<Double>[] posBidArray = new ArrayList[NUM_OF_PLAYERS + 1];
		HashMap<Double, Integer> avgBidPos = new HashMap<Double, Integer>();
		int pos = 0;
		double bid = 0.0;
		//init
		for (int i = 0; i <= NUM_OF_PLAYERS; i++)
		{
			posBidArray[i] = new ArrayList<Double>();
		}


		for (int i = 0; i < bidResults.size(); i++)
		{
			pos = (int)Math.round(slotInfoResults.get(i));
			bid = bidResults.get(i);
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
			avg = sum / (1 + posBidArray[i].size());
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
		boolean logExist=false;

		//debug prints
		//System.out.println("simID = " + simID);
		//System.out.println("GameLogDataStruct.getInstance().getGamesReports().containsKey("+(simID-1)+") = "+GameLogDataStruct.getInstance().getGamesReports().containsKey(simID));

		// if we played at least once
		// if (joinNumber > 0)
		if (GameLogDataStruct.getInstance().getGamesReports().containsKey(simID-1) == true && 
				GameLogDataStruct.getInstance().getGamesReports().get(simID-1).getPublisherInfoReportLog().getSpecificParticipantAllPublisherInfoReport("Agent-Smith").isEmpty() == false)
				{
					logExist = true;
				}
		
		if (true  == logExist)
		{
			System.out.println("modeler: Past log exists");
			//debug prints
			//System.out.println("GameLogDataStruct.getInstance().getGamesReports().get("+(simID-1)+").getPublisherInfoReportLog().getSpecificParticipantAllPublisherInfoReport(\"Agent-Smith\").isEmpty() = "+GameLogDataStruct.getInstance().getGamesReports().get(simID-1).getPublisherInfoReportLog().getSpecificParticipantAllPublisherInfoReport("Agent-Smith").isEmpty());
			//System.out.println("\nUsing data from log of game number " + (simID-1));
			if (avgBidPositionsByLog.containsKey(query.getQuery()) == false){
				avgBidPositionsByLog.put(query.getQuery(), estimatePosByLog(simID-1, convertToQueryLogType(query.getQuery())));
			}
			daySum = TAU_SIMDAYS + day;
			LOG_FACTOR = 0.5;
			CURR_FACTOR = 0.5;
		} else {
//			System.out.println("modeler: Past log not exsist");
			daySum = day;
			LOG_FACTOR = 0.0;
			CURR_FACTOR = 1.0;
		}
		
		if (this.CALC_CURR_GAME == false && logExist == true){
			LOG_FACTOR = 1.0;
			CURR_FACTOR = 0.0;
		}
		if (this.CALC_CURR_GAME == false && logExist == false){
			LOG_FACTOR = 0.0;
			CURR_FACTOR = 0.0;
			//for debug: on first game we will "spray" the position
			ranPos = generator.nextDouble();
			return ranPos * 7.0 + 1.0;
		}

		if (this.CALC_CURR_GAME){
			avgBidPositionsByCurrGame.put(query.getQuery(), estimatePosByCurrGame(query));
		}

		if (avgBidPositionsByLog.containsKey(query.getQuery()) == true){
			System.out.println("modeler: printing local map for query: "+query.getQuery());
			
			localMap.putAll(avgBidPositionsByLog.get(query.getQuery()));
			printMap(localMap);
			double tmpLowLog = 0.0;
			double tmpHighLog = 0.0;
//			System.out.println("local map size: " + localMap.size());
			for (int index = 0; index < localMap.size(); index++){
				if (bid >= (Double)localMap.keySet().toArray()[index]){
					tmpLowLog = (Double)localMap.keySet().toArray()[index];
					try {
						tmpHighLog = (Double)localMap.keySet().toArray()[index + 1];
					}
					catch (ArrayIndexOutOfBoundsException e){
						tmpHighLog = tmpLowLog;
					}
					break;
				}
			}

			if (Math.abs(bid - tmpLowLog) <= Math.abs(tmpHighLog - bid)){
				avgPosLog = localMap.get(tmpLowLog);
			}
			else {
				avgPosLog = localMap.get(tmpHighLog);
			}
//			System.out.println("printing data from log, for query: " + query.getQuery());
//			System.out.println("bid was: " + bid + " tmp low is: " + tmpLowLog + " tmp high is: " + tmpHighLog);
		}

		if (this.CALC_CURR_GAME && avgBidPositionsByCurrGame.containsKey(query.getQuery()) == true){
//			System.out.println("modeler: printing curr game map for query: "+query.getQuery());
//			printMap(avgBidPositionsByCurrGame.get(query.getQuery()));
			
			localMap.putAll(avgBidPositionsByCurrGame.get(query.getQuery()));
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

//			System.out.println("modeler: tmpLowCurr = " + tmpLowCurr + ", tmpHighCurr = " + tmpHighCurr);
			if (Math.abs(bid - tmpLowCurr) <= Math.abs(tmpHighCurr - bid)){
				avgPosCurr = localMap.get(tmpLowCurr);
			}
			else {
				avgPosCurr = localMap.get(tmpHighCurr);
			}
//			System.out.println("modeler: avgPosCurr = " + avgPosCurr);
		} else{
			System.out.println("not calculating curr game");
		}

		double logPrec = ((double)TAU_SIMDAYS) / ((double)daySum);
		double currPrec = ((double)day) / ((double)daySum);
		avgPos = (avgPosLog * logPrec * LOG_FACTOR) + (avgPosCurr * currPrec * CURR_FACTOR);
//		System.out.println("printing mult factors for log avg pos. avg pos log: " + avgPosLog + " TAU_SIMDAYS/daySum: " + logPrec + " LOG_FACTOR: " + LOG_FACTOR);
//		System.out.println("returning avg pos after factoring and math to model: "+avgPos);
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
				
				double tmp = getEstimatedPosByBid(mquery, bid, day);
				result.setPosition(tmp);
				System.out.println("modeler: modeler results position to model method is: " + tmp);
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

	protected void printList(List<Double> theItemList)
	{
		for (Double item : theItemList)
		{
			System.out.println(item + ",  " );
		}
		System.out.println();
	}

	protected void printList(ArrayList<Integer> theItemList)
	{
		for (Integer item : theItemList)
		{
			System.out.println(item + ",  " );
		}
		System.out.println();
	}

	/**
	 * 
	 */
	protected void printMap(Map<Double, Integer> theItemListMap)
	{
		for(Map.Entry<Double, Integer> queueList : theItemListMap.entrySet())
		{
			System.out.println(queueList.getKey() + " : " + theItemListMap.get(queueList.getKey()));
		}
		System.out.println();
	}
}
