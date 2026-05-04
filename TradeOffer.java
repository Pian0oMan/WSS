//represents what is being offered and requested during negotiations

public class TradeOffer {
    public int offeredFood;
    public int offeredWater;
    public int offeredGold;

    public int requestedFood;
    public int requestedWater;
    public int requestedGold;

    //creates new offer
    public TradeOffer(int offeredFood, int offeredWater, int offeredGold,
                      int requestedFood, int requestedWater, int requestedGold) {
        this.offeredFood = offeredFood;
        this.offeredWater = offeredWater;
        this.offeredGold = offeredGold;
        this.requestedFood = requestedFood;
        this.requestedWater = requestedWater;
        this.requestedGold = requestedGold;
    }
    
    //format actual trade
    @Override
    public String toString() {
        return "Offer: give(" + offeredFood +
               " food, " + offeredWater +
               " water, " + offeredGold +
               " gold) for (" + requestedFood +
               " food, " + requestedWater +
               " water, " + requestedGold + 
               " gold)";
    }
}