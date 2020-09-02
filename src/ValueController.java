public class ValueController {

    private Car car;
    private Track track;
    private boolean totalReset; // determines whether the vehicle returns to its origin upon a collision
    private int xSize; // width of the track board
    private int ySize; // height of the track board
    private int xCur; // the current x position of the vehicle
    private int yCur; // the current y position of the vehicle
    private int[] nextAction; // the next action will will be taken
    private double[][] valueTable; // value table used to decide actions

    /**
     * Constructor for the ValueController class. Takes in a track, value table, and boolean state to determine
     * if the vehicle on the track will return to its origin upon colliding with a wall.
     *
     * @param track (Track) the track which the controller will drive its vehicle on
     * @param valueTable (2D Double Array) array containing the action values for each space on the track
     * @param totalReset (Boolean) state determining whether the vehicle will return to origin upon collision
     */
    public ValueController(Track track, double[][] valueTable, boolean totalReset) {
        this.track = track;
        this.car = track.car;
        this.valueTable = valueTable;
        this.totalReset = totalReset;
        xSize = track.board.length;
        ySize = track.board[0].length;
        xCur = car.x;
        yCur = car.y;
        decideNextAction(); // determines the first action the controller will take
    }

    public void start() {
        for (double[] row : valueTable) {
            for (double value : row) {
                System.out.print(String.format("%.12f \t", value));
            }
            System.out.println("\n");
        }
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
        car.applyAcceleration(nextAction[0], nextAction[1]);
        xCur = car.x;
        yCur = car.y;

        // display the vehicle updates
        track.printTrack();
        return decideNextAction();
    }

    /**
     * Method determines what the next action to be applied will be. The method also checks to make sure that the
     * vehicle is within bounds and whether it has reached the finish line yet.
     */
    private boolean decideNextAction() {
        if ((xCur >= xSize || xCur < 0 || yCur >= ySize || yCur < 0) || track.board[xCur][yCur] == '#') {
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

        // vehicle has not reached the finish line yet, so pick another action
        int[] action = new int[2];
        double max = -999;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (valueTable[xCur + i][yCur + j] > max) { // select the action which maximizes value
                    max = valueTable[xCur + i][yCur + j];
                    action = new int[] {i, j};
                }
            }
        }

        nextAction = action; // assign the next action
        return false;
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
                        int i2 = Math.abs(xCur - i) + Math.abs(yCur - j);
                        int i3 = Math.abs(xCur - nearest[0]) + Math.abs(yCur - nearest[1]);
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
