package sampleAgent.sampleEstimator;

import static arch.AgentConstants.TAU_SIMDAYS;
import edu.umich.eecs.tac.props.Query;
import arch.AgentComponentQuery;

public class MyBasicEstimatorQuery extends AgentComponentQuery {


	/* estimated */
	public Double 	[]	estSales;
	public Double 	[]	estConvRate;
	public Double 	[]	estClickRate;

    /* actuals */
	public int 		[]	sales;
	public Double 	[]	convRate;
	public Double	[]	clickRate;
	public int 		[]	clicks;
	public Double   [] 	profitPerUnitSold;
	
	/* initial defaults */
    protected static double SALES_INIT = 10.0;
    protected static double CONVR_INIT = 0.3;
    protected static double CLKR_INIT = 0.3;

	
	public MyBasicEstimatorQuery(Query q) {
		super(q);
		estSales =	 	new Double[TAU_SIMDAYS+3];
		estConvRate =	new Double[TAU_SIMDAYS+3];
		estClickRate =  new Double[TAU_SIMDAYS+3];
		sales = 		new int[TAU_SIMDAYS+3];
		convRate = 		new Double[TAU_SIMDAYS+3];
		clicks = 		new int[TAU_SIMDAYS+3];
		clickRate = 	new Double[TAU_SIMDAYS+3];
		profitPerUnitSold =	new Double[TAU_SIMDAYS+3];
				
		nextDay(0);
	}
	
	/* prepare for the next day results 
	 * we initialize the estimates to default values
	 * @see arch.AgentComponentQuery#nextDay(int)
	 */
	public void nextDay(int day) {
		estSales[day]=SALES_INIT;
		estConvRate[day]=CONVR_INIT;
		estClickRate[day] = CLKR_INIT;
	}
	
}
