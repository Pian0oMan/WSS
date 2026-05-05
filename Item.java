public abstract class Item {
	protected boolean repeating;


	public Item(boolean repeating){
		this.repeating = repeating;
	}
	
	public boolean isRepeating()
	{
		return repeating;
	}
	
	public abstract int getValue();
	public void collect(Player player) {}

}
