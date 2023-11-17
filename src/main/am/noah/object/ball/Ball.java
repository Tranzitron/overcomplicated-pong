package main.am.noah.object.ball;

import main.am.noah.Pong;
import main.am.noah.handler.StageHandler;
import main.am.noah.object.Object;

public class Ball extends Object {

    // Keep track of these variables in order to use whenever resetting the ball.
    private static int DEFAULT_X = 0;
    private static int DEFAULT_Y = 0;
    private static int DEFAULT_HOR_SPEED = 0;

    // Keep track of how fast the ball is going/should go.
    private static int HORIZONTAL_SPEED = 0;
    private static int VERTICAL_SPEED = 0;
    private static int SPEED_INCREASE = 0;

    // Keep track of the play area bounds.
    private static int MINIMUM_Y = 0;
    private static int MAXIMUM_Y = 0;
    private static int WINDOW_WIDTH = 0;


    public Ball(int x, int y, int diameter, int horSpeed, int speedIncrease, int minimumY, int maximumY, int windowWidth) {
        setCoordinateX(x);
        setCoordinateY(y);
        setHeight(diameter);
        setWidth(diameter);

        DEFAULT_X = x;
        DEFAULT_Y = y;
        DEFAULT_HOR_SPEED = horSpeed;

        HORIZONTAL_SPEED = horSpeed;
        VERTICAL_SPEED = (int) ((horSpeed * Math.random()) * (Math.random() > 0.5 ? -1 : 1));
        SPEED_INCREASE = speedIncrease;

        MINIMUM_Y = minimumY;
        MAXIMUM_Y = maximumY;
        WINDOW_WIDTH = windowWidth;
    }

    /**
     * Tell the Ball object a frame has started.
     */
    public void handleFrame(double frameMultiplier, Pong pong) {
        if (pong.lastBallCollisionX == 0) {
            pong.lastBallCollisionX = DEFAULT_X + (StageHandler.BALL_DIAMETER / 2);
            pong.lastBallCollisionY = DEFAULT_Y + (StageHandler.BALL_DIAMETER / 2);
        }
        // If the ball breaches the MAXIMUM_Y reverse the vertical speed and ensure it's within bounds.
        if (getBottomY() >= MAXIMUM_Y) {
            VERTICAL_SPEED = -VERTICAL_SPEED;
            setCoordinateY(MAXIMUM_Y - getHeight());
            pong.lastBallCollisionX = getCoordinateX() + (StageHandler.BALL_DIAMETER / 2);
            pong.lastBallCollisionY = MAXIMUM_Y - (StageHandler.BALL_DIAMETER / 2);
        }

        // If the ball breaches the MINIMUM_Y reverse the vertical speed and ensure it's within bounds.
        if (getTopY() <= MINIMUM_Y) {
            VERTICAL_SPEED = -VERTICAL_SPEED;
            setCoordinateY(MINIMUM_Y);
            pong.lastBallCollisionX = getCoordinateX() + (StageHandler.BALL_DIAMETER / 2);
            pong.lastBallCollisionY = MINIMUM_Y + (StageHandler.BALL_DIAMETER / 2);

        }

        // If the ball breaches either the left or right side of the screen then reset its position and speed.
        if (getRightX() <= 0 || getLeftX() > WINDOW_WIDTH) {
            setCoordinateX(DEFAULT_X);
            setCoordinateY(DEFAULT_Y);
            pong.lastBallCollisionX = DEFAULT_X;
            pong.lastBallCollisionY = DEFAULT_Y;

            HORIZONTAL_SPEED = DEFAULT_HOR_SPEED;
            VERTICAL_SPEED = (int) ((HORIZONTAL_SPEED * Math.random()) * (Math.random() > 0.5 ? -1 : 1));
        }

        final boolean negativeHorizontalSpeed = HORIZONTAL_SPEED < 0;

        /*
         * While we do properly trace the X coordinate of the balls next movement, we don't for the Y coordinate.
         * Instead, we do this where we adjust the Y value by a certain amount for each traced X integer.
         */
        double verticalAmountPer = Math.abs(((HORIZONTAL_SPEED * frameMultiplier) / (VERTICAL_SPEED * frameMultiplier)));
        if (!Double.isFinite(verticalAmountPer)) verticalAmountPer = 0;

        boolean collided = false;

        // Trace the next movement in hopes of finding collisions.
        for (int i = 0; i < Math.abs(HORIZONTAL_SPEED * frameMultiplier); i++) {

            // Create adjusted values of the leftX, rightX, topY, and bottomY based on our trace amount.

            final int leftX = getLeftX() + (i * (negativeHorizontalSpeed ? -1 : 1));
            final int rightX = getRightX() + (i * (negativeHorizontalSpeed ? -1 : 1));

            final int topY = (int) (getTopY() + (i * verticalAmountPer));
            final int bottomY = (int) (getBottomY() + (i * verticalAmountPer));

            // Go through all the objects and check if they collide with the ball.
            for (Object object : pong.getObjects()) {

                // Don't check collisions with itself. The ball will always collide with itself.
                if (object != this) {

                    /*
                     * Accurate hit box method.
                     *
                     * Demonstration/visualization:
                     * https://silentmatt.com/rectangle-intersection/
                     */

                    if (leftX <= object.getRightX()) {
                        if (rightX >= object.getLeftX()) {
                            if (topY <= object.getBottomY()) {
                                if (bottomY >= object.getTopY()) {
                                    collided = true;

                                    // Calculate collision points
                                    if (rightX >= object.getLeftX() && leftX <= object.getLeftX()) {
                                        // Collision happened on the left side of the object
                                        pong.lastBallCollisionX = object.getLeftX() - (StageHandler.BALL_DIAMETER / 2);
                                    } else if (leftX <= object.getRightX() && rightX >= object.getRightX()) {
                                        // Collision happened on the right side of the object
                                        pong.lastBallCollisionX = object.getRightX() + (StageHandler.BALL_DIAMETER / 2);
                                    }

                                    if (bottomY >= object.getTopY() && topY <= object.getTopY()) {
                                        // Collision happened on the top side of the object
                                        pong.lastBallCollisionY = object.getTopY() + (StageHandler.BALL_DIAMETER / 2);
                                    } else if (topY <= object.getBottomY() && bottomY >= object.getBottomY()) {
                                        // Collision happened on the bottom side of the object
                                        pong.lastBallCollisionY = object.getBottomY() - (StageHandler.BALL_DIAMETER / 2);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        /*
         * If the ball collided with anything we need to:
         *
         * Increase the Horizontal speed and reverse it.
         * Set a new random Vertical speed (due to a lack of a better system).
         */
        if (collided) {
//            System.out.println(Math.random() * 100);
            HORIZONTAL_SPEED = (Math.abs(HORIZONTAL_SPEED) + SPEED_INCREASE) * (negativeHorizontalSpeed ? 1 : -1);
            VERTICAL_SPEED = (int) ((HORIZONTAL_SPEED * Math.random()) * (Math.random() > 0.5 ? -1 : 1));
        }

        // Finally, update the ball's CoordinateX and CoordinateY with a frameMultiplier adjusted version of the ball's speed.
        int x = (int) (getCoordinateX() + (HORIZONTAL_SPEED * frameMultiplier));
        int y = (int) (getCoordinateY() + (VERTICAL_SPEED * frameMultiplier));
        setCoordinateX(x);
        setCoordinateY(y);
    }
}
