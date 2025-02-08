package model.grid;

import java.util.StringTokenizer;

public abstract class Grid implements IGrid {
    protected int[][] grid;

    public Grid() {
        grid = new int[9][9];
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                grid[i][j] = 0;
            }
        }
    }

    public int[][] getGrid() {
        return grid;
    }

    public void setGrid(int[][] grid) {
        this.grid = grid;
    }

    public String toXML() {
        StringBuilder sb = new StringBuilder();
        sb.append("<grid>\n");
        for (int[] row : grid) {
            sb.append("  <row>");
            for (int cell : row) {
                sb.append(cell).append(" ");
            }
            sb.deleteCharAt(sb.length() - 1); // Remove trailing space
            sb.append("</row>\n");
        }
        sb.append("</grid>\n");
        return sb.toString();
    }

    public int[][] fromXML(String xml) {
        int[][] newGrid = new int[9][9];
        StringTokenizer st = new StringTokenizer(xml, " \n");
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (st.hasMoreTokens()) {
                    newGrid[i][j] = Integer.parseInt(st.nextToken().trim());
                } else {
                    throw new IllegalArgumentException("Invalid XML format: not enough tokens");
                }
            }
        }
        this.grid = newGrid;
        return newGrid;
    }

    protected boolean isValid(int[][] grid, int row, int col, int num) {
        for (int i = 0; i < 9; i++) {
            if (grid[row][i] == num || grid[i][col] == num) {
                return false;
            }
        }
        int startRow = row - row % 3;
        int startCol = col - col % 3;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (grid[startRow + i][startCol + j] == num) {
                    return false;
                }
            }
        }
        return true;
    }

    public abstract void populate();
}