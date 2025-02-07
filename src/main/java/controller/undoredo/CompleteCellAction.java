package controller.undoredo;

import controller.PuzzleController;

public class CompleteCellAction implements IAction {
    private final int row;
    private final int col;
    private final int value;
    private int oldValue;
    private final PuzzleController puzzleController;

    public CompleteCellAction(int row, int col, int value, PuzzleController sc) {
        this.row = row;
        this.col = col;
        this.value = value;
        this.puzzleController = sc;
    }

    @Override
    public void execute() {
        oldValue = puzzleController.getCell(row, col);
        puzzleController.undoAddCell(row, col, value);
    }

    @Override
    public void undo() {
        puzzleController.undoAddCell(row, col, oldValue);
    }
}
