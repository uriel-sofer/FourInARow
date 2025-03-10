import java.io.*;
import java.util.Scanner;


public class Game {

    private final Grid grid;
    private final int numToWin;

    private Player currentPlayer;
    private final Player[] players = new Player[2];

    private boolean isGameOver = false;
    private boolean isSessionOver = false;
    private boolean isAnotherRoundCalled = false;
    private boolean anotherRoundInProgress = false;

    //Avoiding "Magic Numbers":
    static final int NUM_MENU_OPTIONS = 3; //Number of options in main menu
    static final int MIN_COL = 2, MIN_ROW = 1;

    public final GameRules gameRules = new GameRules(this); //gameRules contains all the functions necessary for the game to operate as well as a reference to the game itself


    /**
     * Default constructor, must use setPlayers method to actually start the game (otherwise who starts?)
     * decided to go private because there is no Game without this option (needs to know which game mode)
     */
    private Game() {
        Scanner scanner = new Scanner(System.in);
        int rows = getRowCount(scanner);
        int cols = getColumnCount(scanner);
        grid = new Grid(rows, cols);
        numToWin = getWinningLineLength(scanner, Math.min(cols, rows));
    }

    /**
     * Creates a costume 4-in-a-row Game object
     * @param option Game mode option from the main menu:
     *               1 - Human first, Machine second
     *               2 - Machine first, Human second
     *               3 - Two human players, first name goes first
     */
    public Game(int option) {
        this();
        setPlayers(option);
    }

    /**
     * Sets and initializes the Players objects: who starts, their names, and their symbols.
     * @param option Game mode option from the main menu:
     *               1 - Human first, Machine second
     *               2 - Machine first, Human second
     *               3 - Two human players, first name goes first
     */
    private void setPlayers(int option) {
        Scanner scanner = new Scanner(System.in);
        final int HUMAN_FIRST = 1, HUMAN_SECOND = 2;
        if (option == HUMAN_FIRST) {
            players[0] = new HumanPlayer('X', this, getNameForHumanPlayer(scanner));
            players[1] = new SmartMachinePlayer('O', this);
        }
        else if (option == HUMAN_SECOND) {
            players[0] = new SmartMachinePlayer('X', this);
            players[1] = new HumanPlayer('O', this, getNameForHumanPlayer(scanner));
        }
        else {
            //Two Human Players
            players[0] = new HumanPlayer('X', this, getNameForHumanPlayer(scanner));
            players[1] = new HumanPlayer('O', this, getNameForHumanPlayer(scanner));
        }
        currentPlayer = players[0];
    }

    public static void main(String[] args) {

        if (args.length < 1) {
            System.err.println("Usage: java FourInARow <history_file>");
            System.exit(1);
        }

        //Statistics
        String fileName = args[0];
        File pastGames = new File(fileName);
        if (!pastGames.exists() || !pastGames.isFile()) {
            System.err.println("Error: File not found or not a valid file: " + fileName);
            System.exit(1);
        }
        //Show initial stats
        StatisticsHandler.initStatisticsHandler(pastGames);
        StatisticsHandler.printStatsFromFile();

        /*
        The constructor handles the three game mode as well as the steps later:
        Choose game mode (1-3)
        grid sizes (columns > 2)
        win size (win size <= min(columns, rows))
        human players names
         */
        Scanner scanner = new Scanner(System.in);
        Game game = new Game(mainMenu(scanner));

        //Adding the players to the DB
        StatisticsHandler.addPlayers(game.players);

        game.play(); //Gameplay loop

        /*
        The win/Loss is updated in the players object during the game's rounds
        This updates their total games played (still not writing to the DB)
         */
        game.incPlayersGames();

        //This the DB object, not file
        StatisticsHandler.updateTotalGames(game.players); //Update the list

        //Writes to the file
        StatisticsHandler.writeToFile(pastGames);
        StatisticsHandler.printStatsFromFile();
    }

    /**
     * Each player makes a move until someone wins or the board is full. When over, another round is called
     */
    private void play() {
        while (!isSessionOver) {
            isGameOver = false;
            currentPlayer = players[0];
            System.out.println("Starting new game...");

            // Start new threads for each player for the new round
            Thread player1Thread = new Thread(players[0]);
            Thread player2Thread = new Thread(players[1]);

            player1Thread.start();
            player2Thread.start();

            try {
                player1Thread.join();
                player2Thread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            playAnotherRound();
        }
    }

    /**
     * Handles playing another round: Prompts, switches and resets.
     */
    public synchronized void playAnotherRound() {
        if (anotherRoundInProgress) return;  // Prevent double call
        anotherRoundInProgress = true;
        //System.out.println("Prompting for another round...");

        if (promptAnotherRound()) {
            resetGrid();
            switchStarter();
            isAnotherRoundCalled = true;
            isGameOver = false;
            System.out.println("Another round starting...");
            notifyAll();  // Notify all waiting threads that the game is ready for the next round
        } else {
            isSessionOver = true;
            System.out.println("Session is over.");
            notifyAll();  // Notify all waiting threads that the session is over
            StatisticsHandler.printLocalWinsAndLosses(players);
        }


        anotherRoundInProgress = false;  // Reset flag
    }

    /**
     * Asks the user for another round
     * @return true if user selected Y or y
     */
    private synchronized boolean promptAnotherRound() {
        System.out.println("Would you like to play again? (y/n)");
        Scanner scanner = new Scanner(System.in);
        char ans = scanner.next().charAt(0);
        while (Character.toLowerCase(ans) != 'y' && Character.toLowerCase(ans) != 'n') {
            System.out.println("Invalid answer. Try again.");
            ans = scanner.next().charAt(0);
        }
        return Character.toLowerCase(ans) == 'y';
    }

    /**
     * Changes the order of the array so the second player will start the next round
     */
    private void switchStarter() {
        Player newFirst = players[1];
        players[1] = players[0];
        players[0] = newFirst;
    }

    /**
     * Resets the grid for a new game and starts a new round
     */
    public void resetGrid() {
        grid.clear();
        notifyAll();
    }

    public synchronized void switchTurn() {
        currentPlayer = (currentPlayer == players[0]) ? players[1] : players[0];
        //System.out.println("Switching turn. New current player: " + currentPlayer.getPlayerName());
        notifyAll(); // Notify all threads waiting for their turn
    }

    public synchronized void waitForTurn(Player player) {
        while (currentPlayer != player && !isGameOver) {
            //System.out.println(player.getPlayerName() + " is waiting for their turn...");
            try {
                player.waiting = true;
                wait();  // Wait for turn
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        //System.out.println(player.getPlayerName() + "'s turn has started.");
    }

    /**
     * Adds to the totalGames of both players
     */
    private void incPlayersGames() {
        players[0].totalGames++;
        players[1].totalGames++;
    }

    //******************************


    //******************************
    // Getters and Setters
    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(Player currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public int getNumToWin() {
        return numToWin;
    }

    public Grid getGrid() {
        return grid;
    }

    public boolean isGameOver() {
        return isGameOver;
    }

    public void setGameOver(boolean gameOver) {
        isGameOver = gameOver;
    }

    public boolean isSessionOver() {
        return isSessionOver;
    }

    public boolean isAnotherRoundCalled() {
        return isAnotherRoundCalled;
    }

    public Player[] getPlayers() {
        return players;
    }
    //******************************

    /*Constructor Utilities:
    mainMenu: returns game mode
    getColumnCount and getRowCount: get user input for the grid's dimensions
    getWinningLength: how much in a sequence for a win
    getNameFurHumanPlayer: used to construct Player instancesS
     */
    /**
     * Prints the main menu and returns the player's choice (1-3)
     * @param scanner Scanner for input
     * @return The option number the player has selected
     */
    private static int mainMenu(Scanner scanner) {
        int playerChoice = 0;
        while (true) {
            System.out.println("""
                Welcome to the 4-in-a-Row!:
                Please select game mode:
                1. Player-vs-Machine: Player first
                2. Player-vs-Machine: Machine first
                3. Player-vs-Player
                Your choice:""");
            playerChoice = scanner.nextInt();

            if (!(playerChoice >= 1 && playerChoice <= NUM_MENU_OPTIONS)) {
                System.err.println("Error: Invalid choice: " + playerChoice);
                System.out.println("Choose a number between 1 and 3");
            }
            else
                break;
        }
        return playerChoice;
    }

    /**
     * Handles the column count input from the player.
     * @param scanner Scanner for input
     * @return The column count
     */
    private static int getColumnCount(Scanner scanner) {
        int colCount = 0;
        while (colCount < MIN_COL) {
            System.out.println("Enter column amount: ");
            colCount = scanner.nextInt();
            if (colCount < MIN_COL) {
                System.err.println("Error: Column count must be at least " + MIN_COL + "!");
            }
        }
        return colCount;
    }

    /**
     * Handles the row count (height) input from the player.
     * @param scanner Scanner for input
     * @return The row count (height)
     */
    private static int getRowCount(Scanner scanner) {
        int rowCount = 0;
        while (rowCount < MIN_ROW) {
            System.out.println("Enter row amount: ");
            rowCount = scanner.nextInt();
            if (rowCount < MIN_ROW) {
                System.err.println("Error: Row count must be at least " + MIN_ROW + "!");
            }
        }
        return rowCount;
    }

    /**
     * Handles the input for the number of consecutive characters needed in a row to win the game.
     * Ensures the number is valid (e.g., not greater than the minimum dimension of the grid).
     * @param scanner Scanner for input
     * @param maxLength The maximum allowed value, typically the smaller dimension of the grid
     * @return The number of characters in a row needed to win
     */
    private static int getWinningLineLength(Scanner scanner, int maxLength) {
        int winLength = 0;
        while (winLength < MIN_COL || winLength > maxLength) {
            System.out.println("Enter the number of consecutive characters needed to win (minimum 2): ");
            winLength = scanner.nextInt();
            if (winLength < MIN_COL || winLength > maxLength) {
                System.err.println("Error: The winning line length must be between 2 and " + maxLength + "!");
            }
        }
        return winLength;
    }

    /**
     *
     * Handles naming HumanPlayer
     * @param scanner Scanner for input
     * @return The name of the human player
     */
    private static String getNameForHumanPlayer(Scanner scanner) {
        System.out.println("Enter name for the human player: ");
        return scanner.next();
    }
}