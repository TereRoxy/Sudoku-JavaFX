package controller;

import controller.undoredo.CompleteCellAction;
import controller.undoredo.IAction;
import model.entity.Puzzle;
import model.grid.PuzzleGrid;
import model.grid.SolutionGrid;

import java.util.Stack;

public class PuzzleController {
    private Stack<IAction> undoStack;
    private Stack<IAction> redoStack;
    private Puzzle puzzle;

    public PuzzleController(){
        startNewGame(3);
    }

    public PuzzleController(Puzzle puzzle) {
        this.puzzle = puzzle;
        undoStack = new Stack<>();
        redoStack = new Stack<>();
    }

    public void addCell(int row, int col, int value) {
        puzzle.addCell(row, col, value);
        undoStack.push(new CompleteCellAction(row, col, value, this));
        redoStack.clear();
    }

    public void undoAddCell(int row, int col, int value) {
        puzzle.addCell(row, col, value);
    }

    public int getCell(int row, int col) {
        return puzzle.getCell(row, col);
    }

    public void undo() {
        if (!undoStack.isEmpty()) { //TODO: repair undo --> when undo is called, the undo operation adds the cleared element to the stack
            IAction action = undoStack.pop();
            action.undo();
            redoStack.push(action);
        }
        else{
            throw new IllegalStateException("No actions to undo");
        }
    }

    public void redo() {
        if (!redoStack.isEmpty()) {
            IAction action = redoStack.pop();
            action.execute();
            undoStack.push(action);
        }
        else{
            throw new IllegalStateException("No actions to redo");
        }
    }

    public void startNewGame(int difficulty) {
        SolutionGrid solutionGrid = new SolutionGrid();
        PuzzleGrid puzzleGrid = new PuzzleGrid(solutionGrid, difficulty);
        puzzle = new Puzzle(puzzleGrid, solutionGrid, "New Game");
        undoStack = new Stack<>();
        redoStack = new Stack<>();
    }

    public Puzzle getPuzzle() {
        return puzzle;
    }

    public void setPuzzle(Puzzle puzzle) {
        this.puzzle = puzzle;
        undoStack = new Stack<>();
        redoStack = new Stack<>();
    }

//    public SolutionGrid getSolution() {
//        return puzzle.getSolutionGrid();
//    }

    public void reset() {
        puzzle.reset();
        undoStack.clear();
        redoStack.clear();
    }

    public void giveHint(){
        puzzle.giveHint();
    }
}