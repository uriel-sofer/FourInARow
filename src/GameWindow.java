import javax.swing.*;

public class GameWindow extends JFrame {
    public GameWindow() {
        setTitle("4 in a Row");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setSize(600, 600);


        setContentPane(new GameMenu(this));
        setLocationRelativeTo(null);
        setVisible(true);
    }
}
