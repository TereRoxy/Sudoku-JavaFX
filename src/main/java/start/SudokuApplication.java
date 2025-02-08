package start;

import controller.PuzzleController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.entity.Puzzle;
import model.grid.PuzzleGrid;
import model.grid.SolutionGrid;
import repository.IRepository;
import repository.Repository;
import view.MainWindowController;

import java.io.IOException;

public class SudokuApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(SudokuApplication.class.getResource("main-window-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);

        // Get the controller instance
        MainWindowController controller = fxmlLoader.getController();

        // Create instances of PuzzleController and IRepository
        IRepository repository = new Repository("saved_games.xml");

        SolutionGrid solutionGrid = new SolutionGrid();
        //solutionGrid.populate();
        PuzzleGrid puzzleGrid = new PuzzleGrid(solutionGrid, 3);
        //puzzleGrid.populate();
        Puzzle puzzle = new Puzzle(puzzleGrid, solutionGrid, "New Game");
        // Create a new PuzzleController instance
        PuzzleController puzzleController = new PuzzleController(puzzle);

        // Set the repository and puzzle controller
        controller.setRepository(repository);
        controller.setPuzzleController(puzzleController);

        controller.setupGrid();

        stage.setTitle("Sudoku");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}