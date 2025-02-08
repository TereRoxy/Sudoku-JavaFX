package repository;

import model.entity.Puzzle;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
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

    private final List<Puzzle> puzzles;
    private final String fileName;

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
            writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            writer.write("<puzzles>\n");
            for (Puzzle puzzle : puzzles) {
                writer.write(puzzle.saveState().replace("<", "  <"));
            }
            writer.write("</puzzles>");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void load() {
        File file = new File(fileName);
        if (file.length() == 0) {
            throw new IllegalArgumentException("File is empty. Nothing to load.");
        }

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new File(fileName));

            NodeList puzzleNodes = doc.getElementsByTagName("puzzle");
            puzzles.clear();

            for (int i = 0; i < puzzleNodes.getLength(); i++) {
                Element puzzleElement = (Element) puzzleNodes.item(i);
                Puzzle puzzle = new Puzzle();
                puzzle.loadState(puzzleElement);
                puzzles.add(puzzle);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
