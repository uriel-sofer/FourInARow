public class GameRules {
    private final Game game;
    private final Coordinates winSeq;

    public Coordinates getWinSeq() {
        return winSeq;
    }

    public GameRules(Game game) {
        this.game = game;
        this.winSeq = new Coordinates();
    }

    //Due to a change in the machine's logic this one is unnecessary
    /**
     * Checks all the columns for winning possibility
     * No need anymore because changed the machine's logic at the end
     * @return true if there is a move that can win the game, false otherwise.
     */

    public boolean isThereWinningMove(Character symbol) {
        //Column check
        for (int col = 0; col < game.getGrid().getCols(); col++) {
            if (oneLeftToWin(game.getGrid().getColumn(col), symbol)) {
                return true;
            }
        }

        //Row check
        for (int row = 0; row < game.getGrid().getRows(); row++) {
            if (oneLeftToWin(game.getGrid().getRow(row), symbol)) {
                return true;
            }
        }

        //Diagonals check
        for (int col = 0; col < game.getGrid().getCols(); col++) {
            if (oneLeftToWin(game.getGrid().getDiagonalFromLeft(col), symbol) || oneLeftToWin(game.getGrid().getDiagonalFromRight(col), symbol)) {
                return true;
            }
        }

        // No winning move found
        return false;
    }

    /**
     * Checks if there is one move left to win in the given line (column, row, or diagonal).
     * @param line The line being checked for a possible win.
     * @param symbol The player's symbol ('X' or 'O').
     * @return true if there is a possible win in the line, false otherwise.
     */
    public boolean oneLeftToWin(Character[] line, Character symbol) {
        int charCounter = 0;
        int emptyCount = 0;

        for (Character character : line) {
            if (character != null && character.equals(symbol)) {
                charCounter++;
            } else if (character == null) {
                emptyCount++;
            } else {
                // Non-matching character found, reset counters
                charCounter = 0;
                emptyCount = 0;
            }

            // Check if there's a sequence with one gap (e.g., O _ O) or contiguous symbols with one empty space
            if (charCounter == game.getNumToWin() - 1 && emptyCount == 1) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns true if player wins or if the grid is full
     * @param player the winner in question, just finished his turn
     * @return true if it was a winning move
     */
    public boolean checkGameOver(Player player) {
        game.setGameOver(checkWin(player.getSymbol()) | game.getGrid().isGridFull());
        return game.isGameOver();
    }


    /**
     * Checks if symbol won
     * @param symbol The player's symbol ('X' or 'O').
     * @return true if the player has won, false otherwise.
     */
    public boolean checkWin(Character symbol) {
        // Rows
        for (int row = 0; row < game.getGrid().getRows(); row++) {
            if (hasWinningSequence(game.getGrid().getRow(row), symbol, 'r', row, 0)) {
                return true;
            }
        }
        // Columns
        for (int col = 0; col < game.getGrid().getCols(); col++) {
            if (hasWinningSequence(game.getGrid().getColumn(col), symbol, 'c', 0, col)) {
                return true;
            }
        }
        // Diagonals
        for (int col = 0; col < game.getGrid().getCols(); col++) {
            if (hasWinningSequence(game.getGrid().getDiagonalFromLeft(col), symbol, 'd', 0, col) ||
                    hasWinningSequence(game.getGrid().getDiagonalFromRight(col), symbol, 'd', 0, col)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if a given line contains a winning sequence of symbols.
     *
     * @param line           The line (row, column, or diagonal) being checked.
     * @param symbol         The player's symbol ('X' or 'O').
     * @param lineIdentifier row, column or diagonal. needed for saving coordinates
     * @param startRow       needed for coordinates
     * @param startCol       needed for coordinates
     * @return true if the line contains a winning sequence, false otherwise.
     */
    private boolean hasWinningSequence(Character[] line, Character symbol, char lineIdentifier, int startRow, int startCol) {
        int count = 0;
        int firstIndex = -1, lastIndex;

        for (int i = 0; i < line.length; i++) {
            if (line[i] != null && line[i].equals(symbol)) {
                count++;
                if (count == 1) {
                    firstIndex = i;
                }
                if (count == game.getNumToWin()) {
                    lastIndex = i;

                    // Save the winning sequence coordinates
                    saveWinningSequence(firstIndex, lastIndex, lineIdentifier, startRow, startCol);

                    return true;
                }
            } else {
                count = 0;
                firstIndex = -1;
            }
        }
        return false;
    }

    /**
     * Save the start and end coordinates of the winning sequence.
     * This method updates the `winSeq` object with the coordinates of the start and end points of a winning sequence,
     * depending on whether the sequence is row-based, column-based, or diagonal-based.
     *
     * @param firstIndex The index of the first character in the winning sequence within the line (row, column, or diagonal).
     * @param lastIndex The index of the last character in the winning sequence within the line (row, column, or diagonal).
     * @param lineIdentifier A character that identifies the type of line being checked:
     *                       'r' for row, 'c' for column, and 'd' for diagonal.
     * @param startRow The starting row position for the sequence. Used as the fixed row for row-based sequences or as a reference for diagonal sequences.
     * @param startCol The starting column position for the sequence. Used as the fixed column for column-based sequences or as a reference for diagonal sequences.
     */

    private void saveWinningSequence(int firstIndex, int lastIndex, char lineIdentifier, int startRow, int startCol) {
        if (lineIdentifier == 'r') {
            // Row-based sequence
            winSeq.setStartX(startRow);
            winSeq.setStartY(firstIndex);
            winSeq.setEndX(startRow);
            winSeq.setEndY(lastIndex);
        } else if (lineIdentifier == 'c') {
            // Column-based sequence
            winSeq.setStartX(firstIndex);
            winSeq.setStartY(startCol);
            winSeq.setEndX(lastIndex);
            winSeq.setEndY(startCol);
        } else if (lineIdentifier == 'd') {
            // Diagonal-based sequence
            winSeq.setStartX(startRow + firstIndex);
            winSeq.setStartY(startCol + firstIndex);
            winSeq.setEndX(startRow + lastIndex);
            winSeq.setEndY(startCol + lastIndex);
        }
    }


}
