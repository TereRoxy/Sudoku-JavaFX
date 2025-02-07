package repository;

import model.entity.Puzzle;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Repository implements IRepository {
    //TODO: A repository should have a list of puzzle states
    //TODO: A repository should have a method to add a puzzle state
    //TODO: A repository should have a method to remove a puzzle state
    //TODO A repository should have a method to get a puzzle state by index
    //TODO: A repository should have a method to save the repository to a file
    //TODO: A repository should have a method to load the repository from a file

    private List<Puzzle> puzzles;
    private String fileName;

    public Repository(String fileName) {
        this.puzzles = new ArrayList<>();
        this.fileName = fileName;
    }

    public void addPuzzleState(Puzzle puzzle) {
        puzzles.add(puzzle);
    }

    public void removePuzzleState(Puzzle puzzle) {
        puzzles.remove(puzzle);
    }

    public Puzzle getPuzzleState(int index) {
        return puzzles.get(index);
    }

    public List<Puzzle> getPuzzleStates() {
        return puzzles;
    }

    @Override
    public void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write("[\n");
            for (int i = 0; i < puzzles.size(); i++) {
                String puzzleJson = puzzles.get(i).saveState();
                // Add indentation to each line
                puzzleJson = puzzleJson.replace("\n", "\n    ");
                writer.write("    " + puzzleJson);
                if (i < puzzles.size() - 1) {
                    writer.write(",\n");
                }
            }
            writer.write("\n]");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void load() {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            List<String> puzzleJsons = new ArrayList<>();
            StringBuilder currentPuzzle = new StringBuilder();
            int braceCount = 0;
            String line;

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("{")) {
                    currentPuzzle = new StringBuilder();
                    braceCount = 0;
                }

                for (char c : line.toCharArray()) {
                    if (c == '{') braceCount++;
                    if (c == '}') braceCount--;
                }

                currentPuzzle.append(line);

                if (braceCount == 0 && line.endsWith("}")) {
                    puzzleJsons.add(currentPuzzle.toString());
                }
            }

            puzzles.clear();
            for (String json : puzzleJsons) {
                Puzzle puzzle = new Puzzle();
                puzzle.loadState(json);
                puzzles.add(puzzle);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
