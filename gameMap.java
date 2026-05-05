import java.util.Random;
public class gameMap {

    private int width;
    private int height;
    private int difficulty;
    private Square[][] grid;
    private Random rand = new Random();

    public gameMap(int width, int height, int difficulty)
    {
        this.width = width;
        this.height = height;
        this.difficulty = difficulty;
        this.grid = new Square[height][width];

        generateTerrain(difficulty);
        populateItems(difficulty);
    }

    public void generateTerrain(int difficulty)
    {
        for (int i = 0; i < height; i++)
        {
            for (int j = 0; j < width; j++)
            {
                TerrainType t = pickTerrain(difficulty);
                grid[i][j] = new Square(t);
            }
        }
    }

    private TerrainType pickTerrain(int difficulty)
    {
        int roll = rand.nextInt(100);

        if (difficulty == 1)
        {
            if (roll <= 40)
                return TerrainType.PLAINS;
            if (roll <= 65)
                return TerrainType.FOREST;
            if (roll <= 75)
                return TerrainType.MOUNTAIN;
            if (roll <= 85)
                return TerrainType.DESERT;
            else
                return TerrainType.SWAMP;
        }
        else
        {
            if (roll <= 30)
                return TerrainType.MOUNTAIN;
            if (roll <= 55)
                return TerrainType.DESERT;
            if (roll <= 75)
                return TerrainType.SWAMP;
            if (roll <= 85)
                return TerrainType.PLAINS;
            else
                return TerrainType.FOREST;
        }
    }

    private void populateItems(int difficulty)
    {
        for (int i = 0; i < height; i++)
        {
            for (int j = 0; j < width; j++)
            {
                int roll = rand.nextInt(100);
                TerrainType terrain = grid[i][j].getTerrain();

                if (difficulty == 1)
                {
                    // Easy mode
                    if (roll < 5)
                    {
                        grid[i][j].addItem(new Trader("Trader"));
                    }
                    else if (roll < 15)
                    {
                        grid[i][j].addItem(new FoodBonus(getFoodAmount(terrain), false));
                    }
                    else if (roll < 25)
                    {
                        grid[i][j].addItem(new WaterBonus(getWaterAmount(terrain), false));
                    }
                    else if (roll < 35)
                    {
                        grid[i][j].addItem(new GoldBonus(getGoldAmount(terrain), false));
                    }
                }
                else
                {
                    // Hard mode
                    if (roll < 5)
                    {
                        grid[i][j].addItem(new Trader("Trader"));
                    }
                    else if (roll < 10)
                    {
                        grid[i][j].addItem(new FoodBonus(getFoodAmount(terrain), false));
                    }
                    else if (roll < 15)
                    {
                        grid[i][j].addItem(new WaterBonus(getWaterAmount(terrain), false));
                    }
                    else if (roll < 20)
                    {
                        grid[i][j].addItem(new GoldBonus(getGoldAmount(terrain), false));
                    }
                }
            }
        }
    }

    // how much food a terrain gives
    private int getFoodAmount(TerrainType terrain)
    {
        switch (terrain)
        {
            case PLAINS:   return 20;
            case FOREST:   return 15;
            case MOUNTAIN: return 5;
            case DESERT:   return 5;
            case SWAMP:    return 10;
            default:       return 10;
        }
    }

    // how much water a terrain gives
    private int getWaterAmount(TerrainType terrain)
    {
        switch (terrain)
        {
            case PLAINS:   return 15;
            case FOREST:   return 25;
            case MOUNTAIN: return 5;
            case DESERT:   return 3;
            case SWAMP:    return 15;
            default:       return 10;
        }
    }

    // how much gold a terrain gives
    private int getGoldAmount(TerrainType terrain)
    {
        switch (terrain)
        {
            case PLAINS:   return 5;
            case FOREST:   return 5;
            case MOUNTAIN: return 25;
            case DESERT:   return 15;
            case SWAMP:    return 10;
            default:       return 5;
        }
    }

    public Square getMapSquare(int row, int column)
    {
        return grid[row][column];
    }
}