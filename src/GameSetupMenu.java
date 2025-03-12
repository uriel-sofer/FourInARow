import javax.swing.*;
import java.awt.*;

public class GameSetupMenu extends JPanel {
    private final JFrame frame;
    private final int mode;

    public GameSetupMenu(JFrame frame, int mode) {
        this.frame = frame;
        this.mode = mode;
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);


        // Row and Column Input
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("Rows:"), gbc);

        gbc.gridx = 1;
        JTextField rowsField = new JTextField("6", 5);
        add(rowsField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("Columns:"), gbc);

        gbc.gridx = 1;
        JTextField columnsField = new JTextField("7", 5);
        add(columnsField, gbc);

        // Win Condition Input
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(new JLabel("Win Length:"), gbc);

        gbc.gridx = 1;
        JTextField seriesLengthField = new JTextField("4", 5);
        add(seriesLengthField, gbc);

        // Player 1 Name
        gbc.gridx = 0;
        gbc.gridy = 3;
        add(new JLabel("Player 1 Name:"), gbc);

        gbc.gridx = 1;
        JTextField player1NameField = new JTextField("test1", 10);
        add(player1NameField, gbc);

        // Player 2 Name (only in PvP mode)
        JTextField player2NameField = new JTextField("test2", 10);
        if (mode == 3) {
            gbc.gridx = 0;
            gbc.gridy = 4;
            add(new JLabel("Player 2 Name:"), gbc);

            gbc.gridx = 1;
            add(player2NameField, gbc);
        }

        // Make the "Play" button span 2 columns
        JButton playButton = new JButton("Play");
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2; // Span two columns
        gbc.anchor = GridBagConstraints.CENTER; // Center it
        add(playButton, gbc);

        playButton.addActionListener(e -> {
            int rows = Integer.parseInt(rowsField.getText());
            int columns = Integer.parseInt(columnsField.getText());
            int seriesLength = Integer.parseInt(seriesLengthField.getText());
            String player1Name = player1NameField.getText();
            String player2Name = (mode == 3) ? player2NameField.getText() : "Machine";

            startGame(rows, columns, seriesLength, player1Name, player2Name);
        });
    }

    private void startGame(int rows, int columns, int seriesLength, String player1, String player2) {
        SwingUtilities.invokeLater(() -> {
            frame.getContentPane().removeAll();
            frame.setContentPane(new GamePanel(new Game(mode, rows, columns, seriesLength,player1, player2)));

            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.revalidate();
            frame.repaint();
        });
    }
}
