import javax.swing.*;

public class GameWindow {
    public GameWindow() {
        JFrame frame = new JFrame("4 in a Row");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

        Game game = new Game(3); // Start a Player-vs-Player game
        GamePanel panel = new GamePanel(game);
        frame.add(panel);
        frame.pack();

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        new GameWindow();
    }
}
