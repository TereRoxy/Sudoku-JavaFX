package model.grid;

public interface IGrid {
    int[][] getGrid();
    String toJSON();
    void setGrid(int[][] grid);
    int[][] fromJSON(String grid);
}
