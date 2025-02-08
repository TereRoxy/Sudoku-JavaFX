package view;

import javafx.beans.property.IntegerProperty;
import javafx.scene.Cursor;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.GridPane;
import javafx.util.converter.NumberStringConverter;
import model.entity.Puzzle;

public class GridPanelView {

    private Puzzle puzzle;
    private final MainWindowController mainWindowController;

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
                TextField cell = createCell(row, col, completedGrid[row][col]);
                sudokuGrid.add(cell, col, row);
            }
        }
    }

    private TextField createCell(int row, int col, int value) {
        TextField cell = new TextField();
        configureCell(cell, row, col, value);
        return cell;
    }

    private void configureCell(TextField cell, int row, int col, int value) {
        cell.setTextFormatter(createTextFormatter());
        bindCellToPuzzle(cell, row, col);
        styleCell(cell, value);
        setCellProperties(cell);
        addCellListeners(cell, row, col);
    }

    private TextFormatter<String> createTextFormatter() {
        return new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            return newText.matches("[1-9]?") ? change : null;
        });
    }

    private void bindCellToPuzzle(TextField cell, int row, int col) {
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
    }

    private void styleCell(TextField cell, int value) {
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

    private void setCellProperties(TextField cell) {
        cell.setPrefSize(40, 40);
        cell.setMaxSize(40, 40);
        cell.setMinSize(40, 40);
        cell.setAlignment(javafx.geometry.Pos.CENTER);
        cell.setCursor(Cursor.CROSSHAIR);
    }

    private void addCellListeners(TextField cell, int row, int col) {
        cell.setOnKeyPressed(mainWindowController::handleArrowKeys);
        cell.setOnKeyReleased(mainWindowController::handleCellInput);
        puzzle.getCellProperty(row, col).addListener((_, oldValue, newValue) ->
                System.out.println("Cell [" + row + "," + col + "] changed from " + oldValue + " to " + newValue));
    }

    private void resetGrid() {
        GridPane sudokuGrid = mainWindowController.getSudokuGrid();
        int[][] completedGrid = puzzle.getCompletedGrid().getGrid();
        sudokuGrid.getChildren().forEach(node -> {
            if (node instanceof TextField cell) {
                int row = GridPane.getRowIndex(cell);
                int col = GridPane.getColumnIndex(cell);
                configureCell(cell, row, col, completedGrid[row][col]);
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
                cell.setEditable(false);
                cell.setFont(javafx.scene.text.Font.font("Courier New Bold", 20));
                if (completedGrid[row][col] != value) {
                    cell.setStyle("-fx-background-color: transparent; -fx-text-fill: #ff0084;");
                } else {
                    cell.setStyle("-fx-background-color: transparent;");
                }
                cell.setText(String.valueOf(value));
            }
        });
    }
}