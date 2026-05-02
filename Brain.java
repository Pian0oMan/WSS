public class Brain{

	private Player player;
	private String brainType;
	private Vision vision;

	public Brain(Player p, gameMap m, String brainType, String visionType){
		player = p;
		this.brainType = brainType;
		vision = new Vision(this, m, visionType);
	}

	public void makeMove(){
		vision.look();
		//figure out how to have different brainTypes decide differently (east then food vs food then east)
	}

	public String[] decisionList(){
		if(brainType.equals("A")){
			return new String[] { "East", "Survival", "Trader" };
		}
		else if(brainType.equals("B")){
			return new String[] { "East", "Survival", "Trader" };
		}
		else if(brainType.equals("C")){
			return new String[] { "East", "Survival", "Trader" };
		}
		else if(brainType.equals("D")){
			return new String[] { "East", "Survival", "Trader" };
		}
		else{
			return new String[] { "East", "Survival", "Trader" };
		}
	}
		public int getRowPos(){
		return player.getRowPos();
	}
	public int getColPos(){
		return player.getColPos();
	}
	}


