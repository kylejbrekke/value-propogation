import java.util.*;

public class QLearningController {

    private static final Random RANDOM = new Random();

    private Car car;
    private Track track;
    private boolean totalReset; // determines whether the vehicle returns to its origin upon a collision
    private int xSize; // width of the track board
    private int ySize; // height of the track board
    private int xCur; // the current x position of the vehicle
    private int yCur; // the current y position of the vehicle
    private int xPrev; // the previous x position of the vehicle
    private int yPrev; // the previous y position of the vehicle
    private int[] nextAction; // the next action will will be taken
    private int[][] alphaTable = new int[][] {{100, 100, 100}, {100, 100, 100}, {100, 100, 100}};
    private double[][] rewardMatrix; // table of reward values for each space on the track board
    private double[][][][] qTable; // corresponding q values for each state and action
    private double explorationChance; // probability the vehicle will choose an action at random
    private double gamma; // value which modifies the Q-value calculation after each step

    /**
     * Constructor for the QLearningController class. Takes in a track, exploration chance, and boolean state to determine
     * if the vehicle on the track will return to its origin upon colliding with a wall. The method prepares the necessary
     * structures to proceed with Q-Learning after initialization completes.
     *
     * @param track (Track) the track which the controller will drive its vehicle on
     * @param explorationChance (Double) probability in range [0, 1] that the vehicle will choose a random action
     * @param gamma (Double) value which modifies the Q-Value calculation after each step
     * @param totalReset (Boolean) state determining whether the vehicle will return to origin upon collision
     */
    public QLearningController(Track track, double explorationChance, double gamma, boolean totalReset) {
        this.track = track;
        this.car = track.car;
        this.explorationChance = explorationChance;
        this.gamma = gamma;
        this.totalReset = totalReset;
        xSize = track.board.length;
        ySize = track.board[0].length;

        // build the Q table; each coordinate on the track is a state, whereas there are a total of 9 (3 x 3) possible actions
        qTable = new double[xSize][ySize][3][3];
        buildRewardMatrix(); // constructs the reward matrix for use in Q-Learning
        decideNextAction(car.xStart, car.yStart); // determines the first action the controller will take
    }

    public void start() {
        while (true) {
            if (act()) break;
        }
    }

    /**
     * Method applies the current value of the nextAction variable to the vehicle, then updates instance values and calls
     * the updateQ() method.
     */
    public boolean act() {
        System.out.println(nextAction[0] + "  " + nextAction[1]);
        // set the previous state of the vehicle
        xPrev = car.x;
        yPrev = car.y;

        // apply the action and set the new state of the vehicle
        alphaTable[nextAction[0] + 1][nextAction[1] + 1] += 1;
        car.applyAcceleration(nextAction[0], nextAction[1]);
        xCur = car.x;
        yCur = car.y;

        // display the vehicle updates
        track.printTrack();
        return updateQ(nextAction[0] + 1, nextAction[1] + 1); // update the Q table using the new information
    }

    /**
     * Method determines the effects of the most recent vehicle state change, then updates the vehicle and Q-Table
     * accordingly. If the vehicle reaches a finish state, then the method terminates.
     *
     * @param i (Integer) the action previously applied for the x-coordinate
     * @param j (Integer) the action previously applied for the y-coordinate
     */
    private boolean updateQ(int i, int j) {
        double reward = getReward(xCur, yCur); // determine the reward for the last applied action
        System.out.println(reward);
        if ((xCur >= xSize || xCur < 0 || yCur >= ySize || yCur < 0) || track.board[xCur][yCur] == '#') { // reset on a collision, depending on specified behavior
            if (totalReset) { // vehicle resets to its origin
                car.reset();
                xCur = car.x;
                yCur = car.y;
            } else { // vehicle resets to its previous state
                int[] nearest = findNearestOpenSpace(xCur, yCur);
                car.reset(nearest[0], nearest[1]);
                xCur = nearest[0];
                yCur = nearest[1];
            }
            track.printTrack();
        } else if (track.board[xCur][yCur] == 'F') { // terminate if the vehicle reaches the finish line
            return true;
        }

        double alpha = calculateAlpha(i, j); // calculate the learning rate
        decideNextAction(xCur, yCur); // determine the next action to be applied

        double q1 = qTable[xPrev][yPrev][i][j]; // acquire the Q-Value for the previous state
        double q2 = qTable[xCur][yCur][nextAction[0] + 1][nextAction[1] + 1]; // acquire the Q-Value for the current state and expected action

        qTable[xPrev][yPrev][i][j] = ((1 - alpha) * q1) + (alpha * (reward + (gamma * q2))); // update the Q-Table
        return false;
    }

    /**
     * Method pulls the reward value for the provided x-y coordinates.
     *
     * @param x (Integer) the x coordinate
     * @param y (Integer) the y coordinate
     * @return a double value indicating the reward for the provided coordinate position
     */
    private double getReward(int x, int y) {
        if (x >= xSize || x < 0 || y >= ySize || y < 0) {
            return -1;
        }
        return rewardMatrix[x][y];
    }

    /**
     * Method determines what the next action to be applied will be. There is a stochastic element which determines
     * whether the action will be made at random based upon the explorationChance value of the controller object. If
     * there are multiple optimal actions, one will be selected at random.
     *
     * @param x the current x coordinate of the vehicle
     * @param y the curreny y coordinate of the vehicle
     */
    private void decideNextAction(int x, int y) {
        int[] action = new int[2];
        if (RANDOM.nextDouble() < explorationChance) { // make an action at random
            nextAction = new int[] {RANDOM.nextInt(3) - 1, RANDOM.nextInt(3) - 1};
        } else { // select the maximized action based upon the current Q-Table information
            double max = -999;
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    if (max < qTable[x][y][i + 1][j + 1]) { // the Q-Table value is larger, so pick it
                        action = new int[]{i, j};
                        max = qTable[x][y][i + 1][j + 1];
                    } else if (max == qTable[x][y][i + 1][j + 1]) { // the Q-Table value and max value are equal
                        if (RANDOM.nextInt(2) == 1) { // randomly pick between the current and selected action
                            action = new int[]{i, j};
                        }
                    }
                }
            }
            nextAction = action; // assign the next action
        }
    }

    /**
     * Method constructs the reward matrix based on the track board. The reward of any viable space is equal to the
     * minimum number of spaces between it and the nearest finish space. The reward of a wall is -1.
     */
    private void buildRewardMatrix() {
        boolean[][] touched = new boolean[xSize][ySize]; // 2d array indicating which spaces have been evaluated or set to be evaluated
        Queue<int[]> toVisit = new LinkedList<>(); // queue of coordinates to evaluate
        rewardMatrix = new double[xSize][ySize];
        for (int i = 0; i < xSize; i++) {
            for (int j = 0; j < ySize; j++) {
                // assign initial reward values based on the type of each space
                rewardMatrix[i][j] = track.board[i][j] == '#' ? -1 : 0;
                if (track.board[i][j] == 'F') {
                    // assign the values of the finish line, and set them to be evaluated
                    rewardMatrix[i][j] = xSize * ySize;
                    toVisit.add(new int[] {i, j});
                    touched[i][j] = true;
                } else {
                    touched[i][j] = false;
                }
            }
        }

        int[] coords;
        int x, y, xi, yj;
        double current;
        while (!toVisit.isEmpty()) { // continue until there are no more spaces to evaluate
            coords = toVisit.remove();
            x = coords[0];
            y = coords[1];

            if (track.board[x][y] == '#') { // do not update the rewards of wall spaces
                continue;
            }

            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    xi = x + i;
                    yj = y + j;

                    if (!(xi >= xSize || xi < 0 || yj >= ySize || yj < 0)) { // ensure the touched coordinates are not out of bounds
                        if (!touched[xi][yj]) {
                            toVisit.add(new int[] {xi, yj});
                            touched[xi][yj] = true;
                        }

                        current = rewardMatrix[xi][yj];
                        rewardMatrix[x][y] = rewardMatrix[x][y] >= current ? rewardMatrix[x][y] : current / 1.1; // choose the highest neighboring reward value, and reduce it by ten
                    }
                }
            }
        }
    }

    /**
     * Method calculates the alpha value for a given action. This serves as the learning rate for the action. As the
     * action occurs more, its learning rate will decay over time, until the values in the Q-Table converge.
     *
     * @param i the action on the x-coordinate
     * @param j the action on the y-coordinate
     * @return a double value representing the learning rate for the provided action
     */
    private double calculateAlpha(int i, int j) {
        return 100d / (double)alphaTable[i][j];
    }

    private int[] findNearestOpenSpace(int x, int y) {
        int minDistance = 9999;
        int[] nearest = new int[2];
        for (int i = 0; i < xSize; i++) {
            for (int j = 0; j < ySize; j++) {
                if (track.board[i][j] == '.') {
                    int i1 = Math.abs(x - i) + Math.abs(y - j);
                    if (i1 < minDistance) {
                        minDistance = i1;
                        nearest = new int[] {i, j};
                    } else if (i1 == minDistance) {
                        int i2 = Math.abs(xPrev - i) + Math.abs(yPrev - j);
                        int i3 = Math.abs(xPrev - nearest[0]) + Math.abs(yPrev - nearest[1]);
                        if (i2 < i3) {
                            nearest = new int[] {i, j};
                        }
                    }
                }
            }
        }
        System.out.println("The Nearest Coordinates to (" + x + ", " + y + ") are (" + nearest[0] + ", " + nearest[1] + ")");
        return nearest;
    }
}
