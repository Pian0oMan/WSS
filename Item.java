public abstract class Item {
	protected boolean repeating;
	
	public Item(boolean repeating){
		this.repeating = repeating;
	}
	
	public boolean getRepeating()
	{
		return repeating;
	}
}
