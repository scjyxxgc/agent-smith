package sampleAgent.sampleOptimizer;

public class Item
{
	protected double bid = 0;
	protected double weight = 0;
	protected double value = 0;

	public Item()
	{
	}

	public Item(Item item)
	{
		setBid(item.bid);
		setWeight(item.weight);
		setValue(item.value);
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

} // class