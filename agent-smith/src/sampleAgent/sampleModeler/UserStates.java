package sampleAgent.sampleModeler;

public enum UserStates {

	/*
	 * Enum of the states that a user can be in
	 */

	NS,IS,F0,F1,F2,TR;
	
	
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
						retVal = 0.10;
						break;
					case F0:
						retVal = 0.70;
						break;
					case F1:
						retVal = 0.20;
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
						retVal = 0.10;
						break;
					case F1:
						retVal = 0.70;
						break;
					case F2:
						retVal = 0.20;
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
						retVal = 0.10;
						break;
					case F2:
						retVal = 0.90;
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