package trainer;

import java.io.IOException;
import java.text.ParseException;

import se.sics.isl.util.IllegalConfigurationException;
import se.sics.tasim.logtool.LogHandler;
import se.sics.tasim.logtool.LogReader;
import trainer.Constants.LogBidBundleReportParams;
import trainer.Constants.LogProduct;
import trainer.Constants.LogPublisherInfoReportParams;
import trainer.Constants.LogQueryReportParams;
import trainer.Constants.LogQueryType;
import trainer.Constants.LogReserveInfoReportParams;
import trainer.Constants.LogRetailCatalogReportParams;
import trainer.Constants.LogSalesReportParams;
import trainer.Constants.LogSlotInfoReportParams;
import trainer.Constants.LogUserClickModelReportParams;

public class GameLogHandler extends LogHandler
{

	public GameLogHandler()
	{
	}

	/**
	 * Invoked when a new log file should be processed.
	 * 
	 * @param reader
	 *            the log reader for the log file.
	 */
	protected void start(LogReader reader) throws IllegalConfigurationException, IOException, ParseException
	{
		int gameId = 218; //TEMP - till i figure out how to integrate this into the game itself
		
		GameLogParser parser = new GameLogParser(reader);
		parser.start();
		parser.stop();

		System.out.println("\n*************************  General  ***************************");
		System.out.println("Is game report Empty: " + GameLogDataStruct.getInstance().getGamesReports().isEmpty());
		System.out.println("Key Set: " + GameLogDataStruct.getInstance().getGamesReports().keySet().toString());
		System.out.println("Game ID " + GameLogDataStruct.getInstance().getGamesReports().get(gameId).getGameId());
		
		System.out.println("\n*************************  Query Reports  ***************************");
		System.out.println("All particiants query reports " + GameLogDataStruct.getInstance().getGamesReports().get(gameId).getQueryReport().getAllParticipantsQueryReports().keySet().toString());
		System.out.println("All of myAgent query reports " + GameLogDataStruct.getInstance().getGamesReports().get(gameId).getQueryReport().getSpecificParticipantAllQueryReport("myAgent").keySet().toString());
		System.out.println("Participant Query Report my myAgent, query p_d:");
		String[] cpcREsults = GameLogDataStruct.getInstance().getGamesReports().get(gameId).getQueryReport().getSpecificParticipantAllQueryReport("myAgent").get(LogQueryType.p_d).get(LogQueryReportParams.cpc);
		for (int i = 0; i < cpcREsults.length; i++)
		{
			System.out.println(cpcREsults[i]);
		}
		
		System.out.println("\n*************************  Sales Reports  ***************************");
		System.out.println("All particiants sales reports " + GameLogDataStruct.getInstance().getGamesReports().get(gameId).getSalesReport().getAllParticipantsSalesReports().keySet().toString());
		System.out.println("All of myAgent slaes reports " + GameLogDataStruct.getInstance().getGamesReports().get(gameId).getSalesReport().getSpecificParticipantAllSalesReport("myAgent").keySet().toString());
		System.out.println("Participant sales Report my myAgent, query p_d:");
		String[] impressionsResults = GameLogDataStruct.getInstance().getGamesReports().get(gameId).getSalesReport().getSpecificParticipantAllSalesReport("myAgent").get(LogQueryType.p_d).get(LogSalesReportParams.impressions);
		for (int i = 0; i < impressionsResults.length; i++)
		{
			System.out.println(impressionsResults[i]);
		}
		
		System.out.println("\n*************************  Bid Bundle Reports  ***************************");
		System.out.println("All particiants bid bundle reports " + GameLogDataStruct.getInstance().getGamesReports().get(gameId).getBidBundleReport().getAllParticipantsBidBundleReports().keySet().toString());
		System.out.println("All of myAgent bid bundle reports " + GameLogDataStruct.getInstance().getGamesReports().get(gameId).getBidBundleReport().getSpecificParticipantAllBidBundleReport("myAgent").keySet().toString());
		System.out.println("Participant bid bundle Report my myAgent, query p_d:");
		String[] bidResults = GameLogDataStruct.getInstance().getGamesReports().get(gameId).getBidBundleReport().getSpecificParticipantAllBidBundleReport("myAgent").get(LogQueryType.p_d).get(LogBidBundleReportParams.bid);
		for (int i = 0; i < bidResults.length; i++)
		{
			System.out.println(bidResults[i]);
		}
		
		System.out.println("\n*************************  Retail Catalog Reports  ***************************");
		System.out.println("All particiants Retail Catalog reports " + GameLogDataStruct.getInstance().getGamesReports().get(gameId).getRetailCatalogReport().getAllParticipantsRetailCatalogReports().keySet().toString());
		System.out.println("All of myAgent Retail Catalog reports " + GameLogDataStruct.getInstance().getGamesReports().get(gameId).getRetailCatalogReport().getSpecificParticipantAllRetailCatalogReport("myAgent").keySet().toString());
		System.out.println("Participant Retail Catalog Report my myAgent, query p_d:");
		String[] salesProfitResults = GameLogDataStruct.getInstance().getGamesReports().get(gameId).getRetailCatalogReport().getSpecificParticipantAllRetailCatalogReport("myAgent").get(LogProduct.p_d).get(LogRetailCatalogReportParams.salesProfit);
		for (int i = 0; i < salesProfitResults.length; i++)
		{
			System.out.println(salesProfitResults[i]);
		}
		
		System.out.println("\n*************************  Slot Info Reports  ***************************");
		System.out.println("All particiants Slot Info reports " + GameLogDataStruct.getInstance().getGamesReports().get(gameId).getSlotInfoReport().getAllParticipantsSlotInfoReports().keySet().toString());
		System.out.println("All of myAgent Slot Info reports " + GameLogDataStruct.getInstance().getGamesReports().get(gameId).getSlotInfoReport().getSpecificParticipantAllSlotInfoReport("myAgent").keySet().toString());
		System.out.println("Participant Slot Info Report my myAgent:");
		String[] slotInfoResults = GameLogDataStruct.getInstance().getGamesReports().get(gameId).getSlotInfoReport().getSpecificParticipantAllSlotInfoReport("myAgent").get(LogSlotInfoReportParams.regularSlots);
		for (int i = 0; i < slotInfoResults.length; i++)
		{
			System.out.println(slotInfoResults[i]);
		}
		
		System.out.println("\n*************************  User Click Model Reports  ***************************");
		System.out.println("All particiants User Click Model Advertiser Effectreports " + GameLogDataStruct.getInstance().getGamesReports().get(gameId).getUserClickModelReport().getAllParticipantsUserClickModelAdvertiserEffectReports().keySet().toString());
		System.out.println("All of Dummy User Click Model Advertiser Effect reports " + GameLogDataStruct.getInstance().getGamesReports().get(gameId).getUserClickModelReport().getSpecificParticipantAllUserClickModelAdvertiserEffectReport("Dummy").keySet().toString());
		System.out.println("Participant Slot Info Report Dummy:");
		String[] userClickModelAdAffectResults = GameLogDataStruct.getInstance().getGamesReports().get(gameId).getUserClickModelReport().getSpecificParticipantAllUserClickModelAdvertiserEffectReport("Dummy").get(LogQueryType.p_d).get(LogUserClickModelReportParams.advertiserEffect);
		for (int i = 0; i < userClickModelAdAffectResults.length; i++)
		{
			System.out.println(userClickModelAdAffectResults[i]);
		}
		System.out.println("Continuation Probability for p_d:");
		String[] userClickModelContinuationProbResults = GameLogDataStruct.getInstance().getGamesReports().get(gameId).getUserClickModelReport().getSpecificUserClickModelContinuationProbabilityReport(LogQueryType.p_d).get(LogUserClickModelReportParams.continuationProbability);
		for (int i = 0; i < userClickModelContinuationProbResults.length; i++)
		{
			System.out.println(userClickModelContinuationProbResults[i]);
		}
		
		System.out.println("\n*************************  Reserve Info Reports  ***************************");
		System.out.println("All particiants Reserve Info reports " + GameLogDataStruct.getInstance().getGamesReports().get(gameId).getReserveInfoReportLog().getAllParticipantsReserveInfoReports().keySet().toString());
		System.out.println("All of myAgent Reserve Info reports " + GameLogDataStruct.getInstance().getGamesReports().get(gameId).getReserveInfoReportLog().getSpecificParticipantAllReserveInfoReport("users").keySet().toString());
		System.out.println("Participant Reserve Info Report users:");
		String[] reserveInfoResults = GameLogDataStruct.getInstance().getGamesReports().get(gameId).getReserveInfoReportLog().getSpecificParticipantAllReserveInfoReport("users").get(LogReserveInfoReportParams.regularReserve);
		for (int i = 0; i < reserveInfoResults.length; i++)
		{
			System.out.println(reserveInfoResults[i]);
		}
		
		System.out.println("\n*************************  Publisher Info Reports  ***************************");
		System.out.println("All particiants Publisher Info reports " + GameLogDataStruct.getInstance().getGamesReports().get(gameId).getPublisherInfoReportLog().getAllParticipantsPublisherInfoReports().keySet().toString());
		System.out.println("All of myAgent Publisher Info reports " + GameLogDataStruct.getInstance().getGamesReports().get(gameId).getPublisherInfoReportLog().getSpecificParticipantAllPublisherInfoReport("myAgent").keySet().toString());
		System.out.println("Participant Publisher Info Report my myAgent:");
		String[] publisherInfoResults = GameLogDataStruct.getInstance().getGamesReports().get(gameId).getPublisherInfoReportLog().getSpecificParticipantAllPublisherInfoReport("myAgent").get(LogPublisherInfoReportParams.squashingParameter);
		for (int i = 0; i < publisherInfoResults.length; i++)
		{
			System.out.println(publisherInfoResults[i]);
		}		
	}

}
