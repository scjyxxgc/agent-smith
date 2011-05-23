package sampleAgent.sampleModeler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

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
	private int simID = 0;
	boolean logExist = false;

	protected Queue<SmithBasicModelerQuery> querySpace;
	protected HashMap<Query, TreeMap<Double, Double>> avgBidPosByLog;
	protected HashMap<Query, TreeMap<Double, Double>> avgBidImpByLog;
	protected HashMap<Query, TreeMap<Double, Double>> avgBidCpcByLog;
	protected HashMap<Query, TreeMap<Double, Double>> avgBidPosByCurrGame;
	protected HashMap<Query, TreeMap<Double, Double>> avgBidImpByCurrGame;
	protected HashMap<Query, TreeMap<Double, Double>> avgBidCpcByCurrGame;
	
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
		avgBidPosByLog = new HashMap<Query, TreeMap<Double,Double>>();
		avgBidImpByLog = new HashMap<Query, TreeMap<Double,Double>>();
		avgBidCpcByLog = new HashMap<Query, TreeMap<Double,Double>>();
		avgBidPosByCurrGame = new HashMap<Query, TreeMap<Double,Double>>();
		avgBidImpByCurrGame = new HashMap<Query, TreeMap<Double,Double>>();
		avgBidCpcByCurrGame = new HashMap<Query, TreeMap<Double,Double>>();
	}

	public void handleQueryReport(QueryReport queryReport, int yday) {
		for(SmithBasicModelerQuery query : querySpace) {      	
			query.cpc[yday] = queryReport.getCPC(query.getQuery());
			query.impressions[yday] = queryReport.getImpressions(query.getQuery());

			/* update estimates */
			if (!query.cpc[yday].isNaN()){
				query.estCpc[yday+2] = decay*query.cpc[yday] + (1-decay)*query.estCpc[yday+1];
				query.gameCpc.add(query.cpc[yday]);
			} else {
				query.estCpc[yday+2] = query.estCpc[yday+1];
				query.gameCpc.add(query.estCpc[yday+1]);
			}

			query.estImpressions[yday+2] = decay*query.impressions[yday] + (1-decay)*query.estImpressions[yday+1];
			query.gameImp.add((Double)(double)(query.impressions[yday]));
			
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
	
	private TreeMap<Double, Double> estimateImpByLog (int gameId, LogQueryType qt) {
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
	
	private TreeMap<Double, Double> estimateCpcByLog (int gameId, LogQueryType qt) {
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


	private TreeMap<Double, Double> estimateByBidFromListByCurrGame (SmithBasicModelerQuery query, ArrayList<Double> list){
		TreeMap<Double, Double> tempMap = avgBidValCalc(query.gameBids, list);

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

	public HashMap<String, Double> getEstimatedByBid(SmithBasicModelerQuery query, double bid, int day){
		int daySum = 0;
		double avgPos = 0.0;
		double avgImp = 0.0;
		double avgCpc = 0.0;
		double avgPosLog = 0.0;
		double avgImpLog = query.estImpressions[day];
		double avgCpcLog = query.estCpc[day];
		double avgPosCurr = 0.0;
		double avgImpCurr = query.estImpressions[day];
		double avgCpcCurr = query.estCpc[day];;
		double CURR_FACTOR = 0.0;
		double LOG_FACTOR = 0.0;
		HashMap<String, Double> retMap = new HashMap<String, Double>();
		if (GameLogDataStruct.getInstance().getGamesReports().containsKey(simID-1) == true && 
			GameLogDataStruct.getInstance().getGamesReports().get(simID-1).getPublisherInfoReportLog().getSpecificParticipantAllPublisherInfoReport("Agent-Smith").isEmpty() == false)
		{
			logExist = true;
//			System.out.println("modeler: log exists");
		} else {
			logExist = false;
//			System.out.println("modeler: log NOT exists");
		}
		
		if (true == logExist)
		{
			daySum = TAU_SIMDAYS + day;
			LOG_FACTOR = 0.5;
			CURR_FACTOR = 0.5;
			if (avgBidImpByLog.containsKey(query.getQuery()) == false){
				avgBidImpByLog.put(query.getQuery(), estimateImpByLog(simID-1, convertToQueryLogType(query.getQuery())));
			}
//			System.out.println("modeler: ImpByLog map");
//			printMap(avgBidImpByLog.get(query.getQuery()));
			avgImpLog = evalMedFromMap(bid, avgBidImpByLog.get(query.getQuery()));

			if (avgBidCpcByLog.containsKey(query.getQuery()) == false){
				avgBidCpcByLog.put(query.getQuery(), estimateCpcByLog(simID-1, convertToQueryLogType(query.getQuery())));
			}
//			System.out.println("modeler: CpcByLog map");
//			printMap(avgBidCpcByLog.get(query.getQuery()));
			avgCpcLog = evalMedFromMap(bid, avgBidCpcByLog.get(query.getQuery()));
			
			if (avgBidPosByLog.containsKey(query.getQuery()) == false){
				avgBidPosByLog.put(query.getQuery(), estimatePosByLog(simID-1, convertToQueryLogType(query.getQuery())));
			}
//			System.out.println("modeler: PosByLog");
//			printMap(avgBidPositionsByLog.get(query.getQuery()));
			avgPosLog = evalMedFromMap(bid, avgBidPosByLog.get(query.getQuery()));
			
//			System.out.println("modeler: avgImpLog="+avgImpLog+" avgCpcLog="+avgCpcLog+" avgPosLog="+avgPosLog);
		
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
		}

		if (this.CALC_CURR_GAME){
			avgBidPosByCurrGame.put(query.getQuery(), estimateByBidFromListByCurrGame(query, query.gamePos));
			avgBidImpByCurrGame.put(query.getQuery(), estimateByBidFromListByCurrGame(query, query.gameImp));
			avgBidCpcByCurrGame.put(query.getQuery(), estimateByBidFromListByCurrGame(query, query.gameCpc));
			
//			System.out.println("modeler: PosByCurr map");
//			printMap(avgBidPosByCurrGame.get(query.getQuery()));
//			System.out.println("modeler: ImpByCurr map");
//			printMap(avgBidImpByCurrGame.get(query.getQuery()));
//			System.out.println("modeler: CpcByCurr map");
//			printMap(avgBidCpcByCurrGame.get(query.getQuery()));
			
			avgPosCurr = evalMedFromMap(bid, avgBidPosByCurrGame.get(query.getQuery()));
			avgImpCurr = evalMedFromMap(bid, avgBidImpByCurrGame.get(query.getQuery()));
			avgCpcCurr = evalMedFromMap(bid, avgBidCpcByCurrGame.get(query.getQuery()));
			
//			System.out.println("modeler: avgImpCurr="+avgImpCurr+" avgCpcCurr="+avgCpcCurr+" avgPosCurr="+avgPosCurr);
		}

		double logPrec = ((double)TAU_SIMDAYS) / ((double)daySum);
		double currPrec = ((double)day) / ((double)daySum);
		
		avgPos = (avgPosLog * logPrec * LOG_FACTOR) + (avgPosCurr * currPrec * CURR_FACTOR);
		avgImp = (avgImpLog * logPrec * LOG_FACTOR) + (avgImpCurr * currPrec * CURR_FACTOR);
		avgCpc = (avgCpcLog * logPrec * LOG_FACTOR) + (avgCpcCurr * currPrec * CURR_FACTOR);
		
		retMap.put("Impressions", avgImp);
		retMap.put("Position", avgPos);
		retMap.put("cpc", avgCpc);
		
		return retMap;
	}

	private double evalMedFromMap(double val, TreeMap<Double, Double> localMap) {
		double tmpLow = (Double)localMap.keySet().toArray()[0];
		double tmpHigh = (Double)localMap.keySet().toArray()[localMap.keySet().size()-1];
		double med = 0.0;
		
		if (val < tmpLow){
			return localMap.get(tmpLow);
		}
		
		if (val > tmpHigh){
			return localMap.get(tmpHigh);
		}
		
		for (int index = 0; index < localMap.size(); index++){
			if (val <= (Double)localMap.keySet().toArray()[index]){
				tmpHigh = (Double)localMap.keySet().toArray()[index];
				try {
					tmpLow = (Double)localMap.keySet().toArray()[index - 1];
				}
				catch (ArrayIndexOutOfBoundsException e){
					tmpLow = tmpHigh;
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
//		System.out.println("modeler: (eval med) val="+val+", med="+med+", tmpLow="+tmpLow+", tmpHigh="+tmpHigh);
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

	public void handleSalesReport(SalesReport salesReport, int yday) {}

	public ModelerResult model(Query query, double bid, Ad ad, int day) {
		ModelerResult result = new ModelerResult(0.0,0.0,0.0);
		HashMap<String, Double> modelMap = new HashMap<String, Double>();

		for(SmithBasicModelerQuery mquery : querySpace) {      	
			if (mquery.getQuery().equals(query)) {
				modelMap = getEstimatedByBid(mquery, bid, day);
				result.setPosition(modelMap.get("Position"));
				result.setCpc(modelMap.get("cpc"));
				result.setImpressions(modelMap.get("Impressions"));
			}
		}
		return result;
	}

	public void simulationFinished() {
		querySpace.clear();
		avgBidPosByLog.clear();
		avgBidImpByLog.clear();
		avgBidCpcByLog.clear();
		avgBidPosByCurrGame.clear();
		avgBidImpByCurrGame.clear();
		avgBidCpcByCurrGame.clear();
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
