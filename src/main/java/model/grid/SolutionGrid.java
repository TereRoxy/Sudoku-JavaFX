package model.grid;

import java.util.StringTokenizer;

public class SolutionGrid implements IGrid {
    private int[][] grid;

    public SolutionGrid(){
        grid = new int[9][9];
        for(int i = 0; i < 9; i++){
            for(int j = 0; j < 9; j++){
                grid[i][j] = 0;
            }
        }
        populate();
    }

    private void generateSeed(){
        int[] seed = new int[9];
        for(int i = 1; i <= 9; i++){
            seed[i - 1] = i;
        }
        for(int i = 0; i < 9; i++){
            //randomly swap the current element with another element
            int index = (int)(Math.random() * 9);
            int temp = seed[i];
            seed[i] = seed[index];
            seed[index] = temp;
        }
        System.arraycopy(seed, 0, grid[0], 0, 9);
    }

    private boolean solve() {
        for (int row = 1; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                if (grid[row][col] == 0) {
                    for (int num = 1; num <= 9; num++) {
                        if (isValid(row, col, num)) {
                            grid[row][col] = num;
                            if (solve()) {
                                return true;
                            }
                            grid[row][col] = 0;
                        }
                    }
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isValid(int row, int col, int num) {
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

    private void populate(){
        generateSeed();
        solve();
    }

    @Override
    public int[][] getGrid() {
        return grid;
    }

    @Override
    public String toXML() {
        StringBuilder sb = new StringBuilder();
        sb.append("    <grid>\n");
        for (int[] row : grid) {
            sb.append("      <row>");
            for (int cell : row) {
                sb.append(cell).append(" ");
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

    @Override
    public void setGrid(int[][] grid) {
        this.grid = grid;
    }

//    public SolutionGrid deepCopy() {
//        SolutionGrid sg = new SolutionGrid();
//        sg.setGrid(grid);
//        return sg;
//    }

}
