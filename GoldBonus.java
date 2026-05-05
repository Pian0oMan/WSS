public class GoldBonus extends Item {
	private int amount;

	public GoldBonus(int amount, boolean repeating)
	{
		super(repeating);
		this.amount = amount;
	}

	public int getAmount()
	{
		return amount;
	}

	public int getValue() { return amount * 3; }

	// public void addToPlayer(Player player)
	// {
	// 	player.increaseGold(int amount);
	// }
}
