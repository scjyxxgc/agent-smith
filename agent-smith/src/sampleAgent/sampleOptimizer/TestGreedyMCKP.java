package sampleAgent.sampleOptimizer;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import edu.umich.eecs.tac.props.Query;

public class TestGreedyMCKP
{

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		Query query1 = new Query("1", "1");
		Query query2 = new Query("2", "2");
		//Query query3 = new Query("3", "3");
		
		Set<Query> querySet = new HashSet<Query>();
		querySet.add(query1);
		querySet.add(query2);
		//querySet.add(query3);
		
		HybridPenalizedGreedyMCKP gMkcp = new HybridPenalizedGreedyMCKP(5, querySet);	
		//GreedyMCKP gMkcp = new GreedyMCKP(100, querySet);
		gMkcp.add(query1, 0, 0.1, 6, 70);
		gMkcp.add(query1, 1, 0.2, 5, 75);
		gMkcp.add(query1, 2, 0.3, 3, 60);
		gMkcp.add(query1, 3, 0.4, 2, 50);
		gMkcp.add(query1, 4, 0.5, 1, 30);
		gMkcp.add(query2, 0, 0.1, 6, 76);
		gMkcp.add(query2, 1, 0.2, 5, 75);
		gMkcp.add(query2, 2, 0.3, 3, 60);
		gMkcp.add(query2, 3, 0.4, 2, 40);
		gMkcp.add(query2, 4, 0.5, 1, 30);
		
		
		/*gMkcp.add(query1, 0, 0.1, 7, 25);
		gMkcp.add(query1, 1, 0.2, 12, 55);
		gMkcp.add(query1, 2, 0.3, 26, 76);
		gMkcp.add(query1, 3, 0.4, 65, 89);
		gMkcp.add(query2, 0, 0.1, 12, 47);
		gMkcp.add(query2, 1, 0.2, 36, 95);
		gMkcp.add(query3, 0, 0.1, 8, 35);
		gMkcp.add(query3, 1, 0.2, 24, 59);
		gMkcp.add(query3, 2, 0.3, 40, 71);
		gMkcp.add(query3, 3, 0.4, 85, 80);*/
		
		Map<Query, Integer> results = gMkcp.calcSolution();
		
		System.out.println("Optimal Bid 1: " + results.get(query1));
		System.out.println("Optimal Bid 2: " + results.get(query2));

	}
}