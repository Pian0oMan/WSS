public class Brain{

	private Player player;
	private Vision vision;
	private String brainType;

	private float survivalRatio, goldRatio, traderRatio;

	public Brain(Player p, gameMap m, String brainType, String visionType){
		player = p;
		if(brainType.equals("Survival")){
			survivalRatio = 0.5;
			goldRatio = 0.2;
			traderRatio = 0.3;
		}
		else if(brainType.equals("East")){
			survivalRatio = 0.3;
			goldRatio = 0.0;
			traderRatio = 0.1;
		}
		else if(brainType.equals("Greedy")){
			survivalRatio = 0.4;
			goldRatio = 0.8;
			traderRatio = 0.6;
		}
		else if(brainType.equals("Merchant")){
			survivalRatio = 0.4;
			goldRatio = 0.3;
			traderRatio = 0.8;
		}
		else{
			survivalRatio = 0;
			goldRatio = 0;
			traderRatio = 0;
		}
		this.brainType = brainType;
		vision = new Vision(this, m, visionType);
	}

	public void makeMove(){
		vision.look();
		//find highest importance out of survival, gold, and trader
		if(brainType.equals("Survival") || brainType.equals("East")){
			if(checkNeedSurvival()){
				//figure out what survival stats are needed and look for those
				//if one is found, break and go to it
			}
			if(checkCanSearchTrader()){
				//find trader, if exists break and go to it
			}
			if(checkCanSearchGold()){
				//find gold, if exists break and go to it
			}
			//else find east and go to it
		}
		else if(brainType.equals("Greedy")){
			if(checkCanSearchGold()){
				//find gold, if exists break and go to it
			}
			if(checkCanSearchTrader()){
				//find trader, if exists break and go to it
			}
			if(checkNeedSurvival()){
				//figure out what survival stats are needed and look for those
				//if one is found, break and go to it
			}
			//else find east and go to it
		}
		else if(brainType.equals("Merchant")){
			if(checkCanSearchTrader()){
				//find trader, if exists break and go to it
			}
			if(checkNeedSurvival()){
				//figure out what survival stats are needed and look for those
				//if one is found, break and go to it
			}
			if(checkCanSearchGold()){
				//find gold, if exists break and go to it
			}
			//else find east and go to it
		}
		else{
			//else find east and go to it
		}
	}

	public boolean checkNeedSurvival(){
		//returns needfood || needwater || needenergy
	}
	
	public boolean checkNeedFood(){
		//checks if current food divided by max food is less than survivalRatio
	}

	public boolean checkNeedWater(){
		//checks if current water divided by max water is less than survivalRatio
	}

	public boolean checkNeedEnergy(){
		//checks if current energy divided by energy food is less than survivalRatio
	}

	public boolean checkCanSearchGold(){
		//checks if all 3 survival stats ratios are less than goldRatio
	}

	public boolean checkCanSearchTrader(){
		//checks if all 3 survival stats ratios are less than traderRatio
	}
	
	public int getRowPos(){
		return player.getRowPos();
	}
	public int getColPos(){
		return player.getColPos();
	}
}


