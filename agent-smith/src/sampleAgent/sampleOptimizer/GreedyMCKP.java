package sampleAgent.sampleOptimizer;

import java.util.*;

public class GreedyMCKP
{
	protected List<Item> itemList = new ArrayList<Item>();
	protected List<Item> backupItemList = new ArrayList<Item>();
	protected List<Item> itemListDiffSeries = new ArrayList<Item>();
	protected double maxWeight = 0;
	protected double solutionWeight = 0;
	protected int lastIncremantalItemIndex = 0;
	protected boolean calculated = false;

	public GreedyMCKP()
	{
	}

	public GreedyMCKP(int theMaxWeight)
	{
		setMaxWeight(theMaxWeight);
	}

	public GreedyMCKP(List<Item> theItemList)
	{
		setItemList(theItemList);
	}

	public GreedyMCKP(List<Item> theItemList, int theMaxWeight)
	{
		setItemList(theItemList);
		setMaxWeight(theMaxWeight);
	}

	// calculate the solution of MCKP with greedy method and return optimal bid
	public double calcSolution()
	{
		int n = itemList.size();
		backupItemList.addAll(itemList);

		setInitialStateForCalculation();
		if (n > 0 && maxWeight > 0)
		{
			//System.out.println("Step 1");
			sortItemsByWeight();
			//printList(itemList);

			//System.out.println("Step 2");
			removeDominatedItems();
			removeLPDominatedItems();
			//printList(itemList);
			
			//System.out.println("Step 3");
			createIncrementalItemsDiffSeries();
			//printList(itemListDiffSeries);

			//System.out.println("Calculating Regular KP");
			calcRegularKP();
			//printList(itemListDiffSeries);
			
			calculated = true;
		} // if()
		
		System.out.println("lastIncremantalItemIndex= " + lastIncremantalItemIndex);
		System.out.println("solutionWeight= " + solutionWeight);
		
		return backupItemList.get(lastIncremantalItemIndex).getBid();
	}

	/**
	 * 
	 */
	protected void calcRegularKP()
	{
		for (int i = 0; i < itemListDiffSeries.size(); i++)
		{
			while ((solutionWeight + itemListDiffSeries.get(i).getWeight()) <= maxWeight)
			{
				solutionWeight += itemListDiffSeries.get(i).getWeight();
				lastIncremantalItemIndex = i;
			}
		}
	}

	/**
	 * 
	 */
	protected void createIncrementalItemsDiffSeries()
	{
		for (int i = 0; i < itemList.size(); i++)
		{
			if(i==0)
			{
				itemListDiffSeries.add(new Item(itemList.get(i).getWeight(), itemList.get(i).getValue()));
			}
			else
			{
				itemListDiffSeries.add(new Item(itemList.get(i).getWeight()-itemList.get(i-1).getWeight(), itemList.get(i).getValue()-itemList.get(i-1).getValue()));
			}
		}
		
		Collections.sort(itemListDiffSeries, new Comparator<Item>() {
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
	protected void removeDominatedItems()
	{
		List<Item> itemsToRemove = new ArrayList<Item>();
		for (int i = 0; i < itemList.size(); i++)
		{
			for (int j = i; j < itemList.size(); j++)
			{
				if (itemList.get(i).getValue() > itemList.get(j).getValue())
				{
					itemsToRemove.add(itemList.get(j));
				} else if (itemList.get(i).getWeight() == itemList.get(j).getWeight() && itemList.get(i).getValue() < itemList.get(j).getValue())
				{
					itemsToRemove.add(itemList.get(i));
				}
			}
		}
		itemList.removeAll(itemsToRemove);
		itemsToRemove.clear();
	}

	/**
	 * 
	 */
	protected void removeLPDominatedItems()
	{
		List<Item> itemsToRemove = new ArrayList<Item>();
		for (int i = 1; i < itemList.size() - 1; i++)
		{
			if (itemList.get(i - 1).getWeight() < itemList.get(i).getWeight() && itemList.get(i).getWeight() < itemList.get(i + 1).getWeight()
					&& itemList.get(i - 1).getValue() < itemList.get(i).getValue() && itemList.get(i).getValue() < itemList.get(i + 1).getValue()
					&& (((itemList.get(i+1).getValue() - itemList.get(i).getValue()) / (itemList.get(i+1).getWeight() - itemList.get(i).getWeight())) >= 
						((itemList.get(i).getValue() - itemList.get(i - 1).getValue()) / (itemList.get(i).getWeight() - itemList.get(i - 1).getWeight()))))
			{
				itemsToRemove.add(itemList.get(i));
			}
		}
		itemList.removeAll(itemsToRemove);
		itemsToRemove.clear();
	}

	/**
	 * 
	 */
	protected void printList(List<Item> theItemList)
	{
		for (Item item : theItemList)
		{
			System.out.println(item.getBid() + "  " + item.getWeight() + "  " + item.getValue());
		}
		System.out.println();
	}

	/**
	 * 
	 */
	protected void sortItemsByWeight()
	{
		Collections.sort(itemList, new Comparator<Item>() {
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

	// add an item to the item list
	public void add(double theBid, double theWeight, double theValue)
	{
		itemList.add(new Item(theBid, theWeight, theValue));
		setInitialStateForCalculation();
	}

	// remove an item from the item list
	public void remove(double theBid)
	{
		for (Iterator<Item> it = itemList.iterator(); it.hasNext();)
		{
			if (theBid == it.next().getBid())
			{
				it.remove();
			}
		}
		setInitialStateForCalculation();
	}

	// remove all items from the item list
	public void removeAllItems()
	{
		itemList.clear();
		setInitialStateForCalculation();
	}

	public double getSolutionWeight()
	{
		return solutionWeight;
	}
	
	public int getLastIncremantalItemIndex()
	{
		return lastIncremantalItemIndex;
	}

	public boolean isCalculated()
	{
		return calculated;
	}

	public double getMaxWeight()
	{
		return maxWeight;
	}

	public void setMaxWeight(int theMaxWeight)
	{
		maxWeight = Math.max(theMaxWeight, 0);
	}

	public void setItemList(List<Item> theItemList)
	{
		if (theItemList != null)
		{
			itemList = theItemList;
		}
	}

	// set the class in the state of starting the calculation:
	protected void setInitialStateForCalculation()
	{
		calculated = false;
		solutionWeight = 0;
		lastIncremantalItemIndex = 0;
	}

} // class