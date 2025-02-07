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

    public IntegerProperty getCellProperty(int row, int col) {
        return grid[row][col]; // Return the IntegerProperty for binding
    }


//    @Override
//    public String toJSON(){
//        StringBuilder sb = new StringBuilder();
//        sb.append("[\n");
//        for(int i = 0; i < 9; i++){
//            sb.append("\t[");
//            for(int j = 0; j < 9; j++){
//                sb.append(grid[i][j].get());
//                if(j < 8){
//                    sb.append(", ");
//                }
//            }
//            sb.append("]");
//            if(i < 8){
//                sb.append(",");
//            }
//            sb.append("\n");
//        }
//        sb.append("]");
//        return sb.toString();
//    }
//
//    @Override
//    public int[][] fromJSON(String grid) {
//        StringTokenizer st = new StringTokenizer(grid, "[],\n");
//        int[][] newGrid = new int[9][9];
//        for (int i = 0; i < 9; i++) {
//            for (int j = 0; j < 9; j++) {
//                newGrid[i][j] = Integer.parseInt(st.nextToken());
//            }
//        }
//        return newGrid;
//    }

//    @Override
//    public String toJSON() {
//        StringBuilder sb = new StringBuilder();
//        sb.append("[\n");
//        for (int i = 0; i < 9; i++) {
//            sb.append("  [");
//            for (int j = 0; j < 9; j++) {
//                sb.append(grid[i][j]);
//                if (j < 8) sb.append(", ");
//            }
//            sb.append("]");
//            if (i < 8) sb.append(",\n");
//        }
//        sb.append("\n]");
//        return sb.toString();
//    }
//
//    @Override
//    public int[][] fromJSON(String json) {
//        int[][] newGrid = new int[9][9];
//        StringTokenizer st = new StringTokenizer(json, "[], \n");
//        for (int i = 0; i < 9; i++) {
//            for (int j = 0; j < 9; j++) {
//                if (st.hasMoreTokens()) {
//                    newGrid[i][j] = Integer.parseInt(st.nextToken());
//                }
//            }
//        }
//        return newGrid;
//    }

    @Override
    public String toJSON() {
        StringBuilder sb = new StringBuilder();
        sb.append("[\n");
        for (int i = 0; i < 9; i++) {
            sb.append("  [");
            for (int j = 0; j < 9; j++) {
                sb.append(grid[i][j].get());
                if (j < 8) sb.append(", ");
            }
            sb.append("]");
            sb.append(i < 8 ? ",\n" : "\n");
        }
        sb.append("]");
        return sb.toString();
    }

    @Override
    public int[][] fromJSON(String json) {
        int[][] newGrid = new int[9][9];
        StringTokenizer st = new StringTokenizer(json, "[], \n");
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (st.hasMoreTokens()) {
                    newGrid[i][j] = Integer.parseInt(st.nextToken());
                }
            }
        }
        return newGrid;
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
