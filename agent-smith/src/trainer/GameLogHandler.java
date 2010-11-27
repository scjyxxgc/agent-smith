package trainer;

import java.io.IOException;
import java.text.ParseException;

import se.sics.isl.util.IllegalConfigurationException;
import se.sics.tasim.logtool.LogHandler;
import se.sics.tasim.logtool.LogReader;
import trainer.Constants.LogQueryReportParams;
import trainer.Constants.LogQueryType;
import trainer.Constants.LogSalesReportParams;

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
		GameLogParser parser = new GameLogParser(reader);
		parser.start();
		parser.stop();

		System.out.println("Is game report Empty: " + GameLogDataStruct.getInstance().getGamesReports().isEmpty());
		System.out.println("Key Set: " + GameLogDataStruct.getInstance().getGamesReports().keySet().toString());
		System.out.println("Game ID " + GameLogDataStruct.getInstance().getGamesReports().get(218).getGameId());
		System.out.println("All particiants query reports " + GameLogDataStruct.getInstance().getGamesReports().get(218).getQueryReport().getAllParticipantsQueryReports().keySet().toString());
		System.out.println("All of myAgent query reports " + GameLogDataStruct.getInstance().getGamesReports().get(218).getQueryReport().getSpecificParticipantAllQueryReport("myAgent").keySet().toString());
		System.out.println("Participant Query Report my myAgent, query p_d:");
		String[] cpcREsults = GameLogDataStruct.getInstance().getGamesReports().get(218).getQueryReport().getSpecificParticipantAllQueryReport("myAgent").get(LogQueryType.p_d).get(LogQueryReportParams.cpc);
		for (int i = 0; i < cpcREsults.length; i++)
		{
			System.out.println(cpcREsults[i]);
		}
		
		System.out.println("*******************************************************************");
		System.out.println("All particiants sales reports " + GameLogDataStruct.getInstance().getGamesReports().get(218).getSalesReport().getAllParticipantsSalesReports().keySet().toString());
		System.out.println("All of myAgent slaes reports " + GameLogDataStruct.getInstance().getGamesReports().get(218).getSalesReport().getSpecificParticipantAllSalesReport("myAgent").keySet().toString());
		System.out.println("Participant sales Report my myAgent, query p_d:");
		String[] impressionsResults = GameLogDataStruct.getInstance().getGamesReports().get(218).getSalesReport().getSpecificParticipantAllSalesReport("myAgent").get(LogQueryType.p_d).get(LogSalesReportParams.impressions);
		for (int i = 0; i < impressionsResults.length; i++)
		{
			System.out.println(impressionsResults[i]);
		}
	}

}
