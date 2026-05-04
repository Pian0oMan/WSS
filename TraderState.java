//keep track of what state the trader is currently in
public enum TraderState {
    IDLE,
    WAITING_FOR_OFFER,
    OFFER_RECEIVED,
    EVALUATING_OFFER,
    COUNTER_OFFERING,
    ACCEPTING,
    REJECTING,
    ANGRY,
    TRADE_COMPLETED,
    END_NEGOTIATION
}