import java.util.Random;
public class Map {

    //chances in integers representing CHANCE/100
    private int ITEM_CHANCE = 10;
    private int TRADER_CHANCE = 5;

    private int width;
    private int height;
    private Square[][] grid;
    private Random rand = new Random();

    public Map(int width, int height, int difficulty)
    {
        this.width = width;
        this.height = height;
        this.grid = new Square[height][width];

        generateTerrain(difficulty);
        populateItems();
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
            if (roll <= 30)
                return TerrainType.PLAINS;
            if (roll <= 60)
                return TerrainType.FOREST;
            else
                return TerrainType.MOUNTAIN;
        }
        else
        {
            if (roll <= 40)
                return TerrainType.DESERT;
            if (roll <= 70)
                return TerrainType.MOUNTAIN;
            else
                return TerrainType.SWAMP;
        }
    }
    
    private void populateItems()
    {
        int randomNumber;
        for (int i = 0; i < height; i++)
        {
            for (int j = 0; j < width; j++)
            {
                randomNumber = rand.nextInt(100);
                if (randomNumber >= TRADER_CHANCE && randomNumber < (ITEM_CHANCE + TRADER_CHANCE))
                {
                    grid[i][j].addItem(new FoodBonus(5, false);
                }
                else if (randomNumber < TRADER_CHANCE)
                {
                    grid[i][j].addItem(new Trader());
                }
            }
        }
    }

    public Square getMapSquare(int row, int column)
    {
        return grid[row][column];
    }
}