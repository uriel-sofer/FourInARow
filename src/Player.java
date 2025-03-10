abstract public class Player extends Thread{

    protected char symbol;//X or O
    protected final Game game;
    protected String name;
    protected int totalGames;
    protected int wins;
    protected int losses;

    protected boolean waiting;



    public Player(char symbol, Game game, String name) {
        this.symbol = symbol;
        this.game = game;
        this.name = name;
        this.wins = 0;
        this.losses = 0;
        totalGames = StatisticsHandler.getTotalGames(name);
    }

    @Override
    public void run() {
        while (!game.isGameOver()) {
            game.waitForTurn(this);

            if (game.isGameOver()) break; // Handle game end during wait

            makeMove();

            if (endMove()) break;

            game.switchTurn();
        }
        game.switchTurn();
        System.out.println(name + " has finished their game.");
    }

    /**
     * Increment wins for self, increment losses for other
     */
    public void updateWinsAndLosses() {
        this.wins++;
        for (Player p : game.getPlayers()) {
            if (!p.equals(this)) {
                p.losses++;
            }
        }
    }

    protected abstract void makeMove();

    /**
     * Checks the result of the move
     * @return True if filled the board or if won
     */
    public boolean endMove() {
        boolean result = game.gameRules.checkGameOver(this);
        if (result) {
            if (game.gameRules.checkWin(symbol)){
                win();
            }
            else {
                game.getGrid().printGrid();
                System.out.println("Tie!");
            }
            return true;
        }
        else {
            game.getGrid().printGrid();
        }
        return false;
    }

    /**
     * Do winner things: update wins and losses, brag to the screen and print the won grid
     */
    public void win() {
        this.updateWinsAndLosses();
        System.out.println(name + " won! total wins: " + wins);
        game.getGrid().printWonGrid(game.gameRules.getWinSeq());
    }

    public char getSymbol() {
        return symbol;
    }

    public Game getGame() {
        return game;
    }

    public String getPlayerName() {
        return name;
    }

    public int getTotalGames() {
        return totalGames;
    }

    public void setTotalGames(int totalGames) {
        this.totalGames = totalGames;
    }

}