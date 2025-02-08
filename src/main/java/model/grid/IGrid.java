package model.grid;

public interface IGrid {
    int[][] getGrid();
    void setGrid(int[][] grid);
    String toXML();
    int[][] fromXML(String xml);
}
