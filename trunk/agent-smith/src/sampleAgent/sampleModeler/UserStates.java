package sampleAgent.sampleModeler;

public class UserStates {

	protected double[][] prob;
	protected Integer [] stateBins = {10000, 0, 0, 0, 0, 0};
	
	public UserStates(){
		int matSize = EnumUserState.values().length;
		this.prob = new double[matSize][matSize];
		
		setCell(EnumUserState.NS, EnumUserState.NS, 0.99);
		setCell(EnumUserState.NS, EnumUserState.IS, 0.01);
		setCell(EnumUserState.NS, EnumUserState.F0, 0.00);
		setCell(EnumUserState.NS, EnumUserState.F1, 0.00);
		setCell(EnumUserState.NS, EnumUserState.F2, 0.00);
		setCell(EnumUserState.NS, EnumUserState.TR, 0.00);
		
		setCell(EnumUserState.IS, EnumUserState.NS, 0.05);
		setCell(EnumUserState.IS, EnumUserState.IS, 0.20);
		setCell(EnumUserState.IS, EnumUserState.F0, 0.60);
		setCell(EnumUserState.IS, EnumUserState.F1, 0.10);
		setCell(EnumUserState.IS, EnumUserState.F2, 0.05);
		setCell(EnumUserState.IS, EnumUserState.TR, 0.00);
		
		setCell(EnumUserState.F0, EnumUserState.NS, 0.10);
		setCell(EnumUserState.F0, EnumUserState.IS, 0.00);
		setCell(EnumUserState.F0, EnumUserState.F0, 0.70);
		setCell(EnumUserState.F0, EnumUserState.F1, 0.20);
		setCell(EnumUserState.F0, EnumUserState.F2, 0.00);
		setCell(EnumUserState.F0, EnumUserState.TR, 0.00);
		
		setCell(EnumUserState.F1, EnumUserState.NS, 0.10);
		setCell(EnumUserState.F1, EnumUserState.IS, 0.00);
		setCell(EnumUserState.F1, EnumUserState.F0, 0.00);
		setCell(EnumUserState.F1, EnumUserState.F1, 0.70);
		setCell(EnumUserState.F1, EnumUserState.F2, 0.20);
		setCell(EnumUserState.F1, EnumUserState.TR, 0.00);	
		
		setCell(EnumUserState.F2, EnumUserState.NS, 0.10);
		setCell(EnumUserState.F2, EnumUserState.IS, 0.00);
		setCell(EnumUserState.F2, EnumUserState.F0, 0.00);
		setCell(EnumUserState.F2, EnumUserState.F1, 0.00);
		setCell(EnumUserState.F2, EnumUserState.F2, 0.90);
		setCell(EnumUserState.F2, EnumUserState.TR, 0.00);
		
		setCell(EnumUserState.TR, EnumUserState.NS, 0.80);
		setCell(EnumUserState.TR, EnumUserState.IS, 0.00);
		setCell(EnumUserState.TR, EnumUserState.F0, 0.00);
		setCell(EnumUserState.TR, EnumUserState.F1, 0.00);
		setCell(EnumUserState.TR, EnumUserState.F2, 0.00);
		setCell(EnumUserState.TR, EnumUserState.TR, 0.20);
	}
	
	public UserStates(double[][] prob) throws ArrayIndexOutOfBoundsException{
		int matSize = EnumUserState.values().length;
		if ((prob.length != matSize) || (prob[0].length != matSize)){
			throw new ArrayIndexOutOfBoundsException("the matrix that was given is not equal to the Enum size");
		}
		else{
			this.prob = prob;
		}
	}
	
	public void setCell (EnumUserState from, EnumUserState to, double value){
		prob[from.value()][to.value()] = value;
	}
	
	public double getCell (EnumUserState from, EnumUserState to){
		return prob[from.value()][to.value()];
	}
	
	public double[][] getProbMatrix(){
		return this.prob.clone();
	}
	
}