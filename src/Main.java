import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        try {
            for (int i = 0; i < 100; i++) {
                Track track = new Track(args[0]);
                track.printTrack();

                QLearningController qLearningController = new QLearningController(track, 0.1, 0.8,  true);
                qLearningController.start();

                //ValueController valueController = new ValueController(track, ValueIteration.generate(track.board, 0.01, 1), false);
                //valueController.start();

                System.out.print(track.car.currentTime + "\t");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
