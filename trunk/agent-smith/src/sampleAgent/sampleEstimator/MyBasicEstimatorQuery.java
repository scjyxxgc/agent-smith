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
	
	/* computations arrays for DES model */
	public Double 	[]	b1;
	public Double 	[]	b2;
	public Double 	[]	b3;

	
	/* initial defaults */
    protected static double SALES_INIT = 50.0;
    protected static double CONVR_INIT = 50.3;
    protected static double CLKR_INIT = 50.3;

	
	public MyBasicEstimatorQuery(Query q) 
	{
		super(q);
		estSales =	 	new Double[TAU_SIMDAYS];
		estConvRate =	new Double[TAU_SIMDAYS];
		estClickRate =  new Double[TAU_SIMDAYS];
		
		sales = 		new int[TAU_SIMDAYS];
		convRate = 		new Double[TAU_SIMDAYS];
		clicks = 		new int[TAU_SIMDAYS];
		clickRate = 	new Double[TAU_SIMDAYS];
		profitPerUnitSold =	new Double[TAU_SIMDAYS];
		
		b1= new Double[TAU_SIMDAYS];
		b2= new Double[TAU_SIMDAYS];
		b3= new Double[TAU_SIMDAYS];

				
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
