import java.awt.*;
import javax.swing.*;

public class Test {
    public static void main(String[] args) throws Exception {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        launch();
    }

    public static void launch() {
        JComboBox<String> diffBox = new JComboBox<>(new String[]{
            "1 - Easy (50x50)",
            "2 - Hard (100x100)"
        });
        JComboBox<String> brainBox = new JComboBox<>(new String[]{
            "A - East-Focused    (always pushes east, survives only if critical)",
            "B - Survival-First  (keeps resources topped up, then heads east)",
            "C - Trader-Seeker   (seeks traders when rich in gold, east secondary)",
            "D - Balanced        (east priority, detours for resources at 40%)"
        });
        JComboBox<String> visionBox = new JComboBox<>(new String[]{
            "A - Focused         (2 tiles deep ahead, slight spread)",
            "B - Eyes Peeled     (1 tile sides + 2 tiles ahead)",
            "C - Far Sighted     (looks 3 tiles ahead in a cone)",
            "D - Cross Eyed      (wide diagonal spread)"
        });

        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(new JLabel("Difficulty:"));  panel.add(diffBox);
        panel.add(new JLabel("Brain Type:"));  panel.add(brainBox);
        panel.add(new JLabel("Vision Type:")); panel.add(visionBox);

        int result = JOptionPane.showConfirmDialog(
            null, panel, "Survival Game — Setup",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) System.exit(0);

        int    diff       = diffBox.getSelectedIndex() + 1;
        String brainType  = brainBox.getSelectedItem().toString().substring(0, 1);
        String visionType = visionBox.getSelectedItem().toString().substring(0, 1);

        gameMap newMap = new gameMap(diff * 50, diff * 50, diff);

        int startRow = (diff * 50) / 2;
        Player player = new Player(newMap, startRow, 0, brainType, visionType);

        Display display = new Display(newMap, player, Test::launch);
        display.render();
    }
}
