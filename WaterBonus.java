public class WaterBonus extends Item {
	private int amount;

	public WaterBonus(int amount, boolean repeating)
	{
		super(repeating);
		this.amount = amount;
	}

	public int getAmount()
	{
		return amount;
	}

	public void addToPlayer(Player player)
	{
		player.increaseWater(amount);
	}
}
