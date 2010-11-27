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
//		outLog.println(day + ": " + participantNames[sender] + "->" + participantNames[receiver] + "   BidBundle:");
		DecimalFormat twoPlaces = new DecimalFormat("0.00");
//		if (bb.size() != 0)
//			outLog.println("\t \t \t  ad \t     bid \t   limit \t Total Limit: " + twoPlaces.format(bb.getCampaignDailySpendLimit()));
		for (int i = 0; i < bb.size(); i++)
		{
			Query qentry = bb.getQuery(i);
//			outLog.println(QueryToString(qentry) + "\t \t" + AdToString(bb.getAd(qentry)) + "\t\t" + twoPlaces.format(bb.getBid(qentry)) + "\t\t"
//					+ twoPlaces.format(bb.getDailyLimit(qentry)));
		}
	}

	private void RCparse(RetailCatalog rc, int sender, int receiver)
	{
//		outLog.println(day + ": " + participantNames[sender] + "->" + participantNames[receiver] + "   RetailCatalog:");
		DecimalFormat twoPlaces = new DecimalFormat("0.00");
		if (rc.size() != 0)
//			outLog.println("\t product \tsales profit \t");
		for (Product p : rc.keys())
		{
//			outLog.println("\t " + ProductToString(p) + "\t \t" + twoPlaces.format(rc.getSalesProfit(p)));
		}
	}

	private void SIparse(SlotInfo si, int sender, int receiver)
	{
		DecimalFormat twoPlaces = new DecimalFormat("0.00");
//		outLog.println(day + ": " + participantNames[sender] + "->" + participantNames[receiver] + "   SlotInfo: p-slots " + si.getPromotedSlots() + ", r-slots: "
//				+ si.getPromotedSlots() + ", p-bonus: " + twoPlaces.format(si.getPromotedSlotBonus()));
	}

	private void UCMparse(UserClickModel ucm, int sender, int receiver)
	{
		DecimalFormat twoPlaces = new DecimalFormat("0.00");
//		outLog.println(day + ": " + participantNames[sender] + "->" + participantNames[receiver] + "   UserClickModel: (AdvertiserEffect)");
		String advs = "\t ";
		for (int a = 0; a < ucm.advertiserCount(); a++)
		{
			advs += ucm.advertiser(a) + "    \t";
		}
		advs += "Continuation Probability";
//		outLog.println(advs);

		for (int i = 0; i < ucm.queryCount(); i++)
		{
			String qstr = " ";
			for (int a = 0; a < ucm.advertiserCount(); a++)
			{
				qstr += twoPlaces.format(ucm.getAdvertiserEffect(i, a)) + "        ";
			}
			qstr += twoPlaces.format(ucm.getContinuationProbability(i));

//			outLog.println(QueryToString(ucm.query(i)) + qstr);
		}
	}

	private void RIparse(ReserveInfo ri, int sender, int receiver)
	{
		DecimalFormat twoPlaces = new DecimalFormat("0.00");
//		outLog.println(day + ": " + participantNames[sender] + "->" + participantNames[receiver] + "   ReserveInfo: p: " + twoPlaces.format(ri.getPromotedReserve()) + ", r: "
//				+ twoPlaces.format(ri.getRegularReserve()));
	}

	private void PIparse(PublisherInfo pi, int sender, int receiver)
	{
		DecimalFormat twoPlaces = new DecimalFormat("0.00");
//		outLog.println(day + ": " + participantNames[sender] + "->" + participantNames[receiver] + "   PublisherInfo: squashing parameter: "
//				+ twoPlaces.format(pi.getSquashingParameter()));
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
		} else if ((content instanceof BankStatus) || (content instanceof SimulationStatus) || (content instanceof StartInfo))
		{
			System.out.println(day + ": " + participantNames[sender] + "->" + participantNames[receiver] + "," + content.toString());
		} else
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
