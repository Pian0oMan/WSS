public class Player {

	private int maxStrength;
	private int currentStrength;
	private int maxWater;
	private int currentWater;
	private int maxFood;
	private int currentFood;
	private int currentGold;

	private int row;
	private int col;
	private Square currentSquare;
	private Brain brain;

	public Player(Map map, int startRow, int startCol, String brainType, String visionType) {
		maxStrength = 100;
		currentStrength = maxStrength;
		maxWater = 100;
		currentWater = maxWater;
		maxFood = 100;
		currentFood = maxFood;
		currentGold = 0;

		row = startRow;
		col = startCol;
		currentSquare = map.getMapSquare(row, col);

		brain = new Brain(this, map, brainType, visionType);
	}

	public void move(Square toSquare, int newRow, int newCol) {
		TerrainType t = toSquare.getTerrain();
		currentStrength -= t.moveCost;
		currentWater -= t.waterCost;
		currentFood -= t.foodCost;

		currentSquare = toSquare;
		row = newRow;
		col = newCol;
	}

	public void rest() {
		currentStrength += 10;
		if (currentStrength > maxStrength) {
			currentStrength = maxStrength;
		}
	}

	public void collectItem(Item item) {
		if (item == null) {
			return;
		}
		if (!item.getRepeating()) {
			currentSquare.getListItem().remove(item);
		}
		// TODO: apply item effect once FoodBonus / WaterBonus / GoldBonus
		// expose value/effect getters on Item.
	}

	public void trade(Trader trader, int offer) {
		if (offer > currentGold) {
			return;
		}
		currentGold -= offer;
		// TODO: refine once Trader's is finalized — expected to return
		// an Item in exchange for the gold offer.
	}

	public void updateResources() {
		TerrainType t = currentSquare.getTerrain();
		currentStrength -= t.moveCost;
		currentWater -= t.waterCost;
		currentFood -= t.foodCost;
	}

	public int getRowPos() {
		return row;
	}

	public int getColPos() {
		return col;
	}

	public int getMaxStrength() { return maxStrength; }
	public int getCurrentStrength() { return currentStrength; }
	public int getMaxWater() { return maxWater; }
	public int getCurrentWater() { return currentWater; }
	public int getMaxFood() { return maxFood; }
	public int getCurrentFood() { return currentFood; }
	public int getCurrentGold() { return currentGold; }
	public Square getCurrentSquare() { return currentSquare; }
	public Brain getBrain() { return brain; }
}
