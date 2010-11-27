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
		BidBundleReportLog bidBundleReport;
		RetailCatalogReportLog retailCatalogReport;
		SlotInfoReportLog slotInfoReport;
		UserClickModelReportLog userClickModelReport;
		ReserveInfoReportLog reserveInfoReport;
		PublisherInfoReportLog publisherInfoReport;
		
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
		
		public void createBidBundleReport()
		{
			bidBundleReport = new BidBundleReportLog();
		}

		public BidBundleReportLog getBidBundleReport()
		{
			return bidBundleReport;
		}
		
		public void createRetailCatalogReport()
		{
			retailCatalogReport = new RetailCatalogReportLog();
		}

		public RetailCatalogReportLog getRetailCatalogReport()
		{
			return retailCatalogReport;
		}
		
		public void createSlotInfoReport()
		{
			slotInfoReport = new SlotInfoReportLog();
		}

		public SlotInfoReportLog getSlotInfoReport()
		{
			return slotInfoReport;
		}
		
		public void createUserClickModelReport()
		{
			userClickModelReport = new UserClickModelReportLog();
		}

		public UserClickModelReportLog getUserClickModelReport()
		{
			return userClickModelReport;
		}
		
		public void createReserveInfoReportLog()
		{
			reserveInfoReport = new ReserveInfoReportLog();
		}

		public ReserveInfoReportLog getReserveInfoReportLog()
		{
			return reserveInfoReport;
		}
		
		public void createPublisherInfoReportLog()
		{
			publisherInfoReport = new PublisherInfoReportLog();
		}

		public PublisherInfoReportLog getPublisherInfoReportLog()
		{
			return publisherInfoReport;
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
