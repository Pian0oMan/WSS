import java.awt.*;
import javax.swing.*;

public class Display extends JFrame {

    private static final int TILE_SIZE      = 32;
    private static final int VIEWPORT_COLS  = 25;
    private static final int VIEWPORT_ROWS  = 20;
    private static final int STATS_HEIGHT   = 110;
    private static final int CONTROL_HEIGHT = 44;
    private static final int INFO_WIDTH     = 220;


    private static final Color BG_DARK      = new Color(18,  18,  18);
    private static final Color BG_PANEL     = new Color(28,  28,  35);
    private static final Color BG_LOG       = new Color(22,  22,  28);
    private static final Color BORDER_COLOR = new Color(55,  55,  70);
    private static final Color TEXT_DIM     = new Color(160, 160, 175);
    private static final Color TEXT_LABEL   = new Color(200, 200, 215);

    private static final Color COLOR_PLAINS   = new Color(144, 201, 120);
    private static final Color COLOR_MOUNTAIN = new Color(150, 140, 130);
    private static final Color COLOR_DESERT   = new Color(237, 201, 100);
    private static final Color COLOR_SWAMP    = new Color( 90, 130,  90);
    private static final Color COLOR_FOREST   = new Color( 34, 100,  34);
    private static final Color COLOR_PLAYER   = new Color(220,  60,  60);
    private static final Color COLOR_ITEM     = new Color(255, 215,   0);
    private static final Color COLOR_TRADER   = new Color(180,  80, 220);

    private gameMap map;
    private Player  player;
    private Runnable onReset;
    private MapPanel     mapPanel;
    private StatsPanel   statsPanel;

    private javax.swing.Timer        autoTimer;
    private boolean                  running   = false;
    private int                      timerDelay = 300;
    private DefaultListModel<String> logModel  = new DefaultListModel<>();
    private JList<String>            actionLog = new JList<>(logModel);
    private JButton                  playPauseBtn;

    public Display(gameMap map, Player player, Runnable onReset) {
        this.onReset = onReset;
        this.map    = map;
        this.player = player;

        String brainLabel  = player.getBrain().getBrainLabel();
        String visionLabel = player.getBrain().getVision().getVisionLabel();

        setTitle("Survival Game  —  Brain: " + brainLabel + "  |  Vision: " + visionLabel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BG_DARK);

        mapPanel   = new MapPanel();
        statsPanel = new StatsPanel();
        InfoPanel    infoPanel    = new InfoPanel(brainLabel, visionLabel);
        ControlPanel controlPanel = new ControlPanel();

        JPanel southWrapper = new JPanel();
        southWrapper.setLayout(new BoxLayout(southWrapper, BoxLayout.Y_AXIS));
        southWrapper.setBackground(BG_DARK);
        southWrapper.add(makeSeparator());
        southWrapper.add(statsPanel);
        southWrapper.add(makeSeparator());
        southWrapper.add(controlPanel);

        add(mapPanel,     BorderLayout.CENTER);
        add(infoPanel,    BorderLayout.EAST);
        add(southWrapper, BorderLayout.SOUTH);

        autoTimer = new javax.swing.Timer(timerDelay, e -> stepGame());

        pack();
        setLocationRelativeTo(null);
    }

    private JSeparator makeSeparator() {
        JSeparator sep = new JSeparator();
        sep.setForeground(BORDER_COLOR);
        sep.setBackground(BG_DARK);
        return sep;
    }

    private void toggleTimer() {
        if (running) {
            autoTimer.stop();
            running = false;
            playPauseBtn.setText("▶  Play");
        } else {
            autoTimer.start();
            running = true;
            playPauseBtn.setText("⏸  Pause");
        }
    }

    private void stepGame() {
        player.getBrain().makeMove();

        String action = player.getBrain().getLastAction();
        if (action != null && !action.isEmpty()) {
            logModel.addElement(action);
            actionLog.ensureIndexIsVisible(logModel.size() - 1);
        }

        repaint();

        if (player.getCurrentStrength() <= 0 ||
            player.getCurrentFood()     <= 0 ||
            player.getCurrentWater()    <= 0) {
            autoTimer.stop();
            running = false;
            playPauseBtn.setText("▶  Play");
            JOptionPane.showMessageDialog(this,
                "The player ran out of resources and died.\nReached column "
                + player.getColPos() + " / " + (map.getWidth() - 1) + ".\n\nScroll the action log to review what happened.",
                "Game Over", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (player.getColPos() >= map.getWidth() - 1) {
            autoTimer.stop();
            running = false;
            playPauseBtn.setText("▶  Play");
            JOptionPane.showMessageDialog(this,
                "The player crossed the entire map! Victory!\n\nScroll the action log to review the run.",
                "You Win!", JOptionPane.INFORMATION_MESSAGE);
            return;
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

        MapPanel() {
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

            // Clamp viewport so we don't show black when near map edges
            int startRow = playerRow - VIEWPORT_ROWS / 2;
            int startCol = playerCol - VIEWPORT_COLS / 2;
            startRow = Math.max(0, Math.min(map.getHeight() - VIEWPORT_ROWS, startRow));
            startCol = Math.max(0, Math.min(map.getWidth()  - VIEWPORT_COLS, startCol));

            for (int r = 0; r < VIEWPORT_ROWS; r++) {
                for (int c = 0; c < VIEWPORT_COLS; c++) {
                    int mapRow = startRow + r;
                    int mapCol = startCol + c;
                    int px     = c * TILE_SIZE;
                    int py     = r * TILE_SIZE;

                    Square sq = map.getMapSquare(mapRow, mapCol);
                    if (sq == null) {
                        g2.setColor(Color.BLACK);
                        g2.fillRect(px, py, TILE_SIZE, TILE_SIZE);
                        continue;
                    }

                    // Terrain
                    g2.setColor(getTerrainColor(sq.getTerrain()));
                    g2.fillRect(px, py, TILE_SIZE, TILE_SIZE);

                    // Subtle grid lines
                    g2.setColor(new Color(0, 0, 0, 30));
                    g2.drawRect(px, py, TILE_SIZE, TILE_SIZE);

                    // Item dot
                    Item item = sq.getItem();
                    if (item != null) {
                        g2.setColor(item instanceof Trader ? COLOR_TRADER : COLOR_ITEM);
                        int dot = TILE_SIZE / 3;
                        g2.fillOval(px + (TILE_SIZE - dot) / 2, py + (TILE_SIZE - dot) / 2, dot, dot);
                    }

                    // Player
                    if (mapRow == playerRow && mapCol == playerCol) {
                        int margin = 4;
                        g2.setColor(COLOR_PLAYER);
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

        StatsPanel() {
            setPreferredSize(new Dimension(VIEWPORT_COLS * TILE_SIZE, STATS_HEIGHT));
            setBackground(BG_PANEL);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // --- Resource bars (left column) ---
            int barW  = 160;
            int barH  = 14;
            int barsX = 16;
            int barsY = 18;
            int gap   = 28;

            drawBar(g2, barsX, barsY,         barW, barH, "Strength",
                    player.getCurrentStrength(), player.getMaxStrength(), new Color(220, 70, 70));
            drawBar(g2, barsX, barsY + gap,   barW, barH, "Water",
                    player.getCurrentWater(), player.getMaxWater(), new Color(70, 150, 220));
            drawBar(g2, barsX, barsY + gap*2, barW, barH, "Food",
                    player.getCurrentFood(), player.getMaxFood(), new Color(130, 195, 100));

            // --- Gold + Position (middle column) ---
            int midX = barsX + 70 + barW + 28;
            g2.setFont(new Font("Monospaced", Font.BOLD, 13));
            g2.setColor(COLOR_ITEM);
            g2.drawString("Gold: " + player.getCurrentGold(), midX, barsY + 11);

            g2.setFont(new Font("Monospaced", Font.PLAIN, 11));
            g2.setColor(TEXT_LABEL);
            g2.drawString("Pos:  (" + player.getRowPos() + ", " + player.getColPos() + ")", midX, barsY + gap + 11);
            g2.drawString("Goal: col " + (map.getWidth() - 1), midX, barsY + gap*2 + 11);

            // --- Legend (right column) ---
            drawLegend(g2, midX + 155, barsY - 4);
        }

        private void drawBar(Graphics2D g2, int x, int y, int w, int h,
                             String label, int current, int max, Color fill) {
            int labelW = 62;

            // Label
            g2.setColor(TEXT_DIM);
            g2.setFont(new Font("Monospaced", Font.PLAIN, 11));
            g2.drawString(label, x, y + h - 1);

            int bx = x + labelW;

            // Track
            g2.setColor(new Color(50, 50, 60));
            g2.fillRoundRect(bx, y, w, h, 6, 6);

            // Fill
            float ratio = Math.max(0f, Math.min(1f, (float) current / max));
            g2.setColor(fill);
            int filled = (int)(w * ratio);
            if (filled > 0) g2.fillRoundRect(bx, y, filled, h, 6, 6);

            // Border
            g2.setColor(new Color(80, 80, 95));
            g2.drawRoundRect(bx, y, w, h, 6, 6);

            // Value text
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Monospaced", Font.BOLD, 9));
            String val = current + "/" + max;
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(val, bx + (w - fm.stringWidth(val)) / 2, y + h - 2);
        }

        private void drawLegend(Graphics2D g2, int x, int y) {
            Color[]  colors = { COLOR_PLAINS, COLOR_MOUNTAIN, COLOR_DESERT, COLOR_FOREST,
                                COLOR_SWAMP,  COLOR_ITEM,     COLOR_TRADER };
            String[] labels = { "Plains", "Mountain", "Desert", "Forest",
                                "Swamp",  "Item",     "Trader" };

            g2.setFont(new Font("Monospaced", Font.PLAIN, 10));
            FontMetrics fm = g2.getFontMetrics();

            for (int i = 0; i < labels.length; i++) {
                int col = i / 4;
                int row = i % 4;
                int lx  = x + col * 90;
                int ly  = y + row * 20;

                g2.setColor(colors[i]);
                g2.fillRoundRect(lx, ly, 11, 11, 3, 3);
                g2.setColor(new Color(80, 80, 95));
                g2.drawRoundRect(lx, ly, 11, 11, 3, 3);

                g2.setColor(TEXT_LABEL);
                g2.drawString(labels[i], lx + 15, ly + 10);
            }
        }
    }

    // -------------------------------------------------------
    // Info Panel (east side)
    // -------------------------------------------------------
    private class InfoPanel extends JPanel {

        InfoPanel(String brainLabel, String visionLabel) {
            setPreferredSize(new Dimension(INFO_WIDTH, VIEWPORT_ROWS * TILE_SIZE));
            setBackground(BG_PANEL);
            setLayout(new BorderLayout(0, 0));
            setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, BORDER_COLOR));

            // --- Header labels ---
            JPanel header = new JPanel(new GridLayout(4, 1, 0, 2));
            header.setBackground(BG_PANEL);
            header.setBorder(BorderFactory.createEmptyBorder(14, 14, 10, 14));

            JLabel brainTitle = new JLabel("BRAIN TYPE");
            style(brainTitle, TEXT_DIM, 9, Font.BOLD);

            JLabel brainVal = new JLabel(brainLabel);
            style(brainVal, Color.CYAN, 14, Font.BOLD);

            JLabel visionTitle = new JLabel("VISION TYPE");
            style(visionTitle, TEXT_DIM, 9, Font.BOLD);

            JLabel visionVal = new JLabel(visionLabel);
            style(visionVal, new Color(255, 220, 60), 14, Font.BOLD);

            header.add(brainTitle);
            header.add(brainVal);
            header.add(visionTitle);
            header.add(visionVal);

            // --- Action log ---
            actionLog.setBackground(BG_LOG);
            actionLog.setForeground(new Color(200, 210, 200));
            actionLog.setFont(new Font("Monospaced", Font.PLAIN, 11));
            actionLog.setFixedCellHeight(20);
            actionLog.setBorder(BorderFactory.createEmptyBorder(2, 6, 2, 6));
            actionLog.setSelectionBackground(new Color(50, 60, 80));

            JScrollPane scroll = new JScrollPane(actionLog,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            scroll.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_COLOR));
            scroll.getViewport().setBackground(BG_LOG);

            JLabel logTitle = new JLabel("  ACTION LOG");
            style(logTitle, TEXT_DIM, 9, Font.BOLD);
            logTitle.setBackground(BG_PANEL);
            logTitle.setOpaque(true);
            logTitle.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 0));

            JPanel logWrapper = new JPanel(new BorderLayout());
            logWrapper.setBackground(BG_PANEL);
            logWrapper.add(logTitle, BorderLayout.NORTH);
            logWrapper.add(scroll, BorderLayout.CENTER);

            // --- Footer: Reset + Close ---
            JPanel footer = new JPanel(new GridLayout(1, 2, 6, 0));
            footer.setBackground(BG_PANEL);
            footer.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));

            JButton resetBtn = new JButton("↺ Reset");
            resetBtn.setFont(new Font("Monospaced", Font.BOLD, 11));
            resetBtn.setFocusPainted(false);
            resetBtn.addActionListener(e -> {
                autoTimer.stop();
                running = false;
                dispose();
                SwingUtilities.invokeLater(onReset);
            });

            JButton closeBtn = new JButton("✕ Close");
            closeBtn.setFont(new Font("Monospaced", Font.BOLD, 11));
            closeBtn.setFocusPainted(false);
            closeBtn.addActionListener(e -> System.exit(0));

            footer.add(resetBtn);
            footer.add(closeBtn);

            add(header,     BorderLayout.NORTH);
            add(logWrapper, BorderLayout.CENTER);
            add(footer,     BorderLayout.SOUTH);
        }

        private void style(JLabel lbl, Color color, int size, int style) {
            lbl.setForeground(color);
            lbl.setFont(new Font("Monospaced", style, size));
        }
    }

    // -------------------------------------------------------
    // Control Panel (south strip)
    // -------------------------------------------------------
    private class ControlPanel extends JPanel {

        ControlPanel() {
            setPreferredSize(new Dimension(VIEWPORT_COLS * TILE_SIZE, CONTROL_HEIGHT));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, CONTROL_HEIGHT));
            setBackground(BG_DARK);
            setLayout(new FlowLayout(FlowLayout.LEFT, 14, 8));

            playPauseBtn = new JButton("▶  Play");
            playPauseBtn.setFont(new Font("Monospaced", Font.BOLD, 12));
            playPauseBtn.setFocusPainted(false);
            playPauseBtn.addActionListener(e -> toggleTimer());
            add(playPauseBtn);

            JSeparator div = new JSeparator(JSeparator.VERTICAL);
            div.setPreferredSize(new Dimension(1, 22));
            div.setForeground(BORDER_COLOR);
            add(div);

            JLabel speedLbl = new JLabel("Speed");
            speedLbl.setForeground(TEXT_DIM);
            speedLbl.setFont(new Font("Monospaced", Font.PLAIN, 11));
            add(speedLbl);

            JLabel fastLbl = new JLabel("Fast");
            fastLbl.setForeground(TEXT_DIM);
            fastLbl.setFont(new Font("Monospaced", Font.PLAIN, 10));
            add(fastLbl);

            JSlider speed = new JSlider(JSlider.HORIZONTAL, 50, 1000, timerDelay);
            speed.setInverted(true);
            speed.setPreferredSize(new Dimension(160, 26));
            speed.setBackground(BG_DARK);
            speed.setFocusable(false);
            speed.addChangeListener(e -> {
                timerDelay = speed.getValue();
                if (autoTimer != null) autoTimer.setDelay(timerDelay);
            });
            add(speed);

            JLabel slowLbl = new JLabel("Slow");
            slowLbl.setForeground(TEXT_DIM);
            slowLbl.setFont(new Font("Monospaced", Font.PLAIN, 10));
            add(slowLbl);
        }
    }
}
