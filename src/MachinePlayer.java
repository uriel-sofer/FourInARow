import java.util.Random;

public class MachinePlayer extends Player{

    public MachinePlayer(Character symbol, Game game) {
        super(symbol, game, "Machine");
    }

    /*
    Bug explanation:
    after I adjusted oneMoreToWin() to detect gaps it used to make a winning move without actually making a move (the turns switched but the grid didn't update)
    Now machine tries to win everytime if possible, and if not then makeRandomMove()
     */
    /**
     * For machinePlayer: wins the game, or plays a random move
     */
    @Override
    public void makeMove() {
        try {
            Thread.sleep(500);  //Add delay for machine's move
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        boolean noWinningMove = makeWinningMove();

        if (noWinningMove) {
            makeRandomMove();
        }
    }
    /**
     * Makes the winning move
     * @return true if no move happened, false if everything ok (and the machine won)
     */
    protected boolean makeWinningMove() {
        for (int col = 0; col < game.getGrid().getCols(); col++) {
            try {
                int emptyRow = game.getGrid().findEmptyRowInColumn(col);
                if (emptyRow != -1) {

                    game.getGrid().updateGrid(symbol, col);

                    //Check if this move results in a win
                    if (game.gameRules.checkGameOver(this)) {
                        System.out.println("MachinePlayer made a winning move in column " + (col + 1));
                        return false;  //Exit after making the winning move
                    }

                    //Undo the move if it doesn't lead to a win
                    game.getGrid().getBoard()[emptyRow][col] = null;
                }
            } catch (FullCollumnException e) {
                //No biggie try the next column
            }
        }
        return true;
    }

    protected void makeRandomMove() {
        Random random = new Random();
        int col;
        while (true) {
            col = random.nextInt(game.getGrid().getCols());
            try {
                game.getGrid().updateGrid(symbol, col);
                System.out.println("MachinePlayer made a random move in column " + (col + 1));
                break;
            } catch (FullCollumnException e) {
                //No biggie try the next column
            }
        }
    }
}