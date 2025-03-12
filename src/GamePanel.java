import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class GamePanel extends JPanel {
    private final Game game;
    private final Grid grid;
    private final GameRules rules;

    public GamePanel(Game game) {
        this.game = game;
        this.grid = game.getGrid();  // Get Grid from Game
        this.rules = game.gameRules; // Get GameRules from Game

        int width = grid.getCols() * 80;  // 80px per column
        int height = grid.getRows() * 80; // 80px per row

        setPreferredSize(new Dimension(width, height));
        setBackground(Color.BLUE);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int col = e.getX() / 80;
                dropToken(col);
            }
        });
    }

    public void dropToken(int col) {
        try {
            int row = grid.findEmptyRowInColumn(col);
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Column full! Choose another.");
                return;
            }

            grid.updateGrid(game.getCurrentPlayer().getSymbol(), col);
            repaint();

            grid.printGrid();
            System.out.println();

            if (rules.checkGameOver(game.getCurrentPlayer())) {
                JOptionPane.showMessageDialog(this, game.getCurrentPlayer().getSymbol() + " Wins!");
                grid.clear();
            } else if (grid.isGridFull()) {
                JOptionPane.showMessageDialog(this, "It's a draw!");
                grid.clear();
            }

            game.switchTurn();
        } catch (FullCollumnException ex) {
            JOptionPane.showMessageDialog(this, "Column full! Choose another.");
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw empty board slots
        g.setColor(Color.WHITE);
        for (int row = 0; row < grid.getRows(); row++) {
            for (int col = 0; col < grid.getCols(); col++) {
                int x = col * 80 + 10;
                int y = (grid.getRows() - row - 1) * 80 + 10; // Flip row!
                g.fillOval(x, y, 60, 60);
            }
        }

        // Draw tokens
        for (int row = 0; row < grid.getRows(); row++) {
            for (int col = 0; col < grid.getCols(); col++) {
                Character cell = grid.getBoard()[row][col];
                if (cell != null) {
                    g.setColor(cell == 'X' ? Color.RED : Color.YELLOW);
                    int x = col * 80 + 10;
                    int y = (grid.getRows() - row - 1) * 80 + 10; // Flip row!
                    g.fillOval(x, y, 60, 60);
                }
            }
        }
    }

}
