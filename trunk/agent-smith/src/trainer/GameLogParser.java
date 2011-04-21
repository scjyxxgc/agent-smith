package trainer;

import java.text.DecimalFormat;

import edu.umich.eecs.tac.props.Ad;
import edu.umich.eecs.tac.props.AdvertiserInfo;
import edu.umich.eecs.tac.props.BidBundle;
import edu.umich.eecs.tac.props.Product;
import edu.umich.eecs.tac.props.PublisherInfo;
import edu.umich.eecs.tac.props.Query;
import edu.umich.eecs.tac.props.QueryReport;
import edu.umich.eecs.tac.props.QueryType;
import edu.umich.eecs.tac.props.ReserveInfo;
import edu.umich.eecs.tac.props.RetailCatalog;
import edu.umich.eecs.tac.props.BankStatus;
import edu.umich.eecs.tac.props.SalesReport;
import edu.umich.eecs.tac.props.SlotInfo;
import edu.umich.eecs.tac.props.UserClickModel;
import edu.umich.eecs.tac.TACAAConstants;
import edu.umich.eecs.tac.Parser;
import se.sics.tasim.props.StartInfo;
import se.sics.tasim.props.SimulationStatus;
import se.sics.tasim.logtool.ParticipantInfo;
import se.sics.tasim.logtool.LogReader;
import se.sics.isl.transport.Transportable;
import trainer.Constants.LogProduct;
import trainer.Constants.LogQueryType;

/**
 * <code>TauParser</code> is a simple TAC AA parser.
 * <p>
 * <p/>
 * 
 * @author - Mariano Schain Modified by Liron Lasry
 * 
 * @see edu.umich.eecs.tac.Parser
 */
public class GameLogParser extends Parser
{
	private int day = 0;
	private String[] participantNames;
	private boolean[] is_Advertiser;
	private ParticipantInfo[] participants;
	private int simulationId;

	private GameLogDataStruct myGameLogDataStruct;

	public GameLogParser(LogReader reader)
	{
		super(reader);
		
		participants = reader.getParticipants();
		if (participants == null)
		{
			throw new IllegalStateException("no participants");
		}
		
		simulationId = reader.getSimulationID();

		myGameLogDataStruct = GameLogDataStruct.getInstance();
		myGameLogDataStruct.addNewGameReport(simulationId);
		myGameLogDataStruct.getGamesReports().get(simulationId).createQueryReport();
		myGameLogDataStruct.getGamesReports().get(simulationId).createSalesReport();
		myGameLogDataStruct.getGamesReports().get(simulationId).createBidBundleReport();
		myGameLogDataStruct.getGamesReports().get(simulationId).createRetailCatalogReport();
		myGameLogDataStruct.getGamesReports().get(simulationId).createSlotInfoReport();
		myGameLogDataStruct.getGamesReports().get(simulationId).createUserClickModelReport();
		myGameLogDataStruct.getGamesReports().get(simulationId).createReserveInfoReportLog();
		myGameLogDataStruct.getGamesReports().get(simulationId).createPublisherInfoReportLog();
		myGameLogDataStruct.getGamesReports().get(simulationId).createBankStatusLog();
		
		int agent;
		participantNames = new String[participants.length];
		is_Advertiser = new boolean[participants.length];
		for (int i = 0, n = participants.length; i < n; i++)
		{
			ParticipantInfo info = participants[i];
			agent = info.getIndex();
			participantNames[agent] = info.getName();
			if (info.getRole() == TACAAConstants.ADVERTISER)
			{
				is_Advertiser[agent] = true;
			} else
				is_Advertiser[agent] = false;
		}
	}

	private void QRparse(QueryReport qr, int sender, int receiver)
	{
		DecimalFormat twoPlaces = new DecimalFormat("0.00");
	
		for (int i = 0; i < qr.size(); i++)
		{
			Query qentry = qr.getQuery(i);	
			myGameLogDataStruct.getGamesReports().get(simulationId).getQueryReport().addParticipantQueryReport(participantNames[receiver], QueryToEnum(qentry), day, AdToString(qr.getAd(qentry)), qr.getPromotedImpressions(qentry)+"", qr.getRegularImpressions(qentry)+"", qr.getClicks(qentry)+"", twoPlaces.format(qr.getCPC(qentry)), twoPlaces.format(qr.getPosition(qentry)));
		}
	}

	private void SRparse(SalesReport sr, int sender, int receiver)
	{
		DecimalFormat twoPlaces = new DecimalFormat("0.00");

		for (int i = 0; i < sr.size(); i++)
		{
			Query qentry = sr.getQuery(i);
			myGameLogDataStruct.getGamesReports().get(simulationId).getSalesReport().addParticipantSalesReport(participantNames[receiver], QueryToEnum(qentry), day, sr.getConversions(qentry)+"", twoPlaces.format(sr.getRevenue(qentry)));
		}
	}

	private void BBparse(BidBundle bb, int sender, int receiver)
	{
		DecimalFormat twoPlaces = new DecimalFormat("0.00");

		for (int i = 0; i < bb.size(); i++)
		{
			Query qentry = bb.getQuery(i);
			myGameLogDataStruct.getGamesReports().get(simulationId).getBidBundleReport().addParticipantBidBundleReport(participantNames[sender], QueryToEnum(qentry), day, AdToString(bb.getAd(qentry)), twoPlaces.format(bb.getBid(qentry)), twoPlaces.format(bb.getDailyLimit(qentry)));
		}
	}

	private void RCparse(RetailCatalog rc, int sender, int receiver)
	{
		DecimalFormat twoPlaces = new DecimalFormat("0.00");

		for (Product p : rc.keys())
		{
			myGameLogDataStruct.getGamesReports().get(simulationId).getRetailCatalogReport().addParticipantRetailCatalogReport(participantNames[receiver], ProductToEnum(p), day, twoPlaces.format(rc.getSalesProfit(p)));
		}
	}

	private void SIparse(SlotInfo si, int sender, int receiver)
	{
		DecimalFormat twoPlaces = new DecimalFormat("0.00");
		myGameLogDataStruct.getGamesReports().get(simulationId).getSlotInfoReport().addParticipantSlotInfoReport(participantNames[receiver], day, si.getPromotedSlots()+"", si.getRegularSlots()+"", twoPlaces.format(si.getPromotedSlotBonus()));
	}

	private void UCMparse(UserClickModel ucm, int sender, int receiver)
	{
		DecimalFormat twoPlaces = new DecimalFormat("0.00");
		for (int a = 0; a < ucm.advertiserCount(); a++)
			for (int i = 0; i < ucm.queryCount(); i++)	
				myGameLogDataStruct.getGamesReports().get(simulationId).getUserClickModelReport().addParticipantUserClickModelAdvertiserEffectReport(ucm.advertiser(a), QueryToEnum(ucm.query(i)), day, twoPlaces.format(ucm.getAdvertiserEffect(i, a)));	

		for (int i = 0; i < ucm.queryCount(); i++)
			myGameLogDataStruct.getGamesReports().get(simulationId).getUserClickModelReport().addUserClickModelContinuationProbabilityReport(QueryToEnum(ucm.query(i)), day, ucm.getContinuationProbability(i)+"");
	}

	private void RIparse(ReserveInfo ri, int sender, int receiver)
	{
		DecimalFormat twoPlaces = new DecimalFormat("0.00");
		myGameLogDataStruct.getGamesReports().get(simulationId).getReserveInfoReportLog().addParticipantReserveInfoReport(participantNames[receiver], day, twoPlaces.format(ri.getPromotedReserve()), twoPlaces.format(ri.getRegularReserve()));
	}

	private void PIparse(PublisherInfo pi, int sender, int receiver)
	{
		DecimalFormat twoPlaces = new DecimalFormat("0.00");
		myGameLogDataStruct.getGamesReports().get(simulationId).getPublisherInfoReportLog().addParticipantPublisherInfoReport(participantNames[receiver], day, twoPlaces.format(pi.getSquashingParameter()));
	}
	
	private void BSparse(String bs, int sender, int receiver)
	{
		double bankStatus;
		try
		{
			bankStatus = Double.parseDouble(bs);
		} catch (Exception e)
		{
			bankStatus = 0;
		}
		
		myGameLogDataStruct.getGamesReports().get(simulationId).getBankStatusReportLog().addParticipantBankStatusReport(participantNames[receiver], day, bankStatus);
	}

	private void AIparse(AdvertiserInfo ai, int sender, int receiver)
	{
		DecimalFormat twoPlaces = new DecimalFormat("0.00");
//		outLog.println(day + ": " + participantNames[sender] + "->" + participantNames[receiver] + "   AdvertiserInfo: specialty: (" + ai.getManufacturerSpecialty() + ","
//				+ ai.getComponentSpecialty() + ")" + " bonus (" + ai.getManufacturerBonus() + "," + ai.getComponentBonus() + ")" + " capacity: " + ai.getDistributionCapacity()
//				+ " discounter: " + ai.getDistributionCapacityDiscounter() + " window: " + ai.getDistributionWindow() + " target effect: " + ai.getTargetEffect()
//				+ " focus effects: " + ai.getFocusEffects(QueryType.FOCUS_LEVEL_ZERO) + "," + ai.getFocusEffects(QueryType.FOCUS_LEVEL_ONE) + ","
//				+ ai.getFocusEffects(QueryType.FOCUS_LEVEL_TWO));
	}

	// -------------------------------------------------------------------
	// Callbacks from the parser.
	// Please see the class edu.umich.eecs.tac.Parser for more callback
	// methods.
	// -------------------------------------------------------------------

	/**
	 * Invoked when a message to a specific receiver is encountered in the log
	 * file.
	 * 
	 * @param sender
	 *            the sender of the message
	 * @param receiver
	 *            the receiver of the message
	 * @param content
	 *            the message content
	 */
	protected void message(int sender, int receiver, Transportable content)
	{

		if (content instanceof QueryReport)
		{
			QRparse((QueryReport) content, sender, receiver);
		} else if (content instanceof SalesReport)
		{
			SRparse((SalesReport) content, sender, receiver);
		} else if (content instanceof BidBundle)
		{
			BBparse((BidBundle) content, sender, receiver);
		} else if (content instanceof RetailCatalog)
		{
			RCparse((RetailCatalog) content, sender, receiver);
		} else if (content instanceof SlotInfo)
		{
			SIparse((SlotInfo) content, sender, receiver);
		} else if (content instanceof UserClickModel)
		{
			UCMparse((UserClickModel) content, sender, receiver);
		} else if (content instanceof ReserveInfo)
		{
			RIparse((ReserveInfo) content, sender, receiver);
		} else if (content instanceof PublisherInfo)
		{
			PIparse((PublisherInfo) content, sender, receiver);
		} else if (content instanceof AdvertiserInfo)
		{
			AIparse((AdvertiserInfo) content, sender, receiver);
		} else if ((content instanceof BankStatus))
		{		
			//System.out.println(day + ": " + participantNames[sender] + "->" + participantNames[receiver] + "," + content.toString());
			BSparse(content.toString(), sender, receiver);
		} 
		else if ((content instanceof SimulationStatus) || (content instanceof StartInfo))
		{		
			System.out.println(day + ": " + participantNames[sender] + "->" + participantNames[receiver] + "," + content.toString());
		}else
		{
			System.out.println(day + ": " + "*** " + participantNames[sender] + "->" + participantNames[receiver] + "," + "Unhndeled: " + content.getClass());
		}
	}

	protected void dataUpdated(int type, Transportable content)
	{
	}

	protected void nextDay(int date, long serverTime)
	{
		day = date;
		System.out.println("---> New Day: " + day);
	}

	protected void parseStopped()
	{
	}
	
	
////// ******** Helper Functions ********//////
	
	private String firstLetter(String s)
	{
		if (s == null)
			return "n";
		else
			return s.substring(0, 1);
	}

	private String ProductToString(Product p)
	{
		if (p == null)
			return "( N )";
		else
			return "(" + firstLetter(p.getManufacturer()) + "," + firstLetter(p.getComponent()) + ")";
	}

	private LogProduct ProductToEnum(Product p)
	{
		if (p == null)
			return LogProduct.Null;
		
		LogProduct[] theProducts = LogProduct.values();
		for (int i = 0; i < theProducts.length; i++)
		{
			if((firstLetter(p.getManufacturer()) + "_" + firstLetter(p.getComponent())).equalsIgnoreCase(theProducts[i].toString()))
				return theProducts[i];
		}
		
		return null;
	}
	
	private String AdToString(Ad ad)
	{
		if (ad == null)
			return "( N )";
		else if (ad.isGeneric())
			return "( G )";
		else
			return ProductToString(ad.getProduct());
	}

	private String QueryToString(Query q)
	{
		return "(" + firstLetter(q.getManufacturer()) + "," + firstLetter(q.getComponent()) + ")";
	}
	
	private LogQueryType QueryToEnum(Query q)
	{
		LogQueryType[] theQueries = LogQueryType.values();
		for (int i = 0; i < theQueries.length; i++)
		{
			if((firstLetter(q.getManufacturer()) + "_" + firstLetter(q.getComponent())).equalsIgnoreCase(theQueries[i].toString()))
				return theQueries[i];
		}
		
		return null;
	}
}
