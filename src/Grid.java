public class Grid {
    private Character[][] board;
    private final int rows;
    private final int cols;

    public Grid(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        board = new Character[rows][cols];  // Initialize the grid with null values
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
     * Returns the specified column as an array.
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
     * Returns the specified row as an array.
     * @param row The row index.
     * @return The row as a Character array.
     */
    public Character[] getRow(int row) {
        return board[row];
    }

    /**
     * Returns the diagonal starting from the left.
     * @param startCol The starting column index.
     * @return The diagonal as a Character array.
     */
    public Character[] getDiagonalFromLeft(int startCol) {
        Character[] diagonal = new Character[Math.min(rows, cols)];
        int row = 0;
        for (int col = startCol; col < cols && row < rows; col++) {
            diagonal[row] = board[row][col];
            row++;
        }
        return diagonal;
    }

    /**
     * Returns the diagonal starting from the right.
     * @param startCol The starting column index.
     * @return The diagonal as a Character array.
     */
    public Character[] getDiagonalFromRight(int startCol) {
        Character[] diagonal = new Character[Math.min(rows, cols)];
        int row = 0;
        for (int col = startCol; col >= 0 && row < rows; col--) {
            diagonal[row] = board[row][col];
            row++;
        }
        return diagonal;
    }

     /**
     * Finds the first empty row in the specified column.
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
    public void updateGrid(Character toInsert, int col) throws FullCollumnException {
        int emptyRow = -1;
        for (int row = 0; row < rows; row++) {
            if (board[row][col] == null) {
                emptyRow = row;
                break;
            }
        }

        if (emptyRow != -1) {
            board[emptyRow][col] = toInsert;
        } else {
            throw new FullCollumnException();
        }
    }

    /**
     * Prints the grid for debugging or display purposes.
     */
    public void printGrid() {
        for (int row = 0; row < rows; row++) {
            // Print the top border of each cell (except for the first row)
            if (row > 0) {
                for (int col = 0; col < cols; col++) {
                    System.out.print("____");  // Print the row separator
                    if (col < cols - 1) {
                        System.out.print("_");  // Add extra underscore between cells
                    }
                }
                System.out.println();  // Move to the next line after the row separator
            }
    
            // Print the cells with || between them
            System.out.print("|| ");
            for (int col = 0; col < cols; col++) {
                System.out.print(board[row][col] == null ? " " : board[row][col]);
                System.out.print(" || ");
            }
            System.out.println();  // Move to the next line after the row
        }
    
        // Print the bottom border of the last row
        for (int col = 0; col < cols; col++) {
            System.out.print("____");  // Print the row separator
            if (col < cols - 1) {
                System.out.print("_");  // Add extra underscore between cells
            }
        }
        System.out.println();  // Move to the next line after the row separator
    }
}
