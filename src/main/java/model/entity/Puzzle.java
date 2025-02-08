package model.entity;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import model.grid.CompletedGrid;
import model.grid.PuzzleGrid;
import model.grid.SolutionGrid;
import org.w3c.dom.Element;


public class Puzzle {
    private final SolutionGrid solutionGrid;
    private final CompletedGrid completedGrid;
    private boolean isSolved;
    private boolean pencilMode;
    private String name;
    private String dateTime; // Date and time of creation/last modification
    private final SimpleIntegerProperty availableHints = new SimpleIntegerProperty(0);


    public Puzzle() {
        this.solutionGrid = new SolutionGrid();
        this.completedGrid = new CompletedGrid(new PuzzleGrid());
        this.isSolved = false;
        this.pencilMode = false;
        this.name = "New Game";

        // Create dateTime String from current date and time
        this.dateTime = java.time.LocalDate.now() +
                " " +
                java.time.LocalTime.now();
    }

    public Puzzle(PuzzleGrid pg, SolutionGrid sg, String name) {
        this.solutionGrid = sg;
        this.completedGrid = new CompletedGrid(pg);
        this.isSolved = false;
        this.pencilMode = false;
        this.name = name;

        // Create dateTime String from current date and time
        this.dateTime = java.time.LocalDate.now() +
                " " +
                java.time.LocalTime.now();
        this.availableHints.set(completedGrid.getPuzzleGrid().getNrHints());
    }

    public IntegerProperty getCellProperty(int row, int col) {
        return completedGrid.getCellProperty(row, col);
    }

    public void reset() {
        completedGrid.populate();
        isSolved = false;
        pencilMode = false;
    }

    public void addCell(int row, int col, int value) {
        completedGrid.setCell(row, col, value);
    }

//    public boolean isSolved() {
//        int[][] puzzle = completedGrid.getPuzzleGrid().getGrid();
//        int[][] solution = solutionGrid.getGrid();
//        for (int i = 0; i < 9; i++) {
//            for (int j = 0; j < 9; j++) {
//                if (puzzle[i][j] != solution[i][j]) {
//                    return false;
//                }
//            }
//        }
//        return true;
//    }

    public String saveState() {
        return "<puzzle>\n" +
                "  <puzzle_grid>\n" + completedGrid.getPuzzleGrid().toXML() + "  </puzzle_grid>\n" +
                "  <solution_grid>\n" + solutionGrid.toXML() + "  </solution_grid>\n" +
                "  <completed_grid>\n" + completedGrid.toXML() + "  </completed_grid>\n" +
                "  <is_solved>" + isSolved + "</is_solved>\n" +
                "  <pencil_mode>" + pencilMode + "</pencil_mode>\n" +
                "  <name>" + name + "</name>\n" +
                "  <date_time>" + dateTime + "</date_time>\n" +
                "  <available_hints>" + availableHints.get() + "</available_hints>\n" +
                "</puzzle>\n";
    }

    public void loadState(Element element) {
        completedGrid.getPuzzleGrid().setGrid(parseGrid(element, "puzzle_grid"));
        solutionGrid.setGrid(parseGrid(element, "solution_grid"));
        completedGrid.setGrid(parseGrid(element, "completed_grid"));
        isSolved = Boolean.parseBoolean(getElementText(element, "is_solved").trim());
        pencilMode = Boolean.parseBoolean(getElementText(element, "pencil_mode").trim());
        name = getElementText(element, "name");
        dateTime = getElementText(element, "date_time");
        availableHints.set(Integer.parseInt(getElementText(element, "available_hints").trim()));
    }

    private int[][] parseGrid(Element parent, String tagName) {
        Element gridElement = (Element) parent.getElementsByTagName(tagName).item(0);
        return completedGrid.fromXML(gridElement.getTextContent());
    }

    public void giveHint(){
        if (availableHints.get() == 0){
            throw new IllegalStateException("No more hints available");
        }

        int [][] completedGrid = this.completedGrid.getGrid();
        int [][] solutionGrid = this.solutionGrid.getGrid();

        while (true){
            int row = (int) (Math.random() * 9);
            int col = (int) (Math.random() * 9);
            if (completedGrid[row][col] == 0){
                this.completedGrid.setCell(row, col, solutionGrid[row][col]);
                break;
            }
        }

        this.availableHints.set(availableHints.get() - 1);
    }

    private String getElementText(Element parent, String tagName) {
        return parent.getElementsByTagName(tagName).item(0).getTextContent();
    }

    // Getters and setters for the fields
    public SolutionGrid getSolutionGrid() { return solutionGrid; }
    public CompletedGrid getCompletedGrid() { return completedGrid; }
    public int getCell(int row, int col) { return completedGrid.getCell(row, col); }
    public int getAvailableHints(){ return availableHints.get(); }

    public void setName(String name) { this.name = name; }
    public void setDateTime() {
        this.dateTime = java.time.LocalDate.now() +
                " " +
                java.time.LocalTime.now();
    }

    public String toString() {
        return "Name: " + name + "\n" +
                "Datetime: " + dateTime + "\n";
    }

    public IntegerProperty availableHintsProperty() {
        return availableHints;
    }
}