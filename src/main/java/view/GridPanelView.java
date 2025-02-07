package view;

import javafx.beans.property.IntegerProperty;
import javafx.scene.Cursor;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.util.converter.NumberStringConverter;
import model.entity.Puzzle;

public class GridPanelView {

    private Puzzle puzzle;
    private final MainWindowController mainWindowController;

    //TODO: refactor

    public GridPanelView(Puzzle puzzle, MainWindowController mainWindowController) {
        this.puzzle = puzzle;
        this.mainWindowController = mainWindowController;
        populateGrid();
    }

    public void setPuzzle(Puzzle puzzle) {
        this.puzzle = puzzle;
        resetGrid();
    }

    private void populateGrid() {
        GridPane sudokuGrid = mainWindowController.getSudokuGrid();
        int[][] completedGrid = puzzle.getCompletedGrid().getGrid();
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                int value = completedGrid[row][col];
                TextField cell = new TextField();

                IntegerProperty cellProperty = puzzle.getCellProperty(row, col);

                // Bind TextField to Puzzle cell
                cell.textProperty().bindBidirectional(cellProperty, new NumberStringConverter() {
                    @Override
                    public String toString(Number value) {
                        // Convert 0 to an empty string
                        return (value == null || value.intValue() == 0) ? "" : value.toString();
                    }

                    @Override
                    public Number fromString(String string) {
                        // Convert empty string to 0
                        return string.isEmpty() ? 0 : Integer.parseInt(string);
                    }
                });

                // Add listener to cellProperty
                int finalRow = row;
                int finalCol = col;
                cellProperty.addListener(
                        (_,
                         oldValue,
                         newValue) -> System.out.println("Cell [" + finalRow + "," + finalCol + "] changed from " + oldValue + " to " + newValue));

                if (value != 0) {

                    cell.setText(String.valueOf(value));
                    cell.setEditable(false);
                    cell.setFont(javafx.scene.text.Font.font("Courier New Bold", 20));
                    cell.setStyle("-fx-background-color: transparent");
                } else {
                    cell.setText("");
                    cell.setEditable(true);
                    cell.setFont(javafx.scene.text.Font.font("Courier New", 20));
                    cell.setStyle("-fx-background-color: transparent");
                }
                cell.setPrefSize(40, 40);
                cell.setMaxSize(40, 40);
                cell.setMinSize(40, 40);
                cell.setAlignment(javafx.geometry.Pos.CENTER);
                GridPane.setHalignment(cell, javafx.geometry.HPos.CENTER);
                GridPane.setValignment(cell, javafx.geometry.VPos.CENTER);
                cell.setCursor(Cursor.CROSSHAIR);
                cell.setOnKeyPressed(mainWindowController::handleArrowKeys);
                cell.setOnKeyReleased(mainWindowController::handleCellInput);
                sudokuGrid.setGridLinesVisible(true);
                sudokuGrid.add(cell, finalCol, finalRow);

            }
        }
    }

    private void resetGrid() {
        GridPane sudokuGrid = mainWindowController.getSudokuGrid();
        int[][] completedGrid = puzzle.getCompletedGrid().getGrid();

        //update the cells properties and text
        sudokuGrid.getChildren().forEach(node -> {
            if (node instanceof TextField cell) {
                int row = GridPane.getRowIndex(cell);
                int col = GridPane.getColumnIndex(cell);
                int value = completedGrid[row][col];
                IntegerProperty cellProperty = puzzle.getCellProperty(row, col);
                cell.textProperty().bindBidirectional(cellProperty, new NumberStringConverter() {
                    @Override
                    public String toString(Number value) {
                        return (value == null || value.intValue() == 0) ? "" : value.toString();
                    }

                    @Override
                    public Number fromString(String string) {
                        return string.isEmpty() ? 0 : Integer.parseInt(string);
                    }
                });
                if (value != 0) {
                    cell.setText(String.valueOf(value));
                    cell.setEditable(false);
                    cell.setFont(javafx.scene.text.Font.font("Courier New Bold", 20));
                    cell.setStyle("-fx-background-color: transparent");
                } else {
                    cell.setText("");
                    cell.setEditable(true);
                    cell.setFont(javafx.scene.text.Font.font("Courier New", 20));
                    cell.setStyle("-fx-background-color: transparent");
                }
            }
        });

    }

    public void showSolution() {
        int[][] solutionGrid = puzzle.getSolutionGrid().getGrid();
        int[][] completedGrid = puzzle.getCompletedGrid().getGrid();
        GridPane sudokuGrid = mainWindowController.getSudokuGrid();
        sudokuGrid.getChildren().forEach(node -> {
            if (node instanceof TextField cell) {
                int row = GridPane.getRowIndex(cell);
                int col = GridPane.getColumnIndex(cell);
                int value = solutionGrid[row][col];
                cell.setText(String.valueOf(value));

                cell.setEditable(false);
                cell.setFont(javafx.scene.text.Font.font("Courier New Bold", 20));
                if (completedGrid[row][col] != value) {
                    cell.setStyle("-fx-background-color: transparent; -fx-text-fill: #ff0084;");
                } else {
                    cell.setStyle("-fx-background-color: transparent;");
                }
            }
        });
    }
}