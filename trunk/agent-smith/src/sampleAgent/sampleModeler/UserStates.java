package sampleAgent.sampleModeler;

public class UserStates {

	protected double[][] prob;
	protected Integer [] stateBins = {10000, 0, 0, 0, 0, 0};
	
	public UserStates(){
		int matSize = EnumUserState.values().length;
		this.prob = new double[matSize][matSize];
		setCell(0.10, EnumUserState.F0, EnumUserState.NS);
		setCell(0.70, EnumUserState.F0, EnumUserState.F0);
		setCell(0.20, EnumUserState.F0, EnumUserState.F1);
		setCell(0.00, EnumUserState.F0, EnumUserState.TR);
		
		setCell(0.10, EnumUserState.F1, EnumUserState.NS);
		setCell(0.70, EnumUserState.F1, EnumUserState.F1);
		setCell(0.20, EnumUserState.F1, EnumUserState.F2);
		setCell(0.00, EnumUserState.F1, EnumUserState.TR);
		
		setCell(0.10, EnumUserState.F2, EnumUserState.NS);
		setCell(0.90, EnumUserState.F2, EnumUserState.F2);
		setCell(0.00, EnumUserState.F2, EnumUserState.TR);
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
	
	public void setCell (double value, EnumUserState from, EnumUserState to){
		prob[from.value()][to.value()] = value;
	}
	
	public double getCell (EnumUserState from, EnumUserState to){
		return prob[from.value()][to.value()];
	}
	
	public double[][] getProbMatrix(){
		return this.prob.clone();
	}
	
}