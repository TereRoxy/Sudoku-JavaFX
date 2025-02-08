package model.grid;
public class SolutionGrid extends Grid {

    public SolutionGrid(){
        super();
        populate();
    }

    private void generateSeed(){
        int[] seed = new int[9];
        for(int i = 1; i <= 9; i++){
            seed[i - 1] = i;
        }
        for(int i = 0; i < 9; i++){
            //randomly swap the current element with another element
            int index = (int)(Math.random() * 9);
            int temp = seed[i];
            seed[i] = seed[index];
            seed[index] = temp;
        }
        System.arraycopy(seed, 0, grid[0], 0, 9);
    }

    private boolean solve() {
        for (int row = 1; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                if (grid[row][col] == 0) {
                    for (int num = 1; num <= 9; num++) {
                        if (isValid(this.grid, row, col, num)) {
                            grid[row][col] = num;
                            if (solve()) {
                                return true;
                            }
                            grid[row][col] = 0;
                        }
                    }
                    return false;
                }
            }
        }
        return true;
    }

    public void populate(){
        generateSeed();
        solve();
    }

}
