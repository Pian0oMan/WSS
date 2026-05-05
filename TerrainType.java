
public enum TerrainType {
	PLAINS(2, 1, 1),
	MOUNTAIN(5,3,3),
	DESERT(3,5,4),
	SWAMP(4,2,2),
	FOREST(3,2,2);
	
	public final int moveCost;
	public final int waterCost;
	public final int foodCost;
		
	
	TerrainType(int moveCost, int waterCost, int foodCost){
		this.moveCost = moveCost;
		this.waterCost = waterCost;
		this.foodCost = foodCost;
	}

	public int[] getCosts()
	{
		return new int[] { moveCost, waterCost, foodCost };
	}
	// public int getMoveCost() { return moveCost; }
	// public int getWaterCost() { return waterCost; }
	// public int getFoodCost() { return foodCost; }
}
