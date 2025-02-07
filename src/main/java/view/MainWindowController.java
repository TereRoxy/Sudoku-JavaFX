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

    //TODO: menubar --> new game(difficulty level), save(input name), load*, themes, about, exit*
    //TODO: toolbar --> show hint(remaining hints), solve*, reset*, undo, redo, pencil mode
    //TODO: controls --> grid, number/clear buttons, status bar(elapsed time, remaining cells)
    //print puzzle --> to printer, to pdf, to image, to excel file

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

    PuzzleController puzzleController = null;
    IRepository repository = null;

    Timeline timer = null;
    private int secondsElapsed = 0;
    TextField lastFocused = null;
    GridPanelView gridPanelView = null;

    public MainWindowController() {}

    public void setRepository(IRepository repository) {
        this.repository = repository;
    }

    public void setPuzzleController(PuzzleController puzzleController) {
        this.puzzleController = puzzleController;
    }

    @FXML
    private void initialize() {
        // Initialize the controller and set up event handlers
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

        //link numbered buttons to the handleButtonInput method
        numberButtons.getChildren().forEach(button -> button.setOnMouseClicked(this::handleButtonInput));
        setupTimer();
    }

    public void setupGrid(){
        //initialize and populate the grid
        gridPanelView = new GridPanelView(puzzleController.getPuzzle(), this);
        // Add focus listeners to all TextFields in the GridPane
        for (javafx.scene.Node node : sudokuGrid.getChildren()) {
            if (node instanceof TextField textField) {
                textField.focusedProperty().addListener((_, _, newValue) -> {
                    if (newValue) {
                        lastFocused = textField; // Update the last focused TextField
                    }
                });
            }
        }

        // Create a listener for the number of available hints
        puzzleController.getPuzzle().availableHintsProperty().addListener((_, _, newValue) -> {
            availableHintsLabel.setText("Available Hints: " + newValue);
            showHintBtn.setDisable(newValue.intValue() == 0);
        });

        // Initialize the available hints label
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
        // Handle new game based on difficulty
        String difficulty = item.getText();
        // Call the corresponding function to start a new game with the selected difficulty
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
        gridPanelView.setPuzzle(puzzleController.getPuzzle());
        //restart the timer
        //enable undo/redo buttons
        undoBtn.setDisable(false);
        redoBtn.setDisable(false);
        resetBtn.setDisable(false);
        showHintBtn.setDisable(false);
        // Reset the available hints label and show hint button
        puzzleController.getPuzzle().availableHintsProperty().addListener((_, _, newValue) -> {
            availableHintsLabel.setText("Available Hints: " + newValue);
            showHintBtn.setDisable(newValue.intValue() == 0);
        });

        // Initialize the available hints label
        availableHintsLabel.setText("Available Hints: " + puzzleController.getPuzzle().getAvailableHints());

        startTimer();
    }

    private void handleSave() {
        //open a new window to input the name of the saved game
        // Call the corresponding function to save the current game state
        Window window = saveGameBtn.getScene().getWindow();
        TextInputDialog dialog = new TextInputDialog();
        dialog.initOwner(window);
        dialog.setTitle("Save Game");
        dialog.setHeaderText("Enter the name of the saved game");
        dialog.setContentText("Name:");
        dialog.showAndWait().ifPresent(name -> {
            puzzleController.getPuzzle().setName(name);
            repository.addPuzzleState(puzzleController.getPuzzle());
            repository.save();
        });
    }

    private void handleLoad() {
        // Handle load game --> open a new window with a list view of the saved games from the file
        // Call the corresponding function to load a saved game state
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
        //disable undo/redo buttons
        undoBtn.setDisable(true);
        redoBtn.setDisable(true);
        resetBtn.setDisable(true);
        showHintBtn.setDisable(true);
        //stop the timer
        stopTimer();
    }

    private void handleThemes() {
        // Handle themes
        // Call the corresponding function to change the theme
    }

    private void handleAbout() {
        //open the about window and load the text from the about.txt file
        Window window = aboutBtn.getScene().getWindow();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.initOwner(window);
        alert.setTitle("About");
        alert.setHeaderText("Sudoku Game");

        try{
            FileReader file = new FileReader("about.txt");
            BufferedReader reader = new BufferedReader(file);
            StringBuilder sb = new StringBuilder();
            String line = reader.readLine();
            while (line != null) {
                sb.append(line).append("\n");
                line = reader.readLine();
            }
            alert.setContentText(sb.toString());
        } catch (FileNotFoundException e)
        {
            alert.setContentText("The about.txt file could not be found");
        } catch (IOException e) {
            alert.setContentText("An error occurred while reading the about.txt file");
        }

        alert.resizableProperty().setValue(true);
        alert.getDialogPane().setPrefWidth(700);
        alert.getDialogPane().setPrefHeight(600);
        alert.showAndWait();
    }

    private void handleExit() {
        Window window = exitBtn.getScene().getWindow();
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initOwner(window);
        alert.setTitle("Exit");
        alert.setHeaderText("Are you sure you want to exit? Any unsaved progress will be lost.");
        alert.setContentText("Choose your option:");
        ButtonType save = new ButtonType("Save");
        ButtonType exit = new ButtonType("Exit");
        ButtonType cancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(cancel, save, exit);

        alert.showAndWait().ifPresent(buttonType -> {
            if (buttonType == save) {
                handleSave();
                System.exit(0);
            } else if (buttonType == exit) {
                System.exit(0);
            }
        });

        //close the window
        System.exit(0);
    }

    //TODO: implement remaining hints(listener), remaining cells(?)
    //TODO: implement, show hint, themes
    //TODO: CSS styling --> dark mode, light mode, custom themes
    //TODO: themes --> light, dark, purple, blue, green, yellow, orange, pink
    //TODO: background color, button/controlls color, clear button text color, text field color, text color, editable text field color
    //TODO: add separator lines between the 3x3 blocks, highlight the selected cell, highlight the row and column of the selected cell
    //TODO: fix mouse event on typing --> sanitize the input on keypress, update the puzzle state on key release
    //TODO: rewrite json save/load
    //TODO: redesign the menu bar --> group buttons together
    //TODO: implement the new game functionality + dialog windows for saving the current game
    //TODO: redesign the start window --> add a new game button, load game button, about button, exit button
    // the first window closes after the user selects a new game or loads a game --> the game window opens

    //TODO: add extra checks for the validity of the input, add unit tests for everything

    private void handleButtonInput(Event event) {
        Button source = (Button) event.getSource();

        if (source.getText().equals("X")) {
            if (lastFocused != null) {
                lastFocused.clear();
            }
            return; // Exit early after clearing the text field
        }

        int value = Integer.parseInt(source.getText());
        if (lastFocused != null) {
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
            case UP:
                if (row > 0) { sudokuGrid.getChildren().get((row - 1) * 9 + col + 1).requestFocus(); }
                break;
            case DOWN:
                if (row < 8) { sudokuGrid.getChildren().get((row + 1) * 9 + col + 1).requestFocus(); }
                break;
            case LEFT:
                if (col > 0) { sudokuGrid.getChildren().get(row * 9 + col).requestFocus(); }
                break;
            case RIGHT:
                if (col < 8) { sudokuGrid.getChildren().get(row * 9 + col + 2).requestFocus(); }
                break;
            default:
                handleCellInputSanitize(event);
                break;
        }
    }

    public void handleCellInput(Event event) {

        TextField source = (TextField) event.getSource();
        source.requestFocus();
        int row = GridPane.getRowIndex(source);
        int col = GridPane.getColumnIndex(source);
        System.out.print(source.getText());

        //if the value is non-numeric, clear the cell
        if (source.getText().isEmpty() || !source.getText().matches("[0-9]")) {
            source.clear();
            return;
        }
        int value = Integer.parseInt(source.getText());

        puzzleController.addCell(row, col, value);
    }

    public void handleCellInputSanitize(Event event) {
        TextField source = (TextField) event.getSource();
        String text = source.getText();
        if (text.isEmpty() || !text.matches("[0-9]")) {
            source.clear();
            event.consume();
        }
    }

    public GridPane getSudokuGrid() {
        return sudokuGrid;
    }
}