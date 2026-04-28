public class Vision{

	private Brain brain;
	private Map map;
	private int[][] sight;
	private Square[] tiles;

	public Vision(Brain b, Map m, String visionType){
		brain = b;
		map = m;
		if(visionType.equals("Focused"){
			sight = new int[][] { {0, 1}, {-1, 1}, {1, 1}, {0, 2}, {-1, 2}, {1, 2} };
		}
		else if(visionType.equals("Eyes Peeled"){
			sight = new int[][] { {-1, 0}, {1, 0}, {0, 1}, {-1, 1}, {1, 1}, {0, 2} };
		}
		else if(visionType.equals("Far Sighted"){
			sight = new int[][] { {0, 1}, {0, 2}, {-1, 2}, {1, 2}, {-2, 3}, {2, 3} };
		}
		else if(visionType.equals("Cross Eyed"){
			sight = new int[][] { {-1, 0}, {1, 0}, {-1, 1}, {1, 1}, {-2, 1}, {2, 1} };
		}
		else{
			sight = new int[][] { {0, 1} };
		}
	}

	public void look(){
		tiles = new Square[sight.length];
		for(int i = 0; i < sight.length; i++){
			tiles[i] = map.getMapSquare(brain.getXPos() + sight[i][0], brain.getYPos() + sight[i][1]);
		}
	}

	public Path findFood(){
		int best = -1;
		for(int i = 0; i < tiles.length; i++){
			Square s = tiles[i];
			if(s.getItem() != null && (s.getItem() instanceof FoodBonus)){
				if(best == -1){
					best = s;
				}
				else{
					Square comp = tiles[best];
					//compare if items are repeating or not
					if(!comp.getItem().isRepeating() && s.getItem().isRepeating()){
						best = i;
					}
					else if(comp.getItem().isRepeating() && !s.getItem().isRepeating()){
						continue;
					}
					//compare item values
					if(comp.getItem().getValue() < s.getItem().getValue()){
						best = i;
					}
					else if(comp.getItem().getValue() > s.getItem().getValue()){
						continue;
					}
					//WIP: compare cost to acquire
				}
			}
		}
		if(best == -1){
			return null;
		}
		return new Path(sight[i], tiles[i].getTerrain().getCosts());
	}

	public Path findWater(){

	}

	public Path findTrader(){

	}

	public Path findEast(){

	}
}
