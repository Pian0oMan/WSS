import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Display extends JFrame {

    private static final int TILE_SIZE = 32;
    private static final int VIEWPORT_COLS = 25;
    private static final int VIEWPORT_ROWS = 20;
    private static final int STATS_PANEL_HEIGHT = 100;

    private gameMap map;
    private Player player;
    private MapPanel mapPanel;
    private StatsPanel statsPanel;

    // terrain colors
    private static final Color COLOR_PLAINS   = new Color(144, 201, 120);
    private static final Color COLOR_MOUNTAIN = new Color(150, 140, 130);
    private static final Color COLOR_DESERT   = new Color(237, 201, 100);
    private static final Color COLOR_SWAMP    = new Color(90,  130,  90);
    private static final Color COLOR_FOREST   = new Color(34,  100,  34);
    private static final Color COLOR_PLAYER   = new Color(220,  60,  60);
    private static final Color COLOR_ITEM     = new Color(255, 215,   0);
    private static final Color COLOR_TRADER   = new Color(180,  80, 220);

    public Display(gameMap map, Player player) {
        this.map = map;
        this.player = player;

        setTitle("Survival Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLayout(new BorderLayout());

        mapPanel = new MapPanel();
        statsPanel = new StatsPanel();

        add(mapPanel, BorderLayout.CENTER);
        add(statsPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);

        // key input
        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKey(e.getKeyCode());
            }
        });
    }

    private void handleKey(int keyCode) {
        // just triggers a move via the brain and repaints
        player.getBrain().makeMove();
        repaint();

        if (player.getCurrentStrength() <= 0 ||
            player.getCurrentFood() <= 0 ||
            player.getCurrentWater() <= 0) {
            JOptionPane.showMessageDialog(this, "You died!", "Game Over", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
    }

    public void render() {
        setVisible(true);
        repaint();
    }

    // -------------------------------------------------------
    // Map Panel
    // -------------------------------------------------------
    private class MapPanel extends JPanel {

        public MapPanel() {
            setPreferredSize(new Dimension(VIEWPORT_COLS * TILE_SIZE, VIEWPORT_ROWS * TILE_SIZE));
            setBackground(Color.BLACK);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int playerRow = player.getRowPos();
            int playerCol = player.getColPos();

            // center viewport on player
            int startRow = playerRow - VIEWPORT_ROWS / 2;
            int startCol = playerCol - VIEWPORT_COLS / 2;

            for (int r = 0; r < VIEWPORT_ROWS; r++) {
                for (int c = 0; c < VIEWPORT_COLS; c++) {
                    int mapRow = startRow + r;
                    int mapCol = startCol + c;

                    int px = c * TILE_SIZE;
                    int py = r * TILE_SIZE;

                    // out of bounds = black
                    if (mapRow < 0 || mapCol < 0) {
                        g2.setColor(Color.BLACK);
                        g2.fillRect(px, py, TILE_SIZE, TILE_SIZE);
                        continue;
                    }

                    Square sq;
                    try {
                        sq = map.getMapSquare(mapRow, mapCol);
                    } catch (ArrayIndexOutOfBoundsException e) {
                        g2.setColor(Color.BLACK);
                        g2.fillRect(px, py, TILE_SIZE, TILE_SIZE);
                        continue;
                    }

                    // draw terrain
                    g2.setColor(getTerrainColor(sq.getTerrain()));
                    g2.fillRect(px, py, TILE_SIZE, TILE_SIZE);

                    // draw grid lines
                    g2.setColor(new Color(0, 0, 0, 40));
                    g2.drawRect(px, py, TILE_SIZE, TILE_SIZE);

                    // draw item if present
                    Item item = sq.getItem();
                    if (item != null) {
                        if (item instanceof Trader) {
                            g2.setColor(COLOR_TRADER);
                        } else {
                            g2.setColor(COLOR_ITEM);
                        }
                        int dotSize = TILE_SIZE / 3;
                        g2.fillOval(px + TILE_SIZE / 2 - dotSize / 2,
                                    py + TILE_SIZE / 2 - dotSize / 2,
                                    dotSize, dotSize);
                    }

                    // draw player
                    if (mapRow == playerRow && mapCol == playerCol) {
                        g2.setColor(COLOR_PLAYER);
                        int margin = 4;
                        g2.fillOval(px + margin, py + margin,
                                    TILE_SIZE - margin * 2, TILE_SIZE - margin * 2);
                        g2.setColor(Color.WHITE);
                        g2.setFont(new Font("Monospaced", Font.BOLD, 14));
                        g2.drawString("@", px + TILE_SIZE / 2 - 5, py + TILE_SIZE / 2 + 5);
                    }
                }
            }
        }

        private Color getTerrainColor(TerrainType t) {
            switch (t) {
                case PLAINS:   return COLOR_PLAINS;
                case MOUNTAIN: return COLOR_MOUNTAIN;
                case DESERT:   return COLOR_DESERT;
                case SWAMP:    return COLOR_SWAMP;
                case FOREST:   return COLOR_FOREST;
                default:       return Color.GRAY;
            }
        }
    }

    // -------------------------------------------------------
    // Stats Panel
    // -------------------------------------------------------
    private class StatsPanel extends JPanel {

        public StatsPanel() {
            setPreferredSize(new Dimension(VIEWPORT_COLS * TILE_SIZE, STATS_PANEL_HEIGHT));
            setBackground(new Color(30, 30, 30));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int barWidth = 150;
            int barHeight = 16;
            int startX = 20;
            int startY = 20;
            int gap = 28;

            // Strength
            drawBar(g2, startX, startY,          barWidth, barHeight, "Strength",
                    player.getCurrentStrength(), player.getMaxStrength(), new Color(220, 80, 80));
            // Water
            drawBar(g2, startX, startY + gap,    barWidth, barHeight, "Water",
                    player.getCurrentWater(), player.getMaxWater(), new Color(80, 160, 220));
            // Food
            drawBar(g2, startX, startY + gap * 2, barWidth, barHeight, "Food",
                    player.getCurrentFood(), player.getMaxFood(), new Color(144, 201, 120));

            // Gold (no bar, just text)
            g2.setColor(new Color(255, 215, 0));
            g2.setFont(new Font("Monospaced", Font.BOLD, 13));
            g2.drawString("Gold: " + player.getCurrentGold(), startX + barWidth + 40, startY + 12);

            // Position
            g2.setColor(Color.LIGHT_GRAY);
            g2.setFont(new Font("Monospaced", Font.PLAIN, 11));
            g2.drawString("Pos: (" + player.getRowPos() + ", " + player.getColPos() + ")",
                          startX + barWidth + 40, startY + gap);

            // Legend
            drawLegend(g2, startX + barWidth + 200, startY);
        }

        private void drawBar(Graphics2D g2, int x, int y, int w, int h,
                             String label, int current, int max, Color color) {
            // label
            g2.setColor(Color.LIGHT_GRAY);
            g2.setFont(new Font("Monospaced", Font.PLAIN, 11));
            g2.drawString(label, x, y + h - 2);

            int labelWidth = 60;
            int bx = x + labelWidth;

            // background
            g2.setColor(new Color(60, 60, 60));
            g2.fillRoundRect(bx, y, w, h, 6, 6);

            // fill
            float ratio = Math.max(0, Math.min(1, (float) current / max));
            g2.setColor(color);
            g2.fillRoundRect(bx, y, (int)(w * ratio), h, 6, 6);

            // border
            g2.setColor(new Color(100, 100, 100));
            g2.drawRoundRect(bx, y, w, h, 6, 6);

            // value text
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Monospaced", Font.BOLD, 10));
            String val = current + "/" + max;
            g2.drawString(val, bx + w / 2 - val.length() * 3, y + h - 3);
        }

        private void drawLegend(Graphics2D g2, int x, int y) {
            Object[][] entries = {
                { COLOR_PLAINS,   "Plains"   },
                { COLOR_MOUNTAIN, "Mountain" },
                { COLOR_DESERT,   "Desert"   },
                { COLOR_SWAMP,    "Swamp"    },
                { COLOR_FOREST,   "Forest"   },
                { COLOR_ITEM,     "Item"     },
                { COLOR_TRADER,   "Trader"   },
            };
            g2.setFont(new Font("Monospaced", Font.PLAIN, 10));
            int col = 0;
            for (int i = 0; i < entries.length; i++) {
                int lx = x + col * 100;
                int ly = y + (i % 4) * 18;
                if (i == 4) col++;
                g2.setColor((Color) entries[i][0]);
                g2.fillRect(lx, ly, 12, 12);
                g2.setColor(Color.LIGHT_GRAY);
                g2.drawString((String) entries[i][1], lx + 16, ly + 11);
            }
        }
    }
}