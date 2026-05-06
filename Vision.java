import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Vision {

    private Brain brain;
    private gameMap map;
    private int[][] sight;
    private String visionType;
    private Square[] tiles;
    private int[][] activeSight;

    public Vision(Brain b, gameMap m, String visionType) {
        brain = b;
        map = m;
        this.visionType = visionType;
        if (visionType.equals("A")) {
            sight = new int[][] { {0,1}, {-1,1}, {1,1}, {0,2}, {-1,2}, {1,2} };
        } else if (visionType.equals("B")) {
            sight = new int[][] { {-1,0}, {1,0}, {0,1}, {-1,1}, {1,1}, {0,2} };
        } else if (visionType.equals("C")) {
            sight = new int[][] { {0,1}, {0,2}, {-1,2}, {1,2}, {-2,3}, {2,3} };
        } else if (visionType.equals("D")) {
            sight = new int[][] { {-1,0}, {1,0}, {-1,1}, {1,1}, {-2,1}, {2,1} };
        } else {
            sight = new int[][] { {0,1} };
        }
    }

    public void look() {
        List<Square> visible = new ArrayList<>();
        List<int[]> offsets = new ArrayList<>();
        for (int[] s : sight) {
            Square sq = map.getMapSquare(brain.getRowPos() + s[0], brain.getColPos() + s[1]);
            if (sq != null) {
                visible.add(sq);
                offsets.add(s);
            }
        }
        tiles = visible.toArray(new Square[0]);
        activeSight = offsets.toArray(new int[0][]);
    }

    public Path find(String toFind) {
        Class<?> findType;
        if (toFind.equals("Food")) {
            findType = FoodBonus.class;
        } else if (toFind.equals("Water")) {
            findType = WaterBonus.class;
        } else if (toFind.equals("Gold")) {
            findType = GoldBonus.class;
        } else if (toFind.equals("Trader")) {
            findType = Trader.class;
        } else if (toFind.equals("East")) {
            return findEast();
        } else {
            return null;
        }

        int best = -1;
        for (int i = 0; i < tiles.length; i++) {
            Square s = tiles[i];
            if (s == null) continue;
            if (s.getItem() != null && findType.isInstance(s.getItem())) {
                if (best == -1) {
                    best = i;
                } else {
                    Square comp = tiles[best];
                    if (!comp.getItem().isRepeating() && s.getItem().isRepeating()) {
                        best = i;
                    } else if (comp.getItem().isRepeating() && !s.getItem().isRepeating()) {
                        continue;
                    }
                    if (comp.getItem().getValue() < s.getItem().getValue()) {
                        best = i;
                    } else if (comp.getItem().getValue() > s.getItem().getValue()) {
                        continue;
                    }
                }
            }
        }
        if (best == -1) return null;
        return new Path(activeSight[best], tiles[best].getTerrain().getCosts());
    }

    public Path findEast() {
        int best = -1;
        for (int i = 0; i < tiles.length; i++) {
            if (best == -1) {
                best = i;
            } else {
                if (activeSight[best][1] < activeSight[i][1]) {
                    best = i;
                } else if (activeSight[best][1] > activeSight[i][1]) {
                    continue;
                }
                int currentSum = Arrays.stream(tiles[i].getTerrain().getCosts()).sum();
                int bestSum = Arrays.stream(tiles[best].getTerrain().getCosts()).sum();
                if (currentSum < bestSum) {
                    best = i;
                }
            }
        }
        if (best == -1) return null;
        return new Path(activeSight[best], tiles[best].getTerrain().getCosts());
    }

    public int[] findFirstStep(Path p) {
        int[] pos = p.getPosition();
        if (Math.abs(pos[0]) <= 1 && Math.abs(pos[1]) <= 1) {
            return pos;
        }
        int stepRow = Integer.signum(pos[0]);
        int stepCol = Integer.signum(pos[1]);
        return new int[] { stepRow, stepCol };
    }

    public String getVisionLabel() {
        switch (visionType) {
            case "A": return "Focused";
            case "B": return "Eyes Peeled";
            case "C": return "Far Sighted";
            case "D": return "Cross Eyed";
            default:  return "Default";
        }
    }
}
