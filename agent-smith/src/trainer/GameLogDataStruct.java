package trainer;

import java.util.HashMap;
import java.util.Map;

public class GameLogDataStruct
{
	private Map<Integer, GameReports> gamesReports;

	private static GameLogDataStruct instance = null;

	private GameLogDataStruct()
	{
		gamesReports = new HashMap<Integer, GameReports>();
	}

	public static GameLogDataStruct getInstance()
	{
		if (instance == null)
		{
			instance = new GameLogDataStruct();
		}
		return instance;
	}
	
	/**
	 * @param gameId
	 */
	public void addNewGameReport(int gameId)
	{
		gamesReports.put(gameId, new GameReports(gameId));
	}
	
	/**
	 * @return the gamesReports
	 */
	public Map<Integer, GameReports> getGamesReports()
	{
		return gamesReports;
	}

	public class GameReports
	{
		int gameId;
		QueryReportLog queryReport;
		SalesReportLog salesReport;
		
		public GameReports(int gameId)
		{
			this.gameId = gameId;
		}
		
		public void createQueryReport()
		{
			queryReport = new QueryReportLog();
		}

		public QueryReportLog getQueryReport()
		{
			return queryReport;
		}
		
		public void createSalesReport()
		{
			salesReport = new SalesReportLog();
		}

		public SalesReportLog getSalesReport()
		{
			return salesReport;
		}
		
		/**
		 * @return the gameId
		 */
		public int getGameId()
		{
			return gameId;
		}
	}
}
