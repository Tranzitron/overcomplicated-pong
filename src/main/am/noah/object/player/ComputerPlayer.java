package main.am.noah.object.player;

import main.am.noah.Pong;
import main.am.noah.object.Object;

public class ComputerPlayer extends Object {

    private static int MOVEMENT_SPEED = 0;
    private static int MINIMUM_Y = 0;
    private static int MAXIMUM_Y = 0;
    private final double MOVEMENT_THRESHOLD = 4;

    /**
     * Mode 1: Tries to stay on same Y level as the ball
     * Mode 2: Calculate the trajectory of the ball and goes towards the Y level
     */
    private final int AI_MODE = 2;

    public ComputerPlayer(int x, int y, int height, int width, int movementSpeed, int minimumY, int maximumY) {
        setCoordinateX(x);
        setCoordinateY(y);
        setHeight(height);
        setWidth(width);

        MOVEMENT_SPEED = movementSpeed;
        MINIMUM_Y = minimumY;
        MAXIMUM_Y = maximumY;
    }

    /**
     * Tell the ComputerPlayer object a frame has started.
     */
    @Override
    public void handleFrame(double frameMultiplier, Pong pong) {
        int x = Integer.MAX_VALUE;
        int y = 0;

        Object pongBall = pong.getBall();
        /*
         * Because we don't know whether the computer operated paddle is on the left or right we
         * should try to find the smallest distance to the ball, whether that be to the ball's right edge or left edge.
         */
        final int distance = getDistance(pongBall);

        /*
         * If the distance is shorter than our current recorded X, update the X and Y integers as this is our new
         * closest ball.
         */
        if (distance < x) {
            x = distance;
            // Make the paddle attempt to get the ball to hit it in its center.
            y = pongBall.getCoordinateY() - ((getHeight() - pongBall.getHeight()) / 2);
        }

        switch (AI_MODE) {
            case 1: {
                // Determine the y-coordinate difference between the ball and the center of the paddle.
                int deltaY = pongBall.getCoordinateY() - (getCoordinateY() + getHeight() / 2);

                if (Math.abs(deltaY) > MOVEMENT_THRESHOLD) {
                    // Move the paddle towards the ball to reduce jittering.
                    setCoordinateY((int) Math.min(MAXIMUM_Y, Math.max(MINIMUM_Y, getCoordinateY() + MOVEMENT_SPEED * frameMultiplier * Math.signum(deltaY))));
                }
            }
            case 2: {
                // Determine the y-coordinate difference between the ball and the center of the paddle.
                int deltaY = pongBall.getCoordinateY() - (getCoordinateY() + getHeight() / 2);

                if (Math.abs(deltaY) > MOVEMENT_THRESHOLD) {
                    // Move the paddle towards the ball to reduce jittering.
                    setCoordinateY((int) Math.min(MAXIMUM_Y, Math.max(MINIMUM_Y, getCoordinateY() + MOVEMENT_SPEED * frameMultiplier * Math.signum(deltaY))));
                }
            }
        }
    }

    private int getDistance(Object pongBall) {
        final int distanceToLeftEdge = Math.abs(getRightX() - pongBall.getLeftX());
        final int distanceToRightEdge = Math.abs(getLeftX() - pongBall.getRightX());

        // Use the smaller of the two distances for our actual process.
        return Math.min(distanceToLeftEdge, distanceToRightEdge);
    }
}
