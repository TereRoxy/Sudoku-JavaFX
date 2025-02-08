# Sudoku Game

## Overview
This project is a Sudoku game implemented in Java using JavaFX for the user interface. The game allows users to start new games with different difficulty levels, save and load games, undo and redo moves, and check the solution.

## Features
- Start new games with various difficulty levels (Novice, Easy, Medium, Hard, Very Hard)
- Save and load game states
- Undo and redo moves
- Show hints
- Check the solution
- Timer to track the time spent on the puzzle

## Requirements
- Java 17 or higher
- Maven 3.6.0 or higher

## Setup
1. Clone the repository:
    ```sh
    git clone https://github.com/yourusername/sudoku-game.git
    cd sudoku-game
    ```

2. Build the project using Maven:
    ```sh
    mvn clean install
    ```

3. Run the application:
    ```sh
    mvn javafx:run
    ```

## Usage
- **New Game**: Select a difficulty level from the "New Game" menu to start a new game.
- **Save Game**: Click the "Save Game" button to save the current game state.
- **Load Game**: Click the "Load Game" button to load a previously saved game.
- **Undo**: Click the "Undo" button to undo the last move.
- **Redo**: Click the "Redo" button to redo the last undone move.
- **Reset**: Click the "Reset" button to reset the puzzle to its initial state.
- **Show Hint**: Click the "Show Hint" button to reveal a hint.
- **Check Solution**: Click the "Check Solution" button to check the solution of the puzzle.
- **Themes**: Click the "Themes" button to change the theme of the application.
- **About**: Click the "About" button to view information about the application.
- **Exit**: Click the "Exit" button to exit the application.

## Project Structure
- `src/main/java`: Contains the Java source files.
    - `controller`: Contains the controller classes.
    - `model`: Contains the model classes.
    - `repository`: Contains the repository classes.
    - `view`: Contains the view classes.
- `src/main/resources`: Contains the FXML files and other resources.
- `src/test/java`: Contains the test classes.

## Contributing
Contributions are welcome! Please open an issue or submit a pull request.

## License
This project is licensed under the MIT License. See the `LICENSE` file for details.