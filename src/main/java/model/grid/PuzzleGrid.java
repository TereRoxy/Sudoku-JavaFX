package model.grid;

import model.adt.Pair;

import java.util.Map;

public class PuzzleGrid extends Grid {
    //private final int[][] grid;
    private int deletionSymmetry;
    private int nrTargetCells;
    private int nrHints;
    private SolutionGrid solution;

    public PuzzleGrid(){
        super();
    } //to be used only when deserializing

    public PuzzleGrid(SolutionGrid solutionGrid, int difficulty){
        grid = new int[9][9];
        for(int i = 0; i < 9; i++){
            for(int j = 0; j < 9; j++){
                grid[i][j] = solutionGrid.getGrid()[i][j];
            }
        }

        Map<Integer, Integer> difficultyCellsMap = Map.of(1, 35, 2, 40, 3, 45, 4, 50, 5, 55);
        Map<Integer, Integer> difficultyHintsMap = Map.of(1, 3, 2, 3, 3, 3, 4, 5, 5, 5);

        //select the right parameters based on the difficulty
        deletionSymmetry = (int)(Math.random() * 6);
        nrTargetCells = difficultyCellsMap.get(difficulty);
        nrHints = difficultyHintsMap.get(difficulty);
        solution = solutionGrid;

        populate();
    }

    //generate the puzzle grid by deleting cells from the solution grid
    public void populate() {
        int nrDeletedCells = 0;
        int nrTries = 0;
        //select the rotation degree for the rotational symmetry
        int rotation = (int)(Math.random() * 3 + 1);


        //if we've tried too many times, we should stop (too little constraints for unique solution or choosing the same cell too many times)
        while(nrDeletedCells < nrTargetCells && nrTries < 1000){
            //distribute the missing cells uniformly across the cublets (3x3 squares)
            //select random cublet
            int cubletRow = (int)(Math.random() * 3);
            int cubletCol = (int)(Math.random() * 3);
            //cubltes[cubletRow][cubletCol]++;

            //select random cell in the cublet --> if there are many empty cells, it is more likely to select an invalid
            // spot in the cublet => the alg goes through another iteration => more likely to succeed in a fuller square
            // distribution is more spread out
            int row = cubletRow * 3 + (int)(Math.random() * 3);
            int col = cubletCol * 3 + (int)(Math.random() * 3);
            if(grid[row][col] != 0){
                Pair<Integer, Integer> symmetricCell = getSymmetricCell(row, col);
                grid[row][col] = 0;
                nrDeletedCells++;
                if (deletionSymmetry != 0){ // if we delete based on symmetry, we need to delete the symmetric cell as well
                    if (deletionSymmetry == 5){ //if we have rotational symmetry, we need to rotate the cell 90 degrees a random number of times
                        for (int i = 0; i < rotation; i++){
                            symmetricCell = getSymmetricCell(symmetricCell.getFirst(), symmetricCell.getSecond());
                        }
                    }
                    int i = symmetricCell.getFirst();
                    int j = symmetricCell.getSecond();
                    grid[i][j] = 0;
                    nrDeletedCells++;
                }
            }
            else {
                nrTries++;
            }
        }

        while (countSolutions(grid) > 1){
            //refill some cells
            boolean ok = false;
            while(!ok){
                int row = (int)(Math.random() * 9);
                int col = (int)(Math.random() * 9);
                if (grid[row][col] == 0) {
                    grid[row][col] = solution.getGrid()[row][col];
                    nrDeletedCells--;
                    ok = true;
                }
            }
        }
    }

    private Pair<Integer, Integer> getSymmetricCell(int row, int col){
        return switch (deletionSymmetry) {
            case 0 -> new Pair<>(row, col); //no symmetry
            case 1 -> new Pair<>(row, 8 - col); // vertical symmetry
            case 2 -> new Pair<>(8 - row, col); // horizontal symmetry
            case 3 -> new Pair<>(8 - row, 8 - col); // diagonal symmetry 1
            case 4 -> new Pair<>(col, row); // diagonal symmetry 2
            case 5 -> new Pair<>(8 - col, row); //rotational symmetry
            default -> new Pair<>(row, col);
        };
    }

    private int countSolutions(int[][] solutionGrid){
        int count = 0;
        for(int i = 0; i < 9; i++){
            for(int j = 0; j < 9; j++){
                if(solutionGrid[i][j] == 0){
                    for(int k = 1; k <= 9; k++){
                        if(isValid(solutionGrid, i, j, k)){
                            solutionGrid[i][j] = k;
                            count += countSolutions(solutionGrid);
                            solutionGrid[i][j] = 0;
                        }
                    }
                    return count;
                }
            }
        }
        return 1;
    }


    public int getNrHints() {
        return nrHints;
    }
}
