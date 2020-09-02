import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class Track {

    private static final Random RANDOM = new Random();

    protected char[][] board; // the n x m array which contain characters which represent the driving track
    protected Car car; // the vehicle which will be traversing the track

    /**
     * Constructor for the Track class. takes in the file name of a compatible .txt file and converts it into a two
     * dimensional character array. This array is then stored as the board variable of the instance.
     *
     * @param fileName (String) the file name of a .txt file with an "int,int" format on the first line, dictating the
     *                 x and y scale of the track, followed by a text representation of the track.
     * @throws IOException Throws an IOException if the provided file cannot be read properly.
     */
    public Track(String fileName) throws IOException {
        BufferedReader in = new BufferedReader(new FileReader(new File(fileName))); // open the target file

        // get the size of the track from the first line of the file
        String[] coordinateInformation = in.readLine().split(",");
        int xSize = Integer.parseInt(coordinateInformation[0]);
        int ySize = Integer.parseInt(coordinateInformation[1]);

        // instantiate and fill the board as a character matrix
        String line;
        ArrayList<int[]> possibleStartingCoordinates = new ArrayList<>();
        board = new char[xSize][ySize];
        for (int i = 0; i < xSize; i++) { // each row of the track
            line = in.readLine();
            for (int j = 0; j < ySize; j++) { // each character of the row
                board[i][j] = line.charAt(j);
                if (board[i][j] == 'S') {
                    possibleStartingCoordinates.add(new int[] {i, j});
                }
            }
        }

        int[] startingCoordinates = possibleStartingCoordinates.get(RANDOM.nextInt(possibleStartingCoordinates.size()));
        car = new Car(startingCoordinates[0], startingCoordinates[1]);
    }

    /**
     * Method prints out a visual representation of the track to the console.
     */
    public void printTrack() {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                if (!(i == car.x && j == car.y)) {
                    System.out.print(board[i][j] + " ");
                } else {
                    System.out.print('C' + " ");
                }
            }
            System.out.println();
        }
    }

}
