public class FoodBonus extends Item {
	
	private int amount;

	public FoodBonus(int amount, bool repeating)
	{
		super(repeating);
		this.amount = amount;
	}

	public int getAmount()
	{
		return amount;
	}

	public void applyToPlayer(Player player) //will be initialized later
	{
		player.increaseFood(amount);		
	}
}
