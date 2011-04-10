package sampleAgent.sampleModeler;

import static arch.AgentConstants.TAU_SIMDAYS;
import edu.umich.eecs.tac.props.Query;
import arch.AgentComponentQuery;

public class SmithBasicModelerQuery extends AgentComponentQuery {

	/* estimated */
	public Double 	[]	estCpc;
	public Double 	[]	estImpressions;
	public Integer	[]	estPositions;
	
    /* actuals */
	public Double	[]	cpc;
	public int 		[]	impressions;		

	/* initial defaults */
    protected static double CPC_INIT = 2.0;
    protected static double IMPR_INIT = 100.0;
    
    /* sales report */
    protected	Integer	[]	conversions;
    protected	Double 	[]	revenue;
    protected	Double	[]	estConversions;
    protected	Double 	[]	estRevenue;
    
    protected	Integer	[]	clicks;
    protected	Double	[]	estClicks;
    
    protected	Double		cumulativeClicksProbability;
    protected	Double		cumulativeConversionsProbability;
    protected	Double		estClicksProbability;
    protected	Double		estConversionsProbability;
    
	public SmithBasicModelerQuery(Query q) {
		super(q);
	
		estCpc 			= 	new Double[TAU_SIMDAYS+3];
		estImpressions	=	new Double[TAU_SIMDAYS+3];
		cpc 			= 	new Double[TAU_SIMDAYS+3];
		impressions		=	new int[TAU_SIMDAYS+3];
		conversions		=	new Integer[TAU_SIMDAYS+3];
		revenue			=	new Double[TAU_SIMDAYS+3];
		estConversions	=	new Double[TAU_SIMDAYS+3];
		estRevenue		=	new Double[TAU_SIMDAYS+3];
		clicks			=	new Integer[TAU_SIMDAYS+3];
		estClicks		=	new Double[TAU_SIMDAYS+3];
		estPositions	=	new	Integer[TAU_SIMDAYS+3];
		cumulativeClicksProbability			= 0.0;
		cumulativeConversionsProbability	= 0.0;
		estClicksProbability				= 0.0;
		estConversionsProbability			= 0.0;
		
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
	
	public Integer getConversions(int ind){
		return this.conversions[ind];
	}
	
	public Double getRevenue(int ind){
		return this.revenue[ind];
	}
	
	public void setConversions(int ind, int elem){
		this.conversions[ind] = elem;
	}
	
	public void setRevenue(int ind, double elem){
		this.revenue[ind] = elem;
	}
	
	public Double getEstConversions(int ind){
		return this.estConversions[ind];
	}
	
	public Double getEstRevenue(int ind){
		return this.estRevenue[ind];
	}
	
	public void setEstConversions(int ind, double elem){
		this.estConversions[ind] = elem;
	}
	
	public void setEstRevenue(int ind, double elem){
		this.estRevenue[ind] = elem;
	}
	
	public Integer getClicks(int ind){
		return this.clicks[ind];
	}
	
	public Double getEstClicks(int ind){
		return this.estClicks[ind];
	}
	
	public void setClicks(int ind, int elem){
		this.clicks[ind] = elem;
	}
	
	public void setEstClicks(int ind, double elem){
		this.estClicks[ind] = elem;
	}
	
	public void addToCumulativeClicksProbability(double ratio){
		this.cumulativeClicksProbability += ratio;
	}
	
	public void addToCumulativeConversionsProbability(double ratio){
		this.cumulativeConversionsProbability += ratio;
	}
	
	public double getCumulativeClicksProbability(){
		return this.cumulativeClicksProbability;
	}
	
	public double getCumulativeConversionsProbability(){
		return this.cumulativeConversionsProbability;
	}
	
	public void setEstClicksProbability(double value){
		this.estClicksProbability = value;
	}
	
	public void setEstConversionsProbability(double value){
		this.estConversionsProbability = value;
	}
	
	public double getEstClicksProbability(){
		return this.estClicksProbability;
	}
	
	public double getEstConversionsProbability(){
		return this.estConversionsProbability;
	}
}
