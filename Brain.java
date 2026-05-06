public class Brain {

    private Player player;
    private String brainType;
    private Vision vision;
    private gameMap map;
    private String lastAction = "";

    public Brain(Player p, gameMap m, String brainType, String visionType) {
        player = p;
        map = m;
        this.brainType = brainType;
        vision = new Vision(this, m, visionType);
    }

    public void makeMove() {
        vision.look();
        double threshold = survivalThreshold();
        for (String priority : decisionList()) {
            if (priority.equals("Survival") && needsSurvival(threshold)) {
                Path p = pickBetterPath(vision.find("Food"), vision.find("Water"));
                if (p != null && executeMove(vision.findFirstStep(p))) return;
                player.rest();
                lastAction = "Rested (no resource visible)";
                return;
            }
            if (priority.equals("East")) {
                Path p = vision.find("East");
                if (p != null && executeMove(vision.findFirstStep(p))) return;
            }
            if (priority.equals("Trader") && player.getCurrentGold() > 0) {
                Path p = vision.find("Trader");
                if (p != null && executeMove(vision.findFirstStep(p))) return;
            }
        }
        // fallback: step east unconditionally
        if (!executeMove(new int[]{0, 1})) {
            player.rest();
            lastAction = "Rested (at east edge)";
        }
    }

    private boolean executeMove(int[] step) {
        int newRow = player.getRowPos() + step[0];
        int newCol = player.getColPos() + step[1];
        Square dest = map.getMapSquare(newRow, newCol);
        if (dest == null) return false;
        player.move(dest, newRow, newCol);
        lastAction = "Moved " + directionName(step);
        Item item = dest.getItem();
        if (item instanceof Trader) {
            attemptTrade((Trader) item);
        } else if (item != null) {
            player.collectItem(item);
            lastAction += " + " + item.getClass().getSimpleName();
        }
        return true;
    }

    private void attemptTrade(Trader trader) {
        int gold = Math.min(player.getCurrentGold(), 10);
        int reqFood = 0, reqWater = 0;
        if (player.getCurrentFood() <= player.getCurrentWater()) reqFood = 15;
        else reqWater = 15;
        TradeOffer offer = new TradeOffer(0, 0, gold, reqFood, reqWater, 0);
        player.trade(trader, offer);
        if (trader.getState() == TraderState.TRADE_COMPLETED) {
            StringBuilder sb = new StringBuilder("Trade: gave ");
            if (gold   > 0) sb.append(gold).append("g ");
            sb.append("→ got ");
            if (reqFood  > 0) sb.append(reqFood).append(" food");
            if (reqWater > 0) sb.append(reqWater).append(" water");
            lastAction = sb.toString().trim();
        } else {
            lastAction = "Trade rejected by Trader";
        }
    }

    public String[] decisionList() {
        switch (brainType) {
            case "A": return new String[] { "East", "Survival", "Trader" };
            case "B": return new String[] { "Survival", "East", "Trader" };
            case "C": return new String[] { "Trader", "Survival", "East" };
            case "D": return new String[] { "East", "Trader", "Survival" };
            default:  return new String[] { "East", "Survival", "Trader" };
        }
    }

    private double survivalThreshold() {
        switch (brainType) {
            case "A": return 0.25;
            case "B": return 0.60;
            case "C": return 0.60;
            case "D": return 0.40;
            default:  return 0.40;
        }
    }

    private boolean needsSurvival(double threshold) {
        double s = (double) player.getCurrentStrength() / player.getMaxStrength();
        double w = (double) player.getCurrentWater()    / player.getMaxWater();
        double f = (double) player.getCurrentFood()     / player.getMaxFood();
        return Math.min(s, Math.min(w, f)) < threshold;
    }

    private Path pickBetterPath(Path food, Path water) {
        if (food == null)  return water;
        if (water == null) return food;
        return player.getCurrentFood() <= player.getCurrentWater() ? food : water;
    }

    private String directionName(int[] step) {
        if (step[1] > 0 && step[0] == 0) return "East";
        if (step[1] < 0 && step[0] == 0) return "West";
        if (step[0] < 0) return step[1] > 0 ? "NE" : step[1] < 0 ? "NW" : "North";
        if (step[0] > 0) return step[1] > 0 ? "SE" : step[1] < 0 ? "SW" : "South";
        return "Still";
    }

    public String getLastAction()  { return lastAction; }

    public Vision getVision()      { return vision; }

    public String getBrainLabel() {
        switch (brainType) {
            case "A": return "East-Focused";
            case "B": return "Survival-First";
            case "C": return "Trader-Seeker";
            case "D": return "Balanced";
            default:  return "Unknown";
        }
    }

    public int getRowPos() { return player.getRowPos(); }
    public int getColPos() { return player.getColPos(); }
}
