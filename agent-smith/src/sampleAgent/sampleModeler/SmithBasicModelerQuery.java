package sampleAgent.sampleModeler;

import static arch.AgentConstants.TAU_SIMDAYS;

import java.util.ArrayList;

import edu.umich.eecs.tac.props.Query;
import arch.AgentComponentQuery;

public class SmithBasicModelerQuery extends AgentComponentQuery {

	/* estimated */
	public Double 	[]	estCpc;
	public Double 	[]	estImpressions;
	
    /* actuals */
	public Double	[]	cpc;
	public int 		[]	impressions;		

	/* initial defaults */
    protected static double CPC_INIT = 2.0;
    protected static double IMPR_INIT = 100.0;
    
    protected ArrayList<Double> gameBids;
	protected ArrayList<Double> gamePos;
	protected ArrayList<Double> gameImp;
	protected ArrayList<Double> gameCpc;
    
	public SmithBasicModelerQuery(Query q) {
		super(q);
	
		estCpc 			= 	new Double[TAU_SIMDAYS+3];
		estImpressions	=	new Double[TAU_SIMDAYS+3];
		cpc 			= 	new Double[TAU_SIMDAYS+3];
		impressions		=	new int[TAU_SIMDAYS+3];
		gameBids		=	new ArrayList<Double>();
		gamePos			=	new ArrayList<Double>();
		gameImp			=	new ArrayList<Double>();
		gameCpc			=	new ArrayList<Double>();
		
		nextDay(0);
	}
		
	
	
	/* prepare for the next day results 
	 * we initialize the estimates to default values
	 * @see arch.AgentComponentQuery#nextDay(int)
	 */
	public void nextDay(int day) {
		estCpc[day]=CPC_INIT;
		estImpressions[day]=IMPR_INIT;
	}
}
