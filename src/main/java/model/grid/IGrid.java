package model.grid;

public interface IGrid {
    int[][] getGrid();
    String toXML();
    void setGrid(int[][] grid);
    int[][] fromXML(String grid);
}
