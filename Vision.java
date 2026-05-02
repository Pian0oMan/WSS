import java.util.Arrays;
import java.util.stream.IntStream;

public class Vision{

	private Brain brain;
	private gameMap map;
	private int[][] sight;
	private Square[] tiles;

	public Vision(Brain b, gameMap m, String visionType){
		brain = b;
		map = m;
		if(visionType.equals("Focused")){
			sight = new int[][] { {0, 1}, {-1, 1}, {1, 1}, {0, 2}, {-1, 2}, {1, 2} };
		}
		else if(visionType.equals("Eyes Peeled")){
			sight = new int[][] { {-1, 0}, {1, 0}, {0, 1}, {-1, 1}, {1, 1}, {0, 2} };
		}
		else if(visionType.equals("Far Sighted")){
			sight = new int[][] { {0, 1}, {0, 2}, {-1, 2}, {1, 2}, {-2, 3}, {2, 3} };
		}
		else if(visionType.equals("Cross Eyed")){
			sight = new int[][] { {-1, 0}, {1, 0}, {-1, 1}, {1, 1}, {-2, 1}, {2, 1} };
		}
		else{
			sight = new int[][] { {0, 1} };
		}
	}

	public void look(){
		tiles = new Square[sight.length];
		for(int i = 0; i < sight.length; i++){
			tiles[i] = map.getMapSquare(brain.getRowPos() + sight[i][0], brain.getColPos() + sight[i][1]);
		}
	}

	public Path find(String toFind){
		Class<?> findType;
		if(toFind.equals("Food")){
			findType = FoodBonus.class;
		}
		else if(toFind.equals("Water")){
			findType = WaterBonus.class;
		}
		else if(toFind.equals("Gold")){
			findType = GoldBonus.class;
		}
		else if(toFind.equals("Trader")){
			findType = Trader.class;
		}
		else if(toFind.equals("East")){
			return findEast();
		}
		else{
			return null;
		}
		
		int best = -1;
		for(int i = 0; i < tiles.length; i++){
			Square s = tiles[i];
			if(s.getItem() != null && (s.getItem() instanceof findType)){
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

	public Path findEast(){
		int best = -1;
		for(int i = 0; i < tiles.length; i++){
			if(best == -1){
				best = s;
			}
			else{
				//compare if location is further east or not
				if(sight[best][1] < sight[i][1]){
					best = i;
				}
				else if(sight[best][1] > sight[i][1]){
					continue;
				}
				//compare terrain costs (for now only considers final tile's terrain cost)
				int currentSumTerrainCost = Arrays.stream(tiles[i].getTerrain().getCosts()).sum();
				int bestSumTerrainCost = Arrays.stream(tiles[best].getTerrain().getCosts()).sum();
				if(currentSumTerrainCost > bestSumTerrainCost){
					best = i;
				}
				else if(currentSumTerrainCost < bestSumTerrainCost){
					continue;
				}
			}
		}
		if(best == -1){
			return null;
		}
		return new Path(sight[i], tiles[i].getTerrain().getCosts());
	}

	public int[] findFirstStep(Path p){
		IntStream path = Arrays.stream(p.getPosition());
		if(path.max() <= 1 && path.min() >= -1){
			return p.getPosition();
		}

		//implement later
	}
}
