public class SmartMachinePlayer extends MachinePlayer{

    private final char opponentSymbol;

    public SmartMachinePlayer(Character symbol, Game game) {
        super(symbol, game);
        name = "Smart Machine";
        opponentSymbol = (symbol == 'X') ? 'O' : 'X';
    }

    @Override
    public void makeMove() {
        try {
            Thread.sleep(500);  //Add delay for machine's move
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        //Check if the machine can win with the next move
        if (!makeWinningMove()) {
            return;
        }

        //Check if the opponent can win and block that move
        if (!blockOpponentWinningMove()) {
            return;
        }

        //If no strategic move is found, make a random move
        makeRandomMove();
    }

    /**
     * Blocks opponent's winning move if possible.
     * @return true if no blocking move was made, false if blocked successfully.
     */
    private boolean blockOpponentWinningMove() {
        Player opponent = null;
        for (Player player : game.getPlayers()) {
            if (player.getSymbol() == opponentSymbol) {
                opponent = player;
                break;
            }
        }

        if (opponent == null)
            return false;

        for (int col = 0; col < game.getGrid().getCols(); col++) {
            int emptyRow = game.getGrid().findEmptyRowInColumn(col);
            if (emptyRow != -1) {

                //Temporarily place the opponent's symbol in the grid
                Character originalValue = game.getGrid().getBoard()[emptyRow][col];
                game.getGrid().getBoard()[emptyRow][col] = opponentSymbol;

                // Check if this would cause the opponent to win
                if (game.gameRules.checkGameOver(opponent)) {
                    //Block the opponent by placing the machine's symbol instead
                    game.getGrid().getBoard()[emptyRow][col] = symbol;
                    System.out.println("MachinePlayer blocked opponent in column " + (col + 1));
                    return false;
                }

                //Undo the temporary move by restoring the original value
                game.getGrid().getBoard()[emptyRow][col] = originalValue;
            }
        }
        return true;
    }
}
