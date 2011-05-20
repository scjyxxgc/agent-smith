package sampleAgent.sampleModeler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;

import trainer.Constants.LogQueryReportParams;
import trainer.GameLogDataStruct;
import trainer.Constants.LogBidBundleReportParams;
import trainer.Constants.LogQueryType;
import trainer.Constants.LogSalesReportParams;
import static arch.AgentConstants.TAU_SIMDAYS;

import edu.umich.eecs.tac.props.Ad;
import edu.umich.eecs.tac.props.Query;
import edu.umich.eecs.tac.props.QueryReport;
import edu.umich.eecs.tac.props.SalesReport;
import arch.Modeler;

public class SmithBasicModeler extends Modeler {

	private boolean CALC_CURR_GAME = true;
	double decay;
	protected static double DECAY_DEFAULT = 0.1;
	private static final int NUM_OF_PLAYERS = 8;
	private int simID = 0;

	protected Queue<SmithBasicModelerQuery> querySpace;
	protected HashMap<Query, TreeMap<Double, Double>> avgBidPositionsByLog;
	protected HashMap<Query, TreeMap<Double, Double>> avgBidPositionsByCurrGame;
	
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
		avgBidPositionsByLog = new HashMap<Query, TreeMap<Double,Double>>();
		avgBidPositionsByCurrGame = new HashMap<Query, TreeMap<Double,Double>>();
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
			} else {
				query.gameBids.add(0.0);
			}
			if (!Double.isNaN(queryReport.getPosition(query.getQuery(), this.aaAgent.getAdvertiserInfo().getAdvertiserId()))){
				query.gamePos.add(queryReport.getPosition(query.getQuery(), this.aaAgent.getAdvertiserInfo().getAdvertiserId()));
			} else {
				query.gamePos.add(0.0);
			}
		}
	}

	private TreeMap<Double, Double> estimatePosByLog (int gameId, LogQueryType qt) {
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
				posInfoResults.add(Double.parseDouble(strPosInfoResults[i]));
			}catch (Exception e) {
				posInfoResults.add(0.0);
			}
		}
		for (int i = 0; i < strBidResults.length-2; i++){
			try
			{
				bidResults.add(Double.parseDouble(strBidResults[i]));
			}catch (Exception e) {
				bidResults.add(0.0);
			}			
		}
		
		TreeMap<Double, Double> tmpMap = avgBidValCalc(bidResults, posInfoResults);
		return tmpMap;
	}
	
	private TreeMap<Double, Double> estimateIMPByLog (int gameId, LogQueryType qt) {
		String[] strBidResults;
		String[] strImpResults;

		ArrayList<Double>  bidResults;
		ArrayList<Double>  impResults;


		strBidResults		= GameLogDataStruct.getInstance().getGamesReports().get(gameId).getBidBundleReport().getSpecificParticipantAllBidBundleReport("Agent-Smith").get(qt).get(LogBidBundleReportParams.bid);
		strImpResults		= GameLogDataStruct.getInstance().getGamesReports().get(gameId).getSalesReport().getSpecificParticipantAllSalesReport("Agent-Smith").get(qt).get(LogSalesReportParams.impressions);
		
		bidResults 		= new ArrayList<Double>(strBidResults.length);
		impResults = new ArrayList<Double>(strImpResults.length);

		for (int i = 2; i < strImpResults.length; i++)
		{
			try
			{
				impResults.add(Double.parseDouble(strImpResults[i]));
			}catch (Exception e) {
				impResults.add(0.0);
			}
		}
		for (int i = 0; i < strBidResults.length-2; i++){
			try
			{
				bidResults.add(Double.parseDouble(strBidResults[i]));
			}catch (Exception e) {
				bidResults.add(0.0);
			}			
		}
		
		TreeMap<Double, Double> tmpMap = avgBidValCalc(bidResults, impResults);
		return tmpMap;
	}
	
	private TreeMap<Double, Double> estimateCPCByLog (int gameId, LogQueryType qt) {
		String[] strBidResults;
		String[] strCpcResults;

		ArrayList<Double>  bidResults;
		ArrayList<Double>  cpcResults;


		strBidResults		= GameLogDataStruct.getInstance().getGamesReports().get(gameId).getBidBundleReport().getSpecificParticipantAllBidBundleReport("Agent-Smith").get(qt).get(LogBidBundleReportParams.bid);
		strCpcResults		= GameLogDataStruct.getInstance().getGamesReports().get(gameId).getQueryReport().getSpecificParticipantAllQueryReport("Agent-Smith").get(qt).get(LogQueryReportParams.cpc);
		
		bidResults 		= new ArrayList<Double>(strBidResults.length);
		cpcResults = new ArrayList<Double>(strCpcResults.length);

		for (int i = 2; i < strCpcResults.length; i++)
		{
			try
			{
				cpcResults.add(Double.parseDouble(strCpcResults[i]));
			}catch (Exception e) {
				cpcResults.add(0.0);
			}
		}
		for (int i = 0; i < strBidResults.length-2; i++){
			try
			{
				bidResults.add(Double.parseDouble(strBidResults[i]));
			}catch (Exception e) {
				bidResults.add(0.0);
			}			
		}
		
		TreeMap<Double, Double> tmpMap = avgBidValCalc(bidResults, cpcResults);
		return tmpMap;
	}


	private TreeMap<Double, Double> estimatePosByCurrGame (SmithBasicModelerQuery query){
		TreeMap<Double, Double> tempMap = avgBidValCalc(query.gameBids, query.gamePos);

		return tempMap;
	}
	
	private TreeMap<Double, Double> avgBidValCalc(ArrayList<Double> bidResults, ArrayList<Double> valResults){
		TreeMap<Double, ArrayList<Double>> valBidArray = new TreeMap<Double, ArrayList<Double>>(); 
		TreeMap<Double, Double> avgBidVal = new TreeMap<Double, Double>();
		double bid = 0.0;
		double val = 0.0;
		
		for (int i = 0; i < bidResults.size(); i++){
			bid = bidResults.get(i);
			val = valResults.get(i);
			if (valBidArray.containsKey(val)){
				valBidArray.get(val).add(bid);
			}else{
				valBidArray.put(val, new ArrayList<Double>());
				valBidArray.get(val).add(bid);
			}
		}
		
		for (int i = 0; i < valBidArray.keySet().size(); i++) {
			Double tmpVal = (Double)valBidArray.keySet().toArray()[i];
			Double tmpAvg = calcAvgFromArray(valBidArray.get(tmpVal));
			avgBidVal.put(tmpAvg, tmpVal);
		}
		
		return avgBidVal;
	}

//	private TreeMap<Double, Double> avgBidPosCalc(ArrayList<Double> bidResults, ArrayList<Double> slotInfoResults) {
//		ArrayList<Double>[] posBidArray = new ArrayList[NUM_OF_PLAYERS + 1];
//		TreeMap<Double, Double> avgBidPos = new TreeMap<Double, Double>();
//		int pos = 0;
//		double bid = 0.0;
//		//init
//		for (int i = 0; i <= NUM_OF_PLAYERS; i++)
//		{
//			posBidArray[i] = new ArrayList<Double>();
//		}
//
//
//		for (int i = 0; i < bidResults.size(); i++)
//		{
//			pos = (int)Math.round(slotInfoResults.get(i));
//			bid = bidResults.get(i);
//			posBidArray[pos].add(bid);
//		}
//
//		for (int i = 1; i <= NUM_OF_PLAYERS; i++)
//		{
//			double avg = 0.0;
//			double sum = 0.0;
//			for (int j = 0; j < posBidArray[i].size(); j++)
//			{
//				if (i < posBidArray.length && null != posBidArray[i].get(j)){
//					sum += posBidArray[i].get(j);
//				}
//			}
//			avg = sum / (1 + posBidArray[i].size());
//			avgBidPos.put(avg, (double)i);
//		}
//
//		return avgBidPos;
//	}
	
	private Double calcAvgFromArray(ArrayList<Double> array){
		double avg = 0.0;
		double sum = 0.0;
		for (int i = 0; i < array.size(); i++)
		{
			sum += array.get(i);
		}
		avg = sum / array.size();
		return avg;
	}

	public double getEstimatedPosByBid(SmithBasicModelerQuery query, double bid, int day){
		TreeMap<Double, Double> localMap = new TreeMap<Double, Double>();
		int daySum = 0;
		double avgPos = 0.0;
		double avgPosLog = 0.0;
		double avgPosCurr = 0.0;
		double CURR_FACTOR = 0.0;
		double LOG_FACTOR = 0.0;
		boolean logExist=false;

		if (GameLogDataStruct.getInstance().getGamesReports().containsKey(simID-1) == true && 
				GameLogDataStruct.getInstance().getGamesReports().get(simID-1).getPublisherInfoReportLog().getSpecificParticipantAllPublisherInfoReport("Agent-Smith").isEmpty() == false)
				{
					logExist = true;
				}
		
		if (true  == logExist)
		{
			if (avgBidPositionsByLog.containsKey(query.getQuery()) == false){
				avgBidPositionsByLog.put(query.getQuery(), estimatePosByLog(simID-1, convertToQueryLogType(query.getQuery())));
			}
			daySum = TAU_SIMDAYS + day;
			LOG_FACTOR = 0.5;
			CURR_FACTOR = 0.5;
		} else {
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
			ranPos = generator.nextDouble();
			return ranPos * 7.0 + 1.0;
		}

		if (this.CALC_CURR_GAME){
			avgBidPositionsByCurrGame.put(query.getQuery(), estimatePosByCurrGame(query));
		}

		if (avgBidPositionsByLog.containsKey(query.getQuery()) == true){
			
			localMap.putAll(avgBidPositionsByLog.get(query.getQuery()));
			avgPosLog = evalMedFromMap(bid, localMap);
		}

		if (this.CALC_CURR_GAME && avgBidPositionsByCurrGame.containsKey(query.getQuery()) == true){
			localMap.putAll(avgBidPositionsByCurrGame.get(query.getQuery()));
			avgPosCurr = evalMedFromMap(bid, localMap);
		} 

		double logPrec = ((double)TAU_SIMDAYS) / ((double)daySum);
		double currPrec = ((double)day) / ((double)daySum);
		avgPos = (avgPosLog * logPrec * LOG_FACTOR) + (avgPosCurr * currPrec * CURR_FACTOR);
		System.out.println("printing mult factors for log avg pos. avg pos log: " + avgPosLog + " log precentage: " + logPrec + " LOG_FACTOR: " + LOG_FACTOR);
		System.out.println("printing mult factors for curr avg pos. avg pos curr: " + avgPosCurr + " curr precentage: " + currPrec + " CURR_FACTOR: " + CURR_FACTOR);
		System.out.println("returning avg pos after factoring and math to model: "+avgPos);
		return avgPos;
	}

	private double evalMedFromMap(double val, TreeMap<Double, Double> localMap) {
		double med = 0.0;
		double tmpLow = 0.0;
		double tmpHigh = 0.0;
		
		Double minLowInMap = (Double)localMap.keySet().toArray()[0];
		
		if (val < minLowInMap){
			return localMap.get(minLowInMap);
		}
				
		for (int index = 0; index < localMap.size(); index++){
			if (val >= (Double)localMap.keySet().toArray()[index]){
				tmpLow = (Double)localMap.keySet().toArray()[index];
				try {
					tmpHigh = (Double)localMap.keySet().toArray()[index + 1];
				}
				catch (ArrayIndexOutOfBoundsException e){
					tmpHigh = tmpLow;
				}
				break;
			}
		}
		
		if (Math.abs(val - tmpLow) <= Math.abs(tmpHigh - val)){
			med = localMap.get(tmpLow);
		}
		else {
			med = localMap.get(tmpHigh);
		}
		return med;
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
	protected void printMap(Map<Double, Double> theItemListMap)
	{
		for(Map.Entry<Double, Double> queueList : theItemListMap.entrySet())
		{
			System.out.println(queueList.getKey() + " : " + theItemListMap.get(queueList.getKey()));
		}
		System.out.println();
	}
}
