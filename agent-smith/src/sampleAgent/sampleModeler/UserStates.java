package sampleAgent.sampleModeler;

public enum UserStates {

	/*
	 * Enum of the states that a user can be in
	 */

	NS,IS,F0,F1,F2,TR;
	
	/* F0-to-X probabilities */
	protected Double F0toNS = 0.10;
	protected Double F0toF0 = 0.70;
	protected Double F0toF1 = 0.20;
	protected static Double F0toTR = 0.0;
	
	/* F1-to-X probabilities */
	protected Double F1toNS = 0.10;
	protected Double F1toF1 = 0.70;
	protected Double F1toF2 = 0.20;
	protected static Double F1toTR = 0.0;
	
	/* F2-to-X probabilities */
	protected Double F2toNS = 0.10;
	protected Double F2toF2 = 0.90;
	protected static Double F2toTR = 0.0;
	
	protected Integer [] stateBins = {10000, 0, 0, 0, 0, 0};
	
	
	public static double getF0toTR() {
		return F0toTR;
	}

	public static void setF0toTR(double val) {
		F0toTR = val;
	}

	public static double getF1toTR() {
		return F1toTR;
	}

	public static void setF1toTR(double val) {
		F1toTR = val;
	}

	public static double getF2toTR() {
		return F2toTR;
	}

	public static void setF2toTR(double val) {
		F2toTR = val;
	}

	/*
	 * returns the probability of a user to transition between states under
	 * standard conditions from state 'from' to state 'to'
	 */
	public double getStandardProbability(UserStates from, UserStates to)
	{
		double retVal = 0.0;

		switch (from)
		{
			case NS:
				switch (to)
				{
					case NS:
						retVal = 0.99;
						break;
					case IS:
						retVal = 0.01;
						break;
					default:
						retVal = 0.0;
						break;
				}
				break;
			case IS:
				switch(to)
				{
					case NS:
						retVal = 0.05;
						break;
					case IS:
						retVal = 0.20;
						break;
					case F0:
						retVal = 0.60;
						break;
					case F1:
						retVal = 0.10;
						break;
					case F2:
						retVal = 0.05;
						break;
					default:
						retVal = 0.0;
						break;
				}
				break;
			case F0:
				switch(to)
				{
					case NS:
						retVal = F0toNS;
						break;
					case F0:
						retVal = F0toF0;
						break;
					case F1:
						retVal = F0toF1;
						break;
					case TR:
						retVal = F0toTR;
						break;
					default:
						retVal = 0.0;
						break;
				}
				break;
			case F1:
				switch(to)
				{
					case NS:
						retVal = F1toNS;
						break;
					case F1:
						retVal = F1toF1;
						break;
					case F2:
						retVal = F1toF2;
						break;
					case TR:
						retVal = F1toTR;
						break;
					default:
						retVal = 0.0;
						break;
				}
				break;
			case F2:
				switch(to)
				{
					case NS:
						retVal = F2toNS;
						break;
					case F2:
						retVal = F2toF2;
						break;
					case TR:
						retVal = F2toTR;
						break;
					default:
						retVal = 0.0;
						break;
				}
				break;
			case TR:
				switch(to)
				{
					case NS:
						retVal = 0.80;
						break;
					case TR:
						retVal = 0.20;
						break;
					default:
						retVal = 0.0;
						break;
				}
				break;
			default:
				retVal = 0.0;
				break;
		}

		return retVal;
	}

	/*
	 * returns the probability of a user to transition between states under
	 * burst conditions from state 'from' to state 'to'
	 */
	public double getBurstProbability(UserStates from, UserStates to)
	{
		double retVal = 0.0;

		if (from == UserStates.NS)
		{
			switch (to)
			{
				case NS:
					retVal = 0.80;
					break;
				case IS:
					retVal = 0.20;
					break;
				default:
					retVal = 0.0;
					break;
			}
		}
		else
		{
			retVal = getStandardProbability(from, to);
		}

		return retVal;
	}
}