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

        setPreferredSize(new Dimension(grid.getCols() * 80, grid.getRows() * 80));
        setBackground(Color.BLUE);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int col = e.getX() / 80; // Get column from click
                try {
                    grid.updateGrid(game.getCurrentPlayer().getSymbol(), col);  // Drop token

                    if (rules.checkGameOver(game.getCurrentPlayer())) { // Use GameRules to check win
                        JOptionPane.showMessageDialog(GamePanel.this, game.getCurrentPlayer().getSymbol() + " Wins!");
                        grid.clear(); // Reset grid
                    } else if (grid.isGridFull()) {
                        JOptionPane.showMessageDialog(GamePanel.this, "It's a draw!");
                        grid.clear();
                    }

                    game.switchTurn(); // Switch player turn
                    repaint();  // Redraw board
                } catch (FullCollumnException ex) {
                    JOptionPane.showMessageDialog(GamePanel.this, "Column full! Choose another.");
                }
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(Color.WHITE);
        for (int row = 0; row < grid.getRows(); row++) {
            for (int col = 0; col < grid.getCols(); col++) {
                int x = col * 80 + 10;
                int y = row * 80 + 10;
                g.fillOval(x, y, 60, 60);
            }
        }

        for (int row = 0; row < grid.getRows(); row++) {
            for (int col = 0; col < grid.getCols(); col++) {
                Character cell = grid.getBoard()[row][col];
                if (cell != null) {
                    g.setColor(cell == 'X' ? Color.RED : Color.YELLOW);
                    g.fillOval(col * 80 + 10, row * 80 + 10, 60, 60);
                }
            }
        }
    }
}
