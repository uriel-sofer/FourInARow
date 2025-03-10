import java.util.Scanner;

public class HumanPlayer extends Player{

    public HumanPlayer(char symbol, Game game, String name) {
        super(symbol, game, name);
    }

    /**
     * Prompts the user for selecting a column, validates input and drops the "token"
     */
    @Override
    public void makeMove() {
        Scanner scanner = new Scanner(System.in);
        int col;
        while (true) {
            System.out.println(name + "(" + symbol + ")" + ", enter the column number to drop your token:");
            game.getGrid().printGrid();

            while (!scanner.hasNextInt()) {
                System.out.println("Invalid input. Please enter a valid column number:");
                scanner.next();
            }

            col = scanner.nextInt();

            if (col < 1 || col > game.getGrid().getCols()) {
                System.out.println("Invalid column. Please choose a column between 1 and " + game.getGrid().getCols());
                continue;  //Back to first prompt
            }

            try {
                game.getGrid().updateGrid(symbol, col - 1); //col - 1 so when user enters 1 it goes into zero column
                break;
            } catch (FullCollumnException e) {
                System.err.println(e.getMessage() + " Please choose a different column");
            }
        }
    }

}