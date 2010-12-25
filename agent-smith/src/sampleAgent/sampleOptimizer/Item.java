package sampleAgent.sampleOptimizer;

import edu.umich.eecs.tac.props.Query;

public class Item
{
	private double bid = 0;
	private double weight = 0;
	private double value = 0;
	private Query query = null;
	private int index = 0;
	private int originalIndex = 0;

	public Item()
	{
	}

	public Item(Item item)
	{
		setBid(item.bid);
		setWeight(item.weight);
		setValue(item.value);
		setQuery(item.query);
		setIndex(item.index);
	}

	public Item(double theBid, double theWeight, double theValue, Query theQuery, int theIndex)
	{
		setBid(theBid);
		setWeight(theWeight);
		setValue(theValue);
		setQuery(theQuery);
		setIndex(theIndex);
	}
	
	public Item(double theBid, int theOriginalIndex, double theWeight, double theValue, Query theQuery)
	{
		setBid(theBid);
		setWeight(theWeight);
		setValue(theValue);
		setQuery(theQuery);
		setOriginalIndex(theOriginalIndex);
	}

	public Item(double theWeight, double theValue, Query theQuery, int theIndex)
	{
		setWeight(theWeight);
		setValue(theValue);
		setQuery(theQuery);
		setIndex(theIndex);
	}
	
	public Item(double theBid, double theWeight, double theValue)
	{
		setBid(theBid);
		setWeight(theWeight);
		setValue(theValue);
	}
	
	public Item(double theWeight, double theValue)
	{
		setWeight(theWeight);
		setValue(theValue);
	}

	public void setBid(double theBid)
	{
		bid = theBid;
	}

	public void setWeight(double theWeight)
	{
		weight = Math.max(theWeight, 0);
	}

	public void setValue(double theValue)
	{
		value = Math.max(theValue, 0);
	}

	public double getBid()
	{
		return bid;
	}

	public double getWeight()
	{
		return weight;
	}

	public double getValue()
	{
		return value;
	}
	
	/**
	 * @param query the query to set
	 * @param index the index to set
	 */
	public synchronized void setQueryAndIndex(Query query, int index)
	{
		this.query = query;
		this.index = index;
	}

	
	/**
	 * @return the query
	 */
	public synchronized Query getQuery()
	{
		return query;
	}

	/**
	 * @param query the query to set
	 */
	public synchronized void setQuery(Query query)
	{
		this.query = query;
	}

	/**
	 * @return the index
	 */
	public synchronized int getIndex()
	{
		return index;
	}

	/**
	 * @param index the index to set
	 */
	public synchronized void setIndex(int index)
	{
		this.index = index;
	}
	
	/**
	 * @return the originalIndex
	 */
	public synchronized int getOriginalIndex()
	{
		return originalIndex;
	}
	
	/**
	 * @param originalIndex the originalIndex to set
	 */
	public synchronized void setOriginalIndex(int originalIndex)
	{
		this.originalIndex = originalIndex;
	}

} // class