package model.entity;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import model.grid.CompletedGrid;
import model.grid.IGrid;
import model.grid.PuzzleGrid;
import model.grid.SolutionGrid;

import java.util.StringTokenizer;

public class Puzzle {
    private SolutionGrid solutionGrid;
    private CompletedGrid completedGrid;
    private boolean isSolved;
    private boolean pencilMode;
    private String name;
    private String dateTime;
    private SimpleIntegerProperty availableHints = new SimpleIntegerProperty(0);

    //TODO: A puzzle state should aggregate a PuzzleGrid, a SolutionGrid and a List of entries
    //TODO: A puzzle state should have a method to check if the puzzle is solved
    //TODO: A puzzle state should have a method to save the current state to a string
    //TODO: A puzzle state should have a method to load a state from a string
    //State --> PuzzleGrid, SolutionGrid, isSolved, pencilMode(?)
    // save, load, display state, display solution, reset, reverse(undo), redo
    // name, date and time of creation/last modification

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

    public boolean isSolved() {
        int[][] puzzle = completedGrid.getPuzzleGrid().getGrid();
        int[][] solution = solutionGrid.getGrid();
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (puzzle[i][j] != solution[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }

    public String saveState() {
        return "{\n" +
                "  \"puzzle_grid\": " + formatGrid(completedGrid.getPuzzleGrid()) + ",\n" +
                "  \"solution_grid\": " + formatGrid(solutionGrid) + ",\n" +
                "  \"completed_grid\": " + formatGrid(completedGrid) + ",\n" +
                "  \"is_solved\": " + isSolved + ",\n" +
                "  \"pencil_mode\": " + pencilMode + ",\n" +
                "  \"name\": \"" + name + "\",\n" +
                "  \"date_time\": \"" + dateTime + "\",\n" +
                "  \"available_hints\": " + availableHints.get() + "\n" +
                "}";
    }

    private String formatGrid(IGrid grid) {
        String json = grid.toJSON();
        return json.replace("],[", "],\n    [");
    }

    public void loadState(String state) {
        StringTokenizer st = new StringTokenizer(state, "{},\n\": ");
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            switch (token) {
                case "puzzle_grid" -> {
                    int[][] puzzleGrid = completedGrid.getPuzzleGrid().fromJSON(st.nextToken());
                    completedGrid.setGrid(puzzleGrid);
                }
                case "solution_grid" -> {
                    int[][] solutionGrid = this.solutionGrid.fromJSON(st.nextToken());
                    this.solutionGrid.setGrid(solutionGrid);
                }
                case "completed_grid" -> {
                    int[][] completedGrid = this.completedGrid.fromJSON(st.nextToken());
                    this.completedGrid.setGrid(completedGrid);
                }
                case "is_solved" -> isSolved = Boolean.parseBoolean(st.nextToken());
                case "pencil_mode" -> pencilMode = Boolean.parseBoolean(st.nextToken());
                case "name" -> name = st.nextToken();
                case "date_time" -> dateTime = st.nextToken();
                case "available_hints" -> availableHints.set(Integer.parseInt(st.nextToken()));
            }
        }
    }


    // Getters and setters for the fields
    public SolutionGrid getSolutionGrid() { return solutionGrid; }
    public CompletedGrid getCompletedGrid() { return completedGrid; }
//    public boolean getIsSolved() { return isSolved; }
//    public boolean getPencilMode() { return pencilMode; }
//    public String getName() { return name; }
//    public String getDateTime() { return dateTime; }

    public void setName(String name) { this.name = name; }
//    public void setSolutionGrid(SolutionGrid sg) { solutionGrid = sg; }
//    public void setCompletedGrid(CompletedGrid cg) { completedGrid = cg; }
//    public void setIsSolved(boolean solved) { isSolved = solved; }
//    public void setPencilMode(boolean pencil) { pencilMode = pencil; }
//    public void setDateTime() {
//        this.dateTime = java.time.LocalDate.now() +
//                " " +
//                java.time.LocalTime.now();
//    }

//    public Puzzle deepCopy() {
//        Puzzle copy = new Puzzle();
//        copy.setSolutionGrid(solutionGrid.deepCopy());
//        copy.setCompletedGrid(completedGrid.deepCopy());
//        copy.setIsSolved(isSolved);
//        copy.setPencilMode(pencilMode);
//        copy.setName(name);
//        copy.setDateTime();
//        copy.setAvailableHints(availableHints.get());
//        return copy;
//    }

    public int getCell(int row, int col) {
        return completedGrid.getCell(row, col);
    }

    public String toString() {
        return "Name: " + name + "\n" +
                "Datetime: " + dateTime + "\n";
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
//    public void setAvailableHints(int availableHints){
//        this.availableHints.set(availableHints);
//    }
    public int getAvailableHints(){
        return availableHints.get();
    }

    public IntegerProperty availableHintsProperty() {
        return availableHints;
    }
}