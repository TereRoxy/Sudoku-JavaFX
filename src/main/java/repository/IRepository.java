package repository;

import model.entity.Puzzle;

import java.util.List;

public interface IRepository {
    void save();
    void load();
    void addPuzzleState(Puzzle puzzle);

    List<Puzzle> getPuzzleStates();
}
