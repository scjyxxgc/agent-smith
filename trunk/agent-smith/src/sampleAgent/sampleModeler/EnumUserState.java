package sampleAgent.sampleModeler;

public enum EnumUserState {
	/*
	 * Enum of the states that a user can be in
	 */

	NS(0), 
	IS(1), 
	F0(2), 
	F1(3),
	F2(4),
	TR(5);
	
	private int i;
	
	private EnumUserState (int ind){
		this.i = ind;
	}
	
	public int value(){
		return this.i;
	}
}
