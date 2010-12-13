package sampleAgent.sampleModeler;

import static arch.AgentConstants.TAU_SIMDAYS;
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
    
    /* sales report */
    protected	int		[]	conversions;
    protected	double 	[]	revenue;
    public		int		[]	estConversions;
    public		double 	[]	estRevenue;
    
	public SmithBasicModelerQuery(Query q) {
		super(q);
	
		estCpc = 			new Double[TAU_SIMDAYS+3];
		estImpressions =	new Double[TAU_SIMDAYS+3];
		cpc = 				new Double[TAU_SIMDAYS+3];
		impressions = 		new int[TAU_SIMDAYS+3];
		conversions = 		new int[TAU_SIMDAYS+3];
		revenue = 			new double[TAU_SIMDAYS+3];
		estConversions = 	new int[TAU_SIMDAYS+3];
		estRevenue = 		new double[TAU_SIMDAYS+3];
		
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
	
	public int[] getConversions(){
		return this.conversions;
	}
	
	public double[] getRevenue(){
		return this.revenue;
	}
	
	public void setConversions(int ind, int elem){
		this.conversions[ind] = elem;
	}
	
	public void setRevenue(int ind, double elem){
		this.revenue[ind] = elem;
	}
	
	public int[] getEstConversions(){
		return this.estConversions;
	}
	
	public double[] getEstRevenue(){
		return this.estRevenue;
	}
	
	public void setEstConversions(int ind, int elem){
		this.estConversions[ind] = elem;
	}
	
	public void setEstRevenue(int ind, double elem){
		this.estRevenue[ind] = elem;
	}
}
