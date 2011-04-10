package sampleAgent.sampleModeler;

import java.util.*;
import java.util.Map.Entry;

import org.apache.commons.math.fraction.Fraction;
import org.apache.commons.math.fraction.FractionConversionException;

public class PositionAnalyzer {

	// inputs
	private HashMap<Double, String> advertisersAvgPos;
	private int smithImpressions;
	private int maxImpressions;

	// locals
	private HashMap<String, Integer> limitOrder = new HashMap<String, Integer>();
	private HashMap<String, Equation> equations = new HashMap<String, Equation>();
	private HashMap<String, Equation> eqSols = new HashMap<String, Equation>();
	private int numOfAttempts = 0;

	// outputs
	public HashMap<Integer, String> advertisersByRank = null;
	public HashMap<String, Integer> impressionsByAdv = null;
	public int totalImpressions = 0;

	// constructor
	public PositionAnalyzer(HashMap<Double, String> allAvgPositions,
			int smithImpressions, int maxImpressions) {
		// init inputs from arguments
		this.advertisersAvgPos = new HashMap<Double, String>(allAvgPositions);
		this.smithImpressions = smithImpressions;
		this.maxImpressions = maxImpressions;

		// init outputs
		this.advertisersByRank = new HashMap<Integer, String>();
		this.impressionsByAdv = new HashMap<String, Integer>();
		this.totalImpressions = 0;

		long start = (new Date()).getTime();
		long now = start;
		while ((eqSols.size() == 0) && ((now - start) <= 300)) {
			numOfAttempts++;
			rankNode(1);
			now = (new Date()).getTime();
		}
		// TODO: score eqSols and select the best
	}

	private void rankNode(int level) {
		if (level > advertisersAvgPos.size()) {
			// valid solution found
			// TODO: something with the solution we just found
		} else {
			for (Entry<Double, String> entry : advertisersAvgPos.entrySet()) {
				int rank = (int) Math.floor(entry.getKey()) + 1;
				if (rank >= level) {
					advertisersByRank.put(level, entry.getValue());
					limitOrderNode(level, entry.getKey());
				}
			}
		}
	}

	private void limitOrderNode(int level, double avgPosition) {
		int lim = level + 1 - (int) Math.floor(avgPosition);
		int limUpperBound = (int) Math.floor(avgPosition) + 1; // roof(avgPosition)

		// traverse over all possible values of limitOrder
		for (int value = lim; value <= limUpperBound; lim++) {
			for (int i = 1; i < level; i++) {
				int tmpLimOrd = limitOrder.get(advertisersByRank.get(i));
				if (tmpLimOrd >= value) {
					// if limitOrder of this advertiser is larger/equal to value
					// increment it by 1
					limitOrder.put(advertisersByRank.get(i), tmpLimOrd + 1);
				}
			}
			limitOrder.put(advertisersByRank.get(level), value);
			gcdNode(level);
		}
	}

	private void gcdNode(int level) {
		String advLevel = advertisersByRank.get(level);
		Equation eq = null;
		if (equations.containsKey(advLevel)) {
			eq = equations.get(advLevel);
		} else {
			eq = new Equation();
			for (double avgPos : advertisersAvgPos.keySet()) {
				if (advertisersAvgPos.get(avgPos).equals(advLevel)) {
					Fraction frac = null;
					try {
						frac = new Fraction(avgPos);
						eq.setNumerator(frac.getNumerator());
						eq.setDenominator(frac.getDenominator());
					} catch (FractionConversionException e) {
						eq.setNumerator(-1);
						eq.setDenominator(-1);
						System.out.println("PositionAnalyzer.gcdNode - error:");
						System.out.println("Can't convert avgPos = " + avgPos);
					}
				}
			}
		}

		if (level < 6) {
			eq.setStart(1);
		} else {
			for (String advCompt : limitOrder.keySet()) {
				if (limitOrder.get(advCompt) == (level - 5)) {
					eq.setStart(equations.get(advCompt).getEnd());
				}
			}
		}

		if (limitOrder.get(advLevel) == 1) {
			eq.setGcd(0);
			eq.setEnd(0);
			rankNode(level + 1);
		} else {
			// see if RHS of eq has any unknowns
			boolean hasUnknowns = (eq.getStart() < 1);
			Iterator<Integer> itr = advertisersByRank.keySet().iterator();
			String advCompt = null;
			int rankCompt = 0;
			int sum = 0;
			while ((!hasUnknowns) && (itr.hasNext())) {
				rankCompt = itr.next();
				advCompt = advertisersByRank.get(rankCompt);
				if ((rankCompt < level)
						&& (limitOrder.get(advCompt) < limitOrder.get(advLevel))) {
					if (equations.containsKey(advCompt)) {
						hasUnknowns &= (equations.get(advCompt).getEnd() < 1);
						sum += Math.max(0,
								(equations.get(advCompt).getEnd() - Math.abs(eq
										.getStart())));
					} else {
						// advCompt doesn't have any equation params, so all
						// it's params are unknown
						hasUnknowns = true;
					}
				}
			}

			if (hasUnknowns == false) {
				// no unknowns in RHS, so solve for gcd of advertiser(level)
				int gcd = sum
						/ (eq.getNumerator() - eq.getDenominator()
								* (level - limitOrder.get(advLevel) + 1));
				eq.setGcd(gcd);
				eq.setEnd(eq.getStart() + eq.getDenominator() * eq.getGcd());
				// TODO: check if eq.getEnd() is feasible
				{
					rankNode(level + 1);
				}
			} else {
				// TODO: determine feasible values of eq.getGcd()
				for (int value = 0; value < 5 * numOfAttempts; value++) {
					eq.setGcd(value);
					eq.setEnd(eq.getStart() + eq.getDenominator() * eq.getGcd());
					equations.put(advLevel, eq);
					// TODO: try to solve missing values in equations
					// TODO: check if eq.getEnd() is feasible and that equations
					// is consistent
					{
						rankNode(level + 1);
					}
				}
			}
		}
	}

	private class Equation {
		int gcd = 0;
		int numerator = 0;
		int denominator = 0;
		int start = 0;
		int end = 0;

		int getGcd() {
			return gcd;
		}

		int getNumerator() {
			return numerator;
		}

		int getDenominator() {
			return denominator;
		}

		int getStart() {
			return start;
		}

		int getEnd() {
			return end;
		}

		void setGcd(int gcd) {
			this.gcd = gcd;
		}

		void setNumerator(int numerator) {
			this.numerator = numerator;
		}

		void setDenominator(int denominator) {
			this.denominator = denominator;
		}

		void setStart(int start) {
			this.start = start;
		}

		void setEnd(int end) {
			this.end = end;
		}
	}
}
