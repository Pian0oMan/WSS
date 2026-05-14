// Represents the player character, tracking position, resources, and delegating AI decisions to Brain
public class Player {

	// All resource fields are private so nothing outside this class can set them to invalid values
	private int maxStrength;
	private int currentStrength;
	private int maxWater;
	private int currentWater;
	private int maxFood;
	private int currentFood;
	private int currentGold;

	// Position and map state
	private int row;
	private int col;
	private Square currentSquare;
	private Brain brain;

	// Start the player at full resources at the given map position, with the chosen brain and vision types
	public Player(gameMap map, int startRow, int startCol, String brainType, String visionType) {
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

	// Deduct terrain costs when the player steps onto a new square
	public void move(Square toSquare, int newRow, int newCol) {
		TerrainType t = toSquare.getTerrain();
		currentStrength -= t.moveCost;
		currentWater -= t.waterCost;
		currentFood -= t.foodCost;

		currentSquare = toSquare;
		row = newRow;
		col = newCol;
	}

	// Resting recovers 10 strength but never goes above the max
	public void rest() {
		currentStrength += 10;
		if (currentStrength > maxStrength) {
			currentStrength = maxStrength;
		}
	}

	// Applies the item's effect to the correct resource, capping at the max so we never overflow
	public void collectItem(Item item) {
		if (item == null) {
			return;
		}
		// One-time items are removed from the square after pickup
		if (!item.isRepeating()) {
			currentSquare.getListItem().remove(item);
		}
		if (item instanceof FoodBonus) {
			currentFood = Math.min(currentFood + ((FoodBonus) item).getAmount(), maxFood);
		} else if (item instanceof WaterBonus) {
			currentWater = Math.min(currentWater + ((WaterBonus) item).getAmount(), maxWater);
		} else if (item instanceof GoldBonus) {
			currentGold += ((GoldBonus) item).getAmount();
		}
	}

	// Validates the player can afford the offer before passing it to the trader, then applies results if accepted
	public void trade(Trader trader, TradeOffer offer) {
		if (trader == null || offer == null) {
			return;
		}
		// Reject the trade immediately if the player doesn't have enough resources to cover the offer
		if (offer.offeredFood > currentFood || offer.offeredWater > currentWater || offer.offeredGold > currentGold) {
			return;
		}
		trader.interact(this);
		trader.receiveOffer(offer);
		// Only update resources if the trader accepted — cap food/water at max in case the trade pushes them over
		if (trader.getState() == TraderState.TRADE_COMPLETED) {
			currentFood  = Math.min(currentFood  - offer.offeredFood  + offer.requestedFood,  maxFood);
			currentWater = Math.min(currentWater - offer.offeredWater + offer.requestedWater, maxWater);
			currentGold  = currentGold - offer.offeredGold + offer.requestedGold;
		}
	}

	// Applies passive terrain drain each turn the player stays on the same square (called when resting)
	public void updateResources() {
		TerrainType t = currentSquare.getTerrain();
		currentStrength -= t.moveCost;
		currentWater -= t.waterCost;
		currentFood -= t.foodCost;
	}

	// --- Getters --- outside classes can read state but never write it directly
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
