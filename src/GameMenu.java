import javax.swing.*;
import java.awt.*;

public class GameMenu extends JPanel {
    private final JFrame frame;

    public GameMenu(JFrame frame) {
        this.frame = frame;
        setLayout(new GridLayout(4, 1));

        JButton playerVsPlayer = new JButton("Player vs Player");
        JButton playerVsMachine = new JButton("Player vs Machine");
        JButton viewStats = new JButton("View Stats");
        JButton exitButton = new JButton("Exit");

        playerVsPlayer.addActionListener(e -> openGameSetup(3)); // PvP
        playerVsMachine.addActionListener(e -> openGameSetup(1)); // PvM
        viewStats.addActionListener(e -> showStats());
        exitButton.addActionListener(e -> System.exit(0));


        add(playerVsPlayer);
        add(playerVsMachine);
        add(viewStats);
        add(exitButton);
    }


    private void openGameSetup(int mode) {
        SwingUtilities.invokeLater(() -> {
            frame.getContentPane().removeAll();
            frame.setContentPane(new GameSetupMenu(frame, mode));
            frame.revalidate();
            frame.repaint();
        });
    }

    private void showStats() {
        JOptionPane.showMessageDialog(this, "Stats");
    }
}
