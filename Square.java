import java.util.ArrayList;
import java.util.List;

public class Square {
	private TerrainType terrain;
	private List<Item> items;
	
	public Square(TerrainType terrain) {
		this.terrain = terrain;
		items = new ArrayList<>();
	}
	
	public TerrainType getTerrain()
	{
		return terrain;
	}
	
	public List<Item> getListItem()
	{
		return items;
	}


	
	public void addItem(Item item)
	{
		items.add(item);
	}
} 
