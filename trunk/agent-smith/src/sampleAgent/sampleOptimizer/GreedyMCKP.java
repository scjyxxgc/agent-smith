package sampleAgent.sampleOptimizer;

import java.util.*;

import edu.umich.eecs.tac.props.Query;

public class GreedyMCKP
{
	protected Map<Query, List<Item>> allQueriesItemLists = new HashMap<Query, List<Item>>();
	protected Map<Query, Integer> lastIncrementalIndexForAllQueries = new HashMap<Query, Integer>();
	protected Map<Query, Integer> otimalBidIndexPerQuery = new HashMap<Query, Integer>();
	protected List<Item> allItemsListDiffSeries = new ArrayList<Item>();
	protected double capacity = 0;
	protected double solutionWeight = 0;
	protected boolean calculated = false;

	public GreedyMCKP(int theCapacity, Set<Query> querySet)
	{
		setCapacity(theCapacity);	
		for (Query query : querySet)
		{
			allQueriesItemLists.put(query, new ArrayList<Item>());
			lastIncrementalIndexForAllQueries.put(query, 0);
			otimalBidIndexPerQuery.put(query, 0);
		}
	}

	// calculate the solution of MCKP with greedy method and return optimal bid
	public Map<Query, Integer> calcSolution()
	{
		setInitialStateForCalculation();
		if (capacity > 0)
		{			
			//System.out.println("Step 1");
			sortAllListsItemsByWeight();
			//printMap(allQueriesItemLists);

			//System.out.println("Step 2");
			removeDominatedItemsFromAllLists();
			removeLPDominatedItemsFromAllLists();
			renumberItemsFromAllLists();
			//printMap(allQueriesItemLists);
			
			//System.out.println("Step 3");
			createIncrementalItemsDiffSeries();
			//printList(allItemsListDiffSeries);

			//System.out.println("Calculating Regular KP");
			calcRegularKP();
			
			setOptimalBidPerQuery();
			
			calculated = true;
		} // if()
		
		System.out.println("solutionWeight= " + solutionWeight);
	
		return otimalBidIndexPerQuery;	
	}

	/**
	 * 
	 */
	protected void setOptimalBidPerQuery()
	{ 
		for(Map.Entry<Query, Integer> queueList : lastIncrementalIndexForAllQueries.entrySet())
		{	
			otimalBidIndexPerQuery.put(queueList.getKey(), allQueriesItemLists.get(queueList.getKey()).get(queueList.getValue()).getOriginalIndex());
			System.out.println("HybridPenalizedGreedyMCKP: Last index for query " + queueList.getKey() + " is " + queueList.getValue() + ", Best bid is " + otimalBidIndexPerQuery.get(queueList.getKey()));		
		}
	}
	
	/**
	 * 
	 */
	protected void calcRegularKP()
	{ 
		double residualCapacity = capacity;
		for(Map.Entry<Query, List<Item>> queueList : allQueriesItemLists.entrySet())
		{
			residualCapacity = residualCapacity - queueList.getValue().get(0).getWeight();
			solutionWeight = solutionWeight + queueList.getValue().get(0).getWeight();
		}
		
		for (int i = 0; i < allItemsListDiffSeries.size(); i++)
		{
			if((allItemsListDiffSeries.get(i).getWeight()) <= residualCapacity)
			{
				solutionWeight = solutionWeight +  allItemsListDiffSeries.get(i).getWeight();
				residualCapacity = residualCapacity - allItemsListDiffSeries.get(i).getWeight();
				//System.out.println("Taking " + allItemsListDiffSeries.get(i).getQuery() + ", " + allItemsListDiffSeries.get(i).getValue() + ", " + allItemsListDiffSeries.get(i).getWeight() + ", Residual=" + residualCapacity);
				if(lastIncrementalIndexForAllQueries.get(allItemsListDiffSeries.get(i).getQuery()) < allItemsListDiffSeries.get(i).getIndex())
				{
					lastIncrementalIndexForAllQueries.put(allItemsListDiffSeries.get(i).getQuery(), allItemsListDiffSeries.get(i).getIndex());
					//System.out.println("Setting last index for query " + allItemsListDiffSeries.get(i).getQuery() + "To " + lastIncrementalIndexForAllQueries.get(allItemsListDiffSeries.get(i).getQuery()));
				}
			}
		}
	}

	/**
	 * 
	 */
	protected void createIncrementalItemsDiffSeries()
	{
		for(Map.Entry<Query, List<Item>> queueList : allQueriesItemLists.entrySet())
		{
			for (int i = 1; i < queueList.getValue().size(); i++)
			{
				allItemsListDiffSeries.add(new Item(queueList.getValue().get(i).getWeight()-queueList.getValue().get(i-1).getWeight(), queueList.getValue().get(i).getValue()-queueList.getValue().get(i-1).getValue(), queueList.getValue().get(i).getQuery(), queueList.getValue().get(i).getIndex()));
			}
		}
		
		Collections.sort(allItemsListDiffSeries, new Comparator<Item>() {
			public int compare(Item e1, Item e2)
			{
				if (e1.getValue()/e1.getWeight() < e2.getValue()/e2.getWeight())
					return 1;
				else if (e2.getValue()/e2.getWeight() < e1.getValue()/e1.getWeight())
					return -1;
				else
					return 0;
			}
		});
	}

	/**
	 * 
	 */
	protected void removeDominatedItemsFromAllLists()
	{
		for(Map.Entry<Query, List<Item>> queueList : allQueriesItemLists.entrySet())
		{
			List<Item> itemsToRemove = new ArrayList<Item>();
			for (int i = 0; i < queueList.getValue().size(); i++)
			{
				for (int j = i; j < queueList.getValue().size(); j++)
				{
					if (queueList.getValue().get(i).getValue() > queueList.getValue().get(j).getValue())
					{
						itemsToRemove.add(queueList.getValue().get(j));
					} else if (queueList.getValue().get(i).getWeight() == queueList.getValue().get(j).getWeight() && queueList.getValue().get(i).getValue() < queueList.getValue().get(j).getValue())
					{
						itemsToRemove.add(queueList.getValue().get(i));
					}
				}
			}
			queueList.getValue().removeAll(itemsToRemove);
			itemsToRemove.clear();
		}
	}
	
	/**
	 * 
	 */
	protected void renumberItemsFromAllLists()
	{
		for(Map.Entry<Query, List<Item>> queueList : allQueriesItemLists.entrySet())
		{	
			for (int i = 0; i < queueList.getValue().size(); i++)
			{
				queueList.getValue().get(i).setIndex(i);
			}
		}
	}

	/**
	 * 
	 */
	protected void removeLPDominatedItemsFromAllLists()
	{
		for(Map.Entry<Query, List<Item>> queueList : allQueriesItemLists.entrySet())
		{
			List<Item> itemsToRemove = new ArrayList<Item>();
			for (int i = 1; i < queueList.getValue().size() - 1; i++)
			{
				if (queueList.getValue().get(i - 1).getWeight() < queueList.getValue().get(i).getWeight() && queueList.getValue().get(i).getWeight() < queueList.getValue().get(i + 1).getWeight()
						&& queueList.getValue().get(i - 1).getValue() < queueList.getValue().get(i).getValue() && queueList.getValue().get(i).getValue() < queueList.getValue().get(i + 1).getValue()
						&& (((queueList.getValue().get(i+1).getValue() - queueList.getValue().get(i).getValue()) / (queueList.getValue().get(i+1).getWeight() - queueList.getValue().get(i).getWeight())) >= 
							((queueList.getValue().get(i).getValue() - queueList.getValue().get(i - 1).getValue()) / (queueList.getValue().get(i).getWeight() - queueList.getValue().get(i - 1).getWeight()))))
				{
					itemsToRemove.add(queueList.getValue().get(i));
				}
			}
			queueList.getValue().removeAll(itemsToRemove);
			itemsToRemove.clear();
		}
	}

	/**
	 * 
	 */
	protected void printList(List<Item> theItemList)
	{
		for (Item item : theItemList)
		{
			System.out.println(item.getQuery() + "  " + item.getValue() + "  " + item.getWeight() );
		}
		System.out.println();
	}
	
	/**
	 * 
	 */
	protected void printMap(Map<Query, List<Item>> theItemListMap)
	{
		for(Map.Entry<Query, List<Item>> queueList : theItemListMap.entrySet())
		{
			List<Item> theItemList = queueList.getValue();
			System.out.print(queueList.getKey() + ": ");
			for (Item item : theItemList)
			{
				System.out.print("(" + item.getValue() + "," + item.getWeight() + "), ");
			}
			System.out.println();
		}
		System.out.println();
	}

	/**
	 * 
	 */
	protected void sortAllListsItemsByWeight()
	{
		for(Map.Entry<Query, List<Item>> queueList : allQueriesItemLists.entrySet())
		{
			Collections.sort(queueList.getValue(), new Comparator<Item>() {
				public int compare(Item e1, Item e2)
				{
					if (e1.getWeight() > e2.getWeight())
						return 1;
					else if (e2.getWeight() > e1.getWeight())
						return -1;
					else
						return 0;
				}
			});
		}
	}

	// add an item to the item list
	public void add(Query theQuery, int index, double theBid, double theWeight, double theValue)
	{
		//System.out.println("The value added: " + theValue);
		allQueriesItemLists.get(theQuery).add(new Item(theBid, index, theWeight, theValue, theQuery));
	}

	public double getSolutionWeight()
	{
		return solutionWeight;
	}

	public boolean isCalculated()
	{
		return calculated;
	}

	public double getCapacity()
	{
		return capacity;
	}

	public void setCapacity(int theCapacity)
	{
		capacity = Math.max(theCapacity, 0);
	}

	// set the class in the state of starting the calculation:
	protected void setInitialStateForCalculation()
	{
		calculated = false;
		solutionWeight = 0;
		for(Map.Entry<Query, Integer> queueList : lastIncrementalIndexForAllQueries.entrySet())
		{
			queueList.setValue(0);
		}
	}

} // class