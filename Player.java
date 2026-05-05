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

	public void trade(Trader trader, TradeOffer offer) {
		if (trader == null || offer == null) {
			return;
		}
		if (offer.offeredFood > currentFood || offer.offeredWater > currentWater || offer.offeredGold > currentGold) {
			return;
		}
		trader.interact(this);
		trader.receiveOffer(offer);
		if (trader.getState() == TraderState.TRADE_COMPLETED) {
			currentFood  = Math.min(currentFood  - offer.offeredFood  + offer.requestedFood,  maxFood);
			currentWater = Math.min(currentWater - offer.offeredWater + offer.requestedWater, maxWater);
			currentGold  = currentGold - offer.offeredGold + offer.requestedGold;
		}
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
