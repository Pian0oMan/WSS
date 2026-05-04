public class Trader extends Item {
    private TraderState state;
    private int counterOffers;
    private final int maxCounterOffers = 5; //trader tired LOL

    public Trader(String name){
        super(true);    //repeating item
        this.state = TraderState.IDLE;  
        this.counterOffers = 0; //reset
    }

    //called when trader enters players radar
    @Override
    public void collect(Player player) {
        System.out.println("Interact with trader"); //player can choose to interact
    }

    //starts trading session
    public void interact(Player player) {
        state = TraderState.WAITING_FOR_OFFER;
        counterOffers = 0;
    }

    //player submits offer
    public TradeOffer receiveOffer(TradeOffer offer) {
        state = TraderState.OFFER_RECEIVED;
        return evaluateOffer(offer);    
    }

    private TradeOffer evaluateOffer(TradeOffer offer) {
        state = TraderState.EVALUATING_OFFER;   //move state

        //calculate value of offer
        int offeredValue = offer.offeredFood + offer.offeredWater + offer.offeredGold * 3;
        int requestedValue = offer.requestedFood + offer.requestedWater + offer.requestedGold * 3;

        //accept case
        if (offeredValue >= requestedValue) {
            state = TraderState.ACCEPTING;
            state = TraderState.TRADE_COMPLETED;
            return null;
        }

        //angry case
        if (counterOffers >= maxCounterOffers) {
            state = TraderState.ANGRY;
            state = TraderState.END_NEGOTIATION;    //doesnt want to continue negotiating
            return null;
        }

        //counter case
        return makeCounterOffer(offer);
    }

    //trader modifies offer 
    private TradeOffer makeCounterOffer(TradeOffer offer) {
        state = TraderState.COUNTER_OFFERING;   //move state
        counterOffers++;    //keep track

        TradeOffer counter = new TradeOffer(    //create new offer
            offer.offeredFood + 1,
            offer.offeredWater,
            offer.offeredGold,
            offer.requestedFood,
            offer.requestedWater,
            offer.requestedGold
        );

        state = TraderState.WAITING_FOR_OFFER;  //change state
        return counter;
    }

    //player accepts trader counter
    public void playerAcceptsCounterOffer() {
        state = TraderState.ACCEPTING;
        state = TraderState.TRADE_COMPLETED;
    }

    //player rejects trader counter
    public void playerRejectsCounterOffer() {
        state = TraderState.REJECTING;
        state = TraderState.END_NEGOTIATION;
    }

    //allow classes to check current state
    public TraderState getState() {
        return state;
    }
}
