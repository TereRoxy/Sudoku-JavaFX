package controller.undoredo;

public interface IAction {
    void execute();
    void undo();
}
