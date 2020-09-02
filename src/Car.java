import java.util.Random;

public class Car {

    static final Random RANDOM = new Random();

    protected int xStart; // the current x coordinate of the vehicle
    protected int yStart; // the current y coordinate of the vehicle
    protected int x; // list of the vehicle's x position at index == time t
    protected int y; // list of the vehicle's y position at index == time t
    private int xVelocity = 0; // the vehicle's x velocity
    private int yVelocity = 0; // the vehicle's y velocity
    private int xAcceleration = 0; // the present acceleration of the vehicle's xVelocity
    private int yAcceleration = 0; // the present acceleration of the vehicle's yVelocity
    protected int currentTime = 0; // the present time t

    /**
     * Constructor for the Car class. Takes in the initial x and y position of the vehicle and sets up initial values
     * for the remaining instance variables.
     *
     * @param x (Integer) the initial x position of the vehicle
     * @param y (Integer) the initial y position of the vehicle
     */
    public Car(int x, int y) {
        xStart = x;
        yStart = y;
        this.x = x;
        this.y = y;
        currentTime++;
    }

    /**
     * Method updates the position of the vehicle based upon its x and y velocity at the current time.
     */
    private void updatePosition() {
        int xOld = x;
        int yOld = y;
        x = xVelocity + x;
        y = yVelocity + y;
        //System.out.println(x + ", " + y + "(Velocity: " + xVelocity + ", " + yVelocity + ") from " + xOld + ", " + yOld);
    }

    /**
     * Method updates the velocity of the vehicle based upon its present acceleration. If it is attempted to increase
     * the x or y velocity beyond 5, the provided speed will remain at 5. If it is attempted to decrease the x or y
     * velocity below -5, the provided speed will remain at -5.
     */
    private void updateVelocity() {
        int newXVelocity = xAcceleration + xVelocity; // calculate x velocity
        int newYVelocity = yAcceleration + yVelocity; // calculate y velocity

        // do not allow velocity to increase past 5
        newXVelocity = Math.min(newXVelocity, 5);
        newYVelocity = Math.min(newYVelocity, 5);

        // do not allow velocity to decrease past -5
        newYVelocity = Math.max(newYVelocity, -5);
        newXVelocity = Math.max(newXVelocity, -5);

        // apply the (possibly) new velocities
        this.xVelocity = newXVelocity;
        this.yVelocity = newYVelocity;
    }


    /**
     * Method applies an acceleration value to the current x and y acceleration values, then updates the vehicle's
     * velocity and position using the acceleration change. After changes are applied, the current time increments by 1.
     * Additionally, there is a 20% chance that the x and y acceleration values will each be 0, regardless of the inputs.
     *
     * @param xChange (Integer) the change applied to the xVelocity for the current time
     * @param yChange (Integer) the change applied to the yVelocity for the current time
     */
    public void applyAcceleration(int xChange, int yChange) {
        if (RANDOM.nextInt(100) < 20) { // select a value between 0 and 99
            // if the selected value is between 0 and 19, then there is no acceleration or deceleration
            //System.out.println("Action Ignored!");
            xAcceleration = 0;
            yAcceleration = 0;
        } else {
            // if the selected value is between 20 and 99, then the acceleration is applied
            xAcceleration = xChange;
            yAcceleration = yChange;
        }

        updateVelocity(); // update the vehicle's velocity with the new acceleration values
        updatePosition(); // update the vehicle's position with the new velocity values
        currentTime++;
    }

    /**
     * Method returns the vehicle to its origin, while also clearing its velocity and acceleration.
     */
    public void reset() {
        x = xStart;
        y = yStart;
        xVelocity = 0;
        yVelocity = 0;
        xAcceleration = 0;
        yAcceleration = 0;
    }

    /**
     * Method sets the position of the vehicle to a provided coordinate, while also clearing its velocity and acceleration.
     * @param x (Integer) the x coordinate the vehicle is set to
     * @param y (Integer) the y coordinate the vehicle is set to
     */
    public void reset(int x, int y) {
        this.x = x;
        this.y = y;
        xVelocity = 0;
        yVelocity = 0;
        xAcceleration = 0;
        yAcceleration = 0;
    }
}
