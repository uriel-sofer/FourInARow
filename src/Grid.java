public class Grid {
    private final Character[][] board;
    private final int rows;
    private final int cols;

    private static final String ANSI_BOLD = "\u001B[1m";
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_BLUE = "\u001B[34m";

    public Grid(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        board = new Character[rows][cols];  //Initialize the grid with null values
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public Character[][] getBoard() {
        return board;
    }

    /**
     * Returns the specified column as an array, mostly for win checks
     * @param col The column index.
     * @return The column as a Character array.
     */
    public Character[] getColumn(int col) {
        Character[] column = new Character[rows];
        for (int row = 0; row < rows; row++) {
            column[row] = board[row][col];
        }
        return column;
    }

    /**
     * Returns the specified row as an array, mostly for win checks
     * @param row The row index.
     * @return The row as a Character array.
     */
    public Character[] getRow(int row) {
        return board[row];
    }

    /**
     * Returns the characters in the diagonal starting from the left.
     * @param startCol The starting column index.
     * @return The diagonal as a Character array.
     */
    public Character[] getDiagonalFromLeft(int startCol) {
        Character[] diagonal = new Character[Math.min(rows, cols)];
        int row = 0;
        //run from col at row 0 until you run out of space
        for (int col = startCol; col < cols && row < rows; col++) {
            diagonal[row] = board[row][col];
            row++;
        }
        return diagonal;
    }

    /**
     * Returns the characters in the diagonal starting from the right.
     * @param startCol The starting column index.
     * @return The diagonal as a Character array.
     */
    public Character[] getDiagonalFromRight(int startCol) {
        Character[] diagonal = new Character[Math.min(rows, cols)];
        int row = 0;
        //Run from startCol to the 0-th column or to the last row (not all boards have nice shapes)
        for (int col = startCol; col >= 0 && row < rows; col--) {
            diagonal[row] = board[row][col];
            row++;
        }
        return diagonal;
    }

     /**
     * Finds the first empty row in the specified column (for the next drop).
     * @param col The column index.
     * @return The row index of the first empty row, or -1 if the column is full.
     */
    public int findEmptyRowInColumn(int col) {
        for (int row = 0; row < rows; row++) {
            if (board[row][col] == null) {
                return row;
            }
        }
        return -1;  // No empty row found
    }

    /**
     * "Drops" toInsert into the grid at column col.
     * @param toInsert 'X' or 'O'
     * @param col which column to drop the 'token' into.
     */
    public void updateGrid(Character toInsert, int col) throws FullCollumnException{
        int emptyRow = -1;
        for (int row = 0; row < rows; row++) {
            if (board[row][col] == null) {
                emptyRow = row;
                break;
            }
        }

        if (emptyRow == -1) {
            throw new FullCollumnException();

        } else {
            board[emptyRow][col] = toInsert;
        }
    }

    public boolean isGridFull() {
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                if (board[row][col] == null) {
                    return false;
                }
            }
        }
        return true;
    }
    /**
     * Resets all cells in the grid to null
     */
    public void clear() {
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                board[row][col] = null;  // Clear each cell by setting it to null
            }
        }
    }


    /**
     * Prints a single cell from the grid, in the desired style (bold and color)
     * @param cell The letter in the cell or <b>null</b> for empty space
     * @param isWinningCell Is part of the winning sequence
     */
    private void printCell(Character cell, boolean isWinningCell) {
        if (cell == null) {
            System.out.print("   ");  // Print empty cell
        } else {
            String color = (cell == 'X') ? ANSI_RED : (cell == 'O') ? ANSI_BLUE : ANSI_RESET;
            String output = color + (isWinningCell ? ANSI_BOLD : "") + cell + ANSI_RESET;
            System.out.print(" " + output + " ");
        }
    }

    /**
     * Prints the grid.
     */
    public void printGrid() {
        for (int row = rows - 1; row >= 0; row--) {  //Start from the topmost row
            //Print the cells with | between them
            System.out.print("|");
            for (int col = 0; col < cols; col++) {
                printCell(board[row][col], false);
                if (col < cols - 1) {
                    System.out.print("||");  // Separator for middle columns
                }
            }
            System.out.print(" |");  //End the row with a |
            System.out.println();  //Move to the next line after the row

            //Row Separator
            for (int col = 0; col < cols; col++) {
                System.out.print("-----");  //Print five dashes for each cell floor
            }
            System.out.println();  //Move to the next line after the row separator
        }
    }

    /**
     * Prints the grid and highlights the winning series
     */
    public void printWonGrid(Coordinates coordinates) {
        int startX = coordinates.getStartX();
        int startY = coordinates.getStartY();
        int endX = coordinates.getEndX();
        int endY = coordinates.getEndY();

        //Line Separator to make it clearer
        for (int col = 0; col < cols; col++) {
            System.out.print("*****");
        }
        System.out.println();

        for (int row = rows - 1; row >= 0; row--) {
            System.out.print("| ");
            for (int col = 0; col < cols; col++) {
                //Check if the current cell is part of the winning sequence
                boolean isWinningCell = isPartOfWinningSequence(row, col, startX, startY, endX, endY);
                printCell(board[row][col], isWinningCell);
                System.out.print(" |");
            }
            System.out.println();

            //Print row separator
            for (int col = 0; col < cols; col++) {
                System.out.print("-----");
            }
            System.out.println();
        }
    }

    /**
     * Checks if the char at the given position is part of the winning sequence
     * @param row The row of the won num on the grid
     * @param col The column of the won num on the grid
     * @param startX X Start of winning sequence
     * @param startY Y Start of winning sequence
     * @param endX X End of winning sequence
     * @param endY Y End of winning sequence
     * @return true if the char at grid[row][col] is part of the winning sequence
     */
    private boolean isPartOfWinningSequence(int row, int col, int startX, int startY, int endX, int endY) {
        //Check if the current cell is within the range of the winning sequence
        if (startX == endX) {
            //Horizontal winning sequence
            //Same Y
            return row == startX && col >= startY && col <= endY;
        } else if (startY == endY) {
            //Vertical winning sequence
            //Same X
            return col == startY && row >= startX && row <= endX;
        } else if (Math.abs(startX - endX) == Math.abs(startY - endY)) {
            //Diagonal winning sequence
            //The moving direction is the same
            int dx = (endX > startX) ? 1 : -1;  // Direction for row
            int dy = (endY > startY) ? 1 : -1;  // Direction for col

            for (int i = 0; i <= Math.abs(endX - startX); i++) {
                int currentRow = startX + i * dx;
                int currentCol = startY + i * dy;
                if (row == currentRow && col == currentCol) {
                    return true;
                }
            }
        }
        return false;
    }
}

