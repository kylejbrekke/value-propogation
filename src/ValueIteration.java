public class ValueIteration {
    private static double[][] rewardMatrix; // 2 dimensional array dictating the reward function for each space on the track
    private static double[][] previous; // the previous state of the value table
    private static double[][] current; // the current state of the value table
    private static int xSize; // the width of the track board
    private static int ySize; // the height of the track board

    /**
     * Method which generates the action value of every space on a provided track board using repeated Bellman updates.
     *
     * @param inputTable (2D Character Array) ASCII representation of the game board
     * @param threshold (Double) difference threshold which determines when the action values have converged
     * @param gamma (Double) constant between in the range [0, 1] which modifies the impact of look-ahead spaces
     * @return a 2D array of doubles indicating the action value of each space on the board
     */
    public static double[][] generate(char[][] inputTable, double threshold, double gamma) {
        xSize = inputTable.length;
        ySize = inputTable[0].length;
        rewardMatrix = new double[xSize][ySize];
        previous = new double[xSize][ySize];
        current = new double[xSize][ySize];


        // generate the reward matrix
        for (int i = 0; i < xSize; i++) {
            for (int j = 0; j < ySize; j++) {
                if (inputTable[i][j] == 'F') { // the target spaces have a reward value of 1
                    rewardMatrix[i][j] = 1;
                } else { // all other spaces have a reward value of 0
                    rewardMatrix[i][j] = 0;
                }
            }
        }

        // set the initial state of the previous and current tables to be equal to the reward matrix
        for (int i = 0; i < xSize; i++) {
            System.arraycopy(rewardMatrix[i], 0, previous[i], 0, ySize);
            System.arraycopy(rewardMatrix[i], 0, current[i], 0, ySize);
        }

        // repeat until convergence occurs
        do {
            // at the beginning of each iteration, copy the current value table to the previous table
            for (int i = 0; i < xSize; i++) {
                System.arraycopy(current[i], 0, previous[i], 0, ySize);
            }

            // iterate over every space on the board
            for (int i = 0; i < xSize; i++) {
                for (int j = 0; j < ySize; j++) {
                    if (inputTable[i][j] != '#') { // do not update values for walls
                        current[i][j] = bestValue(i, j, gamma, inputTable); // update the target location
                    }
                }
            }

        } while (maxDivergence() > threshold); // check for convergence

        return current;
    }

    /**
     * Method determines the largest value modification between the current and previous table states. That is, the
     * method searches through each space in the tables and compares them, then returns the largest difference found.
     * @return a double value representing the largest difference between a current space and its corresponding space on
     *         the previous table
     */
    private static double maxDivergence() {
        double max = -999;
        double diff;

        for (int i = 0; i < xSize; i++) {
            for (int j = 0; j < ySize; j++) {
                // calculate the difference between each space in the current table and the same space in the previous table
                diff = Math.abs(current[i][j] - previous[i][j]);
                max = Math.max(max, diff); // select the larger of the two existing values
            }
        }

        return max; // return the largest difference
    }

    /**
     * Method calculates the action value of a particular space
     *
     * @param x (Integer) the x coordinate on the table
     * @param y (Integer) the y coordinate on the table
     * @return a double value indicating the action value of moving to the provided x-y coordinates
     */
    private static double valueOf(int x, int y) {
        double totalValue = 0;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (!(x + i >= xSize || x + i < 0 || y + j >= ySize || y + j < 0)) { // do not attempt to calculate the value if out of bounds
                    if (i != 0 || j != 0) {
                        totalValue += 0.1 * previous[x + i][y + j];
                    } else {
                        totalValue += 0.2 * previous[x + i][y + j]; // there is a 20% chance that the acceleration will not change
                    }
                }
            }
        }
        return totalValue;
    }

    /**
     * Method calculates the value of all possible actions which can be made from a given space. The algorithm then
     * returns the largest action value.
     *
     * @param x (Integer) the x coordinate which actions will be made from
     * @param y (Integer) the y corodinate which actions will be made from
     * @param gamma (Double) the value which modifies the calculated action values
     * @param inputTable (2D Character Array) table of characters representing the spaces on the board
     * @return a double value indicating the largest action value available from the provided x-y coordinates
     */
    private static double bestValue(int x, int y, double gamma, char[][] inputTable) {
        double maxValue = -999;
        double currentValue;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (!(x + i >= xSize || x + i < 0 || y + j >= ySize || y + j < 0)) {
                    if (inputTable[x + i][y + j] != '#') { // do not evaluate walls as viable spaces
                        // add the reward value of the current space to the cumulative action value of the selected space
                        currentValue = rewardMatrix[x][y] + (gamma * valueOf(x + i, y + i));
                        maxValue = Math.max(maxValue, currentValue); // pick the larger of the two values
                    }
                }
            }
        }
        return maxValue; // returns the largest action value
    }
}
