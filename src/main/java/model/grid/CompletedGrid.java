package model.grid;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

import java.util.StringTokenizer;

public class CompletedGrid implements IGrid {
    private IntegerProperty[][] grid; // Use IntegerProperty instead of int
    private PuzzleGrid pg;

    public CompletedGrid(PuzzleGrid pg) {
        this.pg = pg;
        grid = new IntegerProperty[9][9];
        int[][] initialGrid = pg.getGrid();
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                grid[i][j] = new SimpleIntegerProperty(initialGrid[i][j]); // Initialize with values from PuzzleGrid
            }
        }
    }

    @Override
    public int[][] getGrid() {
        int[][] intGrid = new int[9][9];
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                intGrid[i][j] = grid[i][j].get(); // Convert IntegerProperty to int
            }
        }
        return intGrid;
    }

    @Override
    public void setGrid(int[][] grid) {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                this.grid[i][j].set(grid[i][j]); // Set values from int array
            }
        }
    }

    @Override
    public String toXML() {
        StringBuilder sb = new StringBuilder();
        sb.append("    <grid>\n");
        for (IntegerProperty[] row : grid) {
            sb.append("      <row>");
            for (IntegerProperty cell : row) {
                sb.append(cell.get()).append(" ");
            }
            sb.deleteCharAt(sb.length() - 1); // Remove trailing space
            sb.append("</row>\n");
        }
        sb.append("    </grid>\n");
        return sb.toString();
    }

    @Override
    public int[][] fromXML(String xml) {
        int[][] newGrid = new int[9][9];
        String[] rows = xml.trim().split("\n");

        for (int i = 0; i < 9; i++) {
            String row = rows[i].replace("<row>", "").replace("</row>", "").trim();
            String[] cells = row.split(" ");
            for (int j = 0; j < 9; j++) {
                newGrid[i][j] = Integer.parseInt(cells[j]);
            }
        }
        return newGrid;
    }

    public IntegerProperty getCellProperty(int row, int col) {
        return grid[row][col]; // Return the IntegerProperty for binding
    }


    public void populate() {
        int[][] puzzleGrid = pg.getGrid();
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                grid[i][j].set(puzzleGrid[i][j]); // Populate with values from PuzzleGrid
            }
        }
    }

//    public CompletedGrid deepCopy() {
//        CompletedGrid cg = new CompletedGrid(pg);
//        int[][] newGrid = new int[9][9];
//        for (int i = 0; i < 9; i++) {
//            for (int j = 0; j < 9; j++) {
//                newGrid[i][j] = grid[i][j].get(); // Get value from IntegerProperty
//            }
//        }
//        cg.setGrid(newGrid);
//        return cg;
//    }

    public void setCell(int row, int col, int value) {
        grid[row][col].set(value); // Set value using IntegerProperty
    }

    public int getCell(int row, int col) {
        return grid[row][col].get(); // Get value from IntegerProperty
    }

    public PuzzleGrid getPuzzleGrid() {
        return pg;
    }
}
