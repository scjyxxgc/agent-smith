package trainer;

import java.util.HashMap;
import java.util.Map;

import edu.umich.eecs.tac.props.Query;

public class GameLogDataStruct {
	private Map<Integer, GameReports> gamesReports;
	private Map<Query, Double> lastBidHistory;

	private static GameLogDataStruct instance = null;

	private GameLogDataStruct() {
		gamesReports = new HashMap<Integer, GameReports>();
		lastBidHistory = new HashMap<Query, Double>();
	}

	public static GameLogDataStruct getInstance() {
		if (instance == null) {
			instance = new GameLogDataStruct();
		}
		return instance;
	}

	public void addLastBid(Query theQuery, double theBid) {
		lastBidHistory.put(theQuery, theBid);
	}

	public double getLastBid(Query theQuery) {
		if (null == lastBidHistory.get(theQuery)) {
			return 0.0;
		} else {
			return lastBidHistory.get(theQuery);
		}
	}

	/**
	 * @param gameId
	 */
	public void addNewGameReport(int gameId) {
		gamesReports.put(gameId, new GameReports(gameId));
	}

	/**
	 * @return the gamesReports
	 */
	public Map<Integer, GameReports> getGamesReports() {
		return gamesReports;
	}

	public class GameReports {
		int gameId;
		QueryReportLog queryReport;
		SalesReportLog salesReport;
		BidBundleReportLog bidBundleReport;
		RetailCatalogReportLog retailCatalogReport;
		SlotInfoReportLog slotInfoReport;
		UserClickModelReportLog userClickModelReport;
		ReserveInfoReportLog reserveInfoReport;
		PublisherInfoReportLog publisherInfoReport;
		BankStatusReportLog bankStatusReport;

		public GameReports(int gameId) {
			this.gameId = gameId;
		}

		public void createQueryReport() {
			queryReport = new QueryReportLog();
		}

		public QueryReportLog getQueryReport() {
			return queryReport;
		}

		public void createSalesReport() {
			salesReport = new SalesReportLog();
		}

		public SalesReportLog getSalesReport() {
			return salesReport;
		}

		public void createBidBundleReport() {
			bidBundleReport = new BidBundleReportLog();
		}

		public BidBundleReportLog getBidBundleReport() {
			return bidBundleReport;
		}

		public void createRetailCatalogReport() {
			retailCatalogReport = new RetailCatalogReportLog();
		}

		public RetailCatalogReportLog getRetailCatalogReport() {
			return retailCatalogReport;
		}

		public void createSlotInfoReport() {
			slotInfoReport = new SlotInfoReportLog();
		}

		public SlotInfoReportLog getSlotInfoReport() {
			return slotInfoReport;
		}

		public void createUserClickModelReport() {
			userClickModelReport = new UserClickModelReportLog();
		}

		public UserClickModelReportLog getUserClickModelReport() {
			return userClickModelReport;
		}

		public void createReserveInfoReportLog() {
			reserveInfoReport = new ReserveInfoReportLog();
		}

		public ReserveInfoReportLog getReserveInfoReportLog() {
			return reserveInfoReport;
		}

		public void createPublisherInfoReportLog() {
			publisherInfoReport = new PublisherInfoReportLog();
		}

		public PublisherInfoReportLog getPublisherInfoReportLog() {
			return publisherInfoReport;
		}

		public void createBankStatusLog() {
			bankStatusReport = new BankStatusReportLog();
		}

		public BankStatusReportLog getBankStatusReportLog() {
			return bankStatusReport;
		}

		/**
		 * @return the gameId
		 */
		public int getGameId() {
			return gameId;
		}
	}
}
