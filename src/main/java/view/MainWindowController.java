package view;

import controller.PuzzleController;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Window;
import javafx.util.Duration;
import model.entity.Puzzle;
import repository.IRepository;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class MainWindowController {

    @FXML
    private Label timerLabel;
    @FXML
    private MenuButton newGameBtn;
    @FXML
    private Button saveGameBtn;
    @FXML
    private Button loadGameBtn;
    @FXML
    private Button undoBtn;
    @FXML
    private Button redoBtn;
    @FXML
    private Button resetBtn;
    @FXML
    private Button showHintBtn;
    @FXML
    private Label availableHintsLabel;
    @FXML
    private Button checkSolutionBtn;
    @FXML
    private Button themesBtn;
    @FXML
    private Button aboutBtn;
    @FXML
    private Button exitBtn;
    @FXML
    private GridPane sudokuGrid;
    @FXML
    private HBox numberButtons;

    private PuzzleController puzzleController;
    private IRepository repository;
    private Timeline timer;
    private int secondsElapsed;
    private TextField lastFocused;
    private GridPanelView gridPanelView;

    public MainWindowController() {}

    public void setRepository(IRepository repository) {
        this.repository = repository;
    }

    public void setPuzzleController(PuzzleController puzzleController) {
        this.puzzleController = puzzleController;
    }

    @FXML
    private void initialize() {
        setupEventHandlers();
        setupTimer();
    }

    private void setupEventHandlers() {
        newGameBtn.getItems().forEach(item -> item.setOnAction(_ -> handleNewGame(item)));
        saveGameBtn.setOnAction(_ -> handleSave());
        loadGameBtn.setOnAction(_ -> handleLoad());
        undoBtn.setOnAction(_ -> handleUndo());
        redoBtn.setOnAction(_ -> handleRedo());
        resetBtn.setOnAction(_ -> handleReset());
        showHintBtn.setOnAction(_ -> handleShowHint());
        checkSolutionBtn.setOnAction(_ -> handleCheckSolution());
        themesBtn.setOnAction(_ -> handleThemes());
        aboutBtn.setOnAction(_ -> handleAbout());
        exitBtn.setOnAction(_ -> handleExit());
        numberButtons.getChildren().forEach(button -> button.setOnMouseClicked(this::handleButtonInput));
    }

    public void setupGrid() {
        gridPanelView = new GridPanelView(puzzleController.getPuzzle(), this);
        addFocusListenersToGrid();
        setupAvailableHintsListener();
    }

    private void addFocusListenersToGrid() {
        sudokuGrid.getChildren().forEach(node -> {
            if (node instanceof TextField textField) {
                textField.focusedProperty().addListener((_, _, newValue) -> {
                    if (newValue) {
                        lastFocused = textField;
                    }
                });
            }
        });
    }

    private void setupAvailableHintsListener() {
        puzzleController.getPuzzle().availableHintsProperty().addListener((_, _, newValue) -> {
            availableHintsLabel.setText("Available Hints: " + newValue);
            showHintBtn.setDisable(newValue.intValue() == 0);
        });
        availableHintsLabel.setText("Available Hints: " + puzzleController.getPuzzle().getAvailableHints());
    }

    private void setupTimer() {
        timer = new Timeline(new KeyFrame(Duration.seconds(1), _ -> updateTimer()));
        timer.setCycleCount(Timeline.INDEFINITE);
        startTimer();
    }

    private void updateTimer() {
        secondsElapsed++;
        int minutes = secondsElapsed / 60;
        int seconds = secondsElapsed % 60;
        timerLabel.setText(String.format("%02d:%02d", minutes, seconds));
    }

    public void startTimer() {
        secondsElapsed = 0;
        timer.play();
    }

    public void stopTimer() {
        timer.stop();
    }

    private void handleNewGame(MenuItem item) {
        String difficulty = item.getText();
        startNewGameWithDifficulty(difficulty);
        gridPanelView.setPuzzle(puzzleController.getPuzzle());
        enableGameButtons();
        setupAvailableHintsListener();
        startTimer();
    }

    private void startNewGameWithDifficulty(String difficulty) {
        switch (difficulty) {
            case "Novice":
                puzzleController.startNewGame(1);
                break;
            case "Easy":
                puzzleController.startNewGame(2);
                break;
            case "Medium":
                puzzleController.startNewGame(3);
                break;
            case "Hard":
                puzzleController.startNewGame(4);
                break;
            case "Very Hard":
                puzzleController.startNewGame(5);
                break;
        }
    }

    private void enableGameButtons() {
        undoBtn.setDisable(false);
        redoBtn.setDisable(false);
        resetBtn.setDisable(false);
        showHintBtn.setDisable(false);
    }

    private void handleSave() {
        try {
            repository.load();
        } catch (IllegalArgumentException ignored) {}
        Window window = saveGameBtn.getScene().getWindow();
        TextInputDialog dialog = new TextInputDialog();
        dialog.initOwner(window);
        dialog.setTitle("Save Game");
        dialog.setHeaderText("Enter the name of the saved game");
        dialog.setContentText("Name:");
        dialog.showAndWait().ifPresent(name -> {
            puzzleController.updatePuzzle(name);
            repository.addPuzzleState(puzzleController.getPuzzle());
            repository.save();
        });
    }

    private void handleLoad() {
        try {
            repository.load();
            Window window = loadGameBtn.getScene().getWindow();
            ChoiceDialog<Puzzle> dialog = new ChoiceDialog<>();
            dialog.initOwner(window);
            dialog.setTitle("Load Game");
            dialog.setHeaderText("Select a saved game to load");
            dialog.setContentText("Saved Games:");
            dialog.getItems().addAll(repository.getPuzzleStates());
            dialog.showAndWait().ifPresent(puzzle -> {
                puzzleController.setPuzzle(puzzle);
                gridPanelView.setPuzzle(puzzle);
                setupGrid();
            });
        } catch (IllegalArgumentException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
        }
    }

    private void handleUndo() {
        try {
            puzzleController.undo();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
        }
    }

    private void handleRedo() {
        try {
            puzzleController.redo();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
        }
    }

    private void handleReset() {
        puzzleController.reset();
    }

    private void handleShowHint() {
        puzzleController.giveHint();
        availableHintsLabel.setText("Available Hints: " + puzzleController.getPuzzle().getAvailableHints());
    }

    private void handleCheckSolution() {
        gridPanelView.showSolution();
        disableGameButtons();
        stopTimer();
    }

    private void disableGameButtons() {
        undoBtn.setDisable(true);
        redoBtn.setDisable(true);
        resetBtn.setDisable(true);
        showHintBtn.setDisable(true);
    }

    private void handleThemes() {
        // Handle themes
    }

    private void handleAbout() {
        Window window = aboutBtn.getScene().getWindow();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.initOwner(window);
        alert.setTitle("About");
        alert.setHeaderText("Sudoku Game");
        alert.setContentText(readAboutFile());
        alert.resizableProperty().setValue(true);
        alert.getDialogPane().setPrefWidth(700);
        alert.getDialogPane().setPrefHeight(600);
        alert.showAndWait();
    }

    private String readAboutFile() {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader("about.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } catch (FileNotFoundException e) {
            return "The about.txt file could not be found";
        } catch (IOException e) {
            return "An error occurred while reading the about.txt file";
        }
        return sb.toString();
    }

    private void handleExit() {
        Window window = exitBtn.getScene().getWindow();
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initOwner(window);
        alert.setTitle("Exit");
        alert.setHeaderText("Are you sure you want to exit? Any unsaved progress will be lost.");
        alert.setContentText("Choose your option:");
        alert.getButtonTypes().setAll(
                new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE),
                new ButtonType("Save"),
                new ButtonType("Exit")
        );
        alert.showAndWait().ifPresent(buttonType -> {
            if (buttonType.getText().equals("Save")) {
                handleSave();
                System.exit(0);
            } else if (buttonType.getText().equals("Exit")) {
                System.exit(0);
            }
        });
    }

    private void handleButtonInput(Event event) {
        Button source = (Button) event.getSource();
        if (source.getText().equals("X")) {
            if (lastFocused != null) {
                lastFocused.clear();
            }
            return;
        }
        int value = Integer.parseInt(source.getText());
        if (lastFocused != null && lastFocused.isEditable()) {
            int row = GridPane.getRowIndex(lastFocused);
            int col = GridPane.getColumnIndex(lastFocused);
            lastFocused.setText(String.valueOf(value));
            lastFocused.requestFocus();
            puzzleController.addCell(row, col, value);
        }
    }

    public void handleArrowKeys(KeyEvent event) {
        TextField source = (TextField) event.getSource();
        int row = GridPane.getRowIndex(source);
        int col = GridPane.getColumnIndex(source);
        switch (event.getCode()) {
            case UP -> moveFocus(row - 1, col);
            case DOWN -> moveFocus(row + 1, col);
            case LEFT -> moveFocus(row, col - 1);
            case RIGHT -> moveFocus(row, col + 1);
            default -> {}
        }
    }

    private void moveFocus(int row, int col) {
        if (row >= 0 && row < 9 && col >= 0 && col < 9) {
            sudokuGrid.getChildren().get(row * 9 + col + 1).requestFocus();
        }
    }

    public void handleCellInput(Event event) {
        TextField source = (TextField) event.getSource();
        source.requestFocus();
        int row = GridPane.getRowIndex(source);
        int col = GridPane.getColumnIndex(source);
        if (source.getText().isEmpty() || !source.getText().matches("[0-9]")) {
            source.clear();
            event.consume();
            return;
        }
        if (source.isEditable()) {
            int value = Integer.parseInt(source.getText());
            puzzleController.addCell(row, col, value);
        }
    }

    public GridPane getSudokuGrid() {
        return sudokuGrid;
    }
}