package main.am.noah.handler;

import main.am.noah.Pong;
import main.am.noah.object.Object;
import main.am.noah.object.ball.Ball;
import main.am.noah.utils.RayCastUtil;

import javax.swing.*;
import java.awt.*;

public class WindowHandler extends JFrame {
    private final double[] frameDeltaTimeArray = new double[50];
    Pong pong;
    private long lastRecalculateFrameTimes;
    private String lastMs;
    private String lastFps;
    private int lastFrameIndex;


    public WindowHandler(Pong pong) {
        this.pong = pong;

        /*
         * Create the window itself.
         * This will create a window with a resolution of 1920 x 1080 titled "Pong".
         */
        setSize(pong.getBaseWindowWidth(), pong.getBaseWindowHeight());
        setTitle("Pong by am noah");

        /*
         * Make it so the program ends when this window is closed.
         */
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        /*
         * Listen for key presses when focused on the window.
         * Without this we do not know whenever the player interacts with the window.
         */
        addKeyListener(pong.getInputHandler());

        // Set the resizable boolean to false initially.
        setResizable(false);

        /*
         * Lastly, open the window. With this the window will pop up and the player will be able to use it.
         */
        setVisible(true);
    }

    public void paint(Graphics g) {
        // We can use these to scale our picture to any width/height.
        final double widthMultiplier = getWidth() / (double) pong.getBaseWindowWidth();
        final double heightMultiplier = getHeight() / (double) pong.getBaseWindowHeight();

        /*
         * In order to prevent flickering we use a method called Double Buffering.
         * Instead of painting each object one by one we instead paint each object onto an image, rendering the whole
         * image at once.
         * This reduces flicking drastically, making the picture much nicer to look at.
         */
        Image doubleBuffering = createImage(getWidth(), getHeight());
        Graphics graphics = doubleBuffering.getGraphics();

        // Switch between the proper rendering for each stage.
        switch (pong.getStageHandler().getCurrentStage()) {
            case MAIN_SCREEN:
                paintMainFrame(graphics);

                break;
            case SELECTION_SCREEN_ONE:
                paintSelectionOneFrame(graphics);

                break;
            case SELECTION_SCREEN_TWO:
                paintSelectionTwoFrame(graphics);

                break;
            case CONTROL_SCREEN:
                paintControlsFrame(graphics);

                break;
            case ACTIVE_GAME:
                paintGameFrame(graphics, widthMultiplier, heightMultiplier);

                break;
            case PAUSE_MENU:
                paintPauseFrame(graphics);

                break;
        }

        // Finally, draw our completed image.
        g.drawImage(doubleBuffering, 0, 0, this);
    }

    /**
     * Paint our Main screen frame.
     * This is just an introduction screen to shout out myself.
     */
    private void paintMainFrame(Graphics g) {
        g.setFont(new Font(getFont().getName(), getFont().getStyle(), 75));

        // Create our hardcoded messages.
        final String topText = "Pong by am noah";
        final String bottomText = "Click any key to continue.";

        final int topTextWidth = g.getFontMetrics().stringWidth(topText);

        // Render the TopText.
        g.drawString(topText, (getWidth() / 2) - (topTextWidth / 2), (getHeight() / 2) - 50);

        // I want to make the bottom text smaller.
        g.setFont(new Font(getFont().getName(), getFont().getStyle(), 50));
        final int bottomTextWidth = g.getFontMetrics().stringWidth(bottomText);

        // Render the BottomText.
        g.drawString(bottomText, (getWidth() / 2) - (bottomTextWidth / 2), (getHeight() / 2) + 50);
    }

    /**
     * Paint our SelectionOne screen frame.
     * This includes the information about what button to choose to select a left paddle mode.
     */
    private void paintSelectionOneFrame(Graphics g) {
        g.setFont(new Font(getFont().getName(), getFont().getStyle(), 50));

        // All of our hardcoded messages.
        final String topText = "Click '1' to make the Left paddle computer operated.";
        final String middleText = "Click '2' to make the Left paddle player operated.";
        final String bottomText = "Click '3' to make the Left paddle a wall.";

        // Find the string width of each.
        final int topTextWidth = g.getFontMetrics().stringWidth(topText);
        final int middleTextWidth = g.getFontMetrics().stringWidth(middleText);
        final int bottomTextWidth = g.getFontMetrics().stringWidth(bottomText);

        // Scale the position of each String into a proper position.
        g.drawString(topText, (getWidth() / 2) - (topTextWidth / 2), (getHeight() / 2) - 150);
        g.drawString(middleText, (getWidth() / 2) - (middleTextWidth / 2), (getHeight() / 2));
        g.drawString(bottomText, (getWidth() / 2) - (bottomTextWidth / 2), (getHeight() / 2) + 150);
    }

    /**
     * Paint our SelectionTwo screen frame.
     * This includes the information about what button to choose to select a right paddle mode.
     */
    private void paintSelectionTwoFrame(Graphics g) {
        g.setFont(new Font(getFont().getName(), getFont().getStyle(), 50));

        // All of our hardcoded messages.
        final String topText = "Click '1' to make the Right paddle computer operated.";
        final String middleText = "Click '2' to make the Right paddle player operated.";
        final String bottomText = "Click '3' to make the Right paddle a wall.";

        // Find the string width of each.
        final int topTextWidth = g.getFontMetrics().stringWidth(topText);
        final int middleTextWidth = g.getFontMetrics().stringWidth(middleText);
        final int bottomTextWidth = g.getFontMetrics().stringWidth(bottomText);

        // Scale the position of each String into a proper position.
        g.drawString(topText, (getWidth() / 2) - (topTextWidth / 2), (getHeight() / 2) - 150);
        g.drawString(middleText, (getWidth() / 2) - (middleTextWidth / 2), (getHeight() / 2));
        g.drawString(bottomText, (getWidth() / 2) - (bottomTextWidth / 2), (getHeight() / 2) + 150);
    }

    /**
     * Paint our controls screen frame.
     * This includes information on how to control paddles (if needed) and how to pause.
     */
    private void paintControlsFrame(Graphics g) {
        g.setFont(new Font(getFont().getName(), getFont().getStyle(), 40));

        // All of our hardcoded messages.
        final String topText = "Controls:";
        final String middleTextTop = "Use " + pong.getStageHandler().getLeftKeyUp() + " to move the left paddle up, use " + pong.getStageHandler().getLeftKeyDown() + " to move the left paddle down.";
        final String middleTextMiddle = "Use P to pause/unpause the game.";
        final String middleTextBottom = "Use " + pong.getStageHandler().getRightKeyUp() + " to move the right paddle up, use " + pong.getStageHandler().getRightKeyDown() + " to move the right paddle down.";
        final String bottomText = "Click any key to continue.";

        // Find the string width of each.
        final int topTextWidth = g.getFontMetrics().stringWidth(topText);
        final int middleTextTopWidth = g.getFontMetrics().stringWidth(middleTextTop);
        final int middleTextMiddleWidth = g.getFontMetrics().stringWidth(middleTextMiddle);
        final int middleTextBottomWidth = g.getFontMetrics().stringWidth(middleTextBottom);
        final int bottomTextWidth = g.getFontMetrics().stringWidth(bottomText);

        // Scale the position of each String into a proper position.
        g.drawString(topText, (getWidth() / 2) - (topTextWidth / 2), (getHeight() / 2) - 200);
        if (pong.getStageHandler().isLeftPlayer())
            g.drawString(middleTextTop, (getWidth() / 2) - (middleTextTopWidth / 2), (getHeight() / 2) - 100);
        g.drawString(middleTextMiddle, (getWidth() / 2) - (middleTextMiddleWidth / 2), (getHeight() / 2));
        if (pong.getStageHandler().isRightPlayer())
            g.drawString(middleTextBottom, (getWidth() / 2) - (middleTextBottomWidth / 2), (getHeight() / 2) + 100);
        g.drawString(bottomText, (getWidth() / 2) - (bottomTextWidth / 2), (getHeight() / 2) + 200);
    }

    /**
     * Paint our general game frame.
     * This includes all the balls, all the paddles, and a line down the center of the screen.
     * We also scale this based on the resolution of our window.
     */
    private void paintGameFrame(Graphics g, double widthMultiplier, double heightMultiplier) {
        setResizable(true);

        // DEBUG draw collision
        if (pong.lastBallCollisionX != 0 && pong.lastBallCollisionY != 0) {
            g.drawOval(pong.lastBallCollisionX, pong.lastBallCollisionY, 5, 5);
        }

        // Draw the current ms & fps on the screen.
        if (System.currentTimeMillis() - lastRecalculateFrameTimes >= 100) {
            lastRecalculateFrameTimes = System.currentTimeMillis();
            lastFps = calculateFps() + " fps";
            lastMs = String.format("%.4f", pong.getMsPerFrame()) + " ms";
        }
        frameDeltaTimeArray[lastFrameIndex] = pong.getMsPerFrame();
        lastFrameIndex = (lastFrameIndex + 1) % frameDeltaTimeArray.length;

        g.drawString(lastFps, (int) (getWidth() * 0.01f), (int) (getHeight() * 0.06f));
        g.drawString(lastMs, (int) (getWidth() * 0.01f), (int) (getHeight() * 0.08f));

        // Draw a line down the center of the screen.
        g.drawLine(getWidth() / 2, 0, getWidth() / 2, getHeight());

        // Go through all of our objects and draw them on the screen.
        for (Object object : pong.getObjects()) {
            //DEBUG line for ball
            if (object instanceof Ball) {
                int x1 = object.getCoordinateX() + (StageHandler.BALL_DIAMETER / 2);
                int y1 = object.getCoordinateY() + (StageHandler.BALL_DIAMETER / 2);
                int x2 = pong.lastBallCollisionX;
                int y2 = pong.lastBallCollisionY;

                double degree = RayCastUtil.calculateDirectionDegrees(x1, y1, x2, y2);

                // Calculate the endpoint of the line by extending it from the center of the object
                int lineLength = -2000; // You can adjust the length as needed
                int x2Line = x1 + (int) (lineLength * Math.cos(Math.toRadians(degree)));
                int y2Line = y1 + (int) (lineLength * Math.sin(Math.toRadians(degree)));

                g.drawLine(pong.lastBallCollisionX, pong.lastBallCollisionY, x2Line, y2Line);
            }
            g.fillRect((int) (object.getCoordinateX() * widthMultiplier),
                    (int) (object.getCoordinateY() * heightMultiplier),
                    (int) (object.getWidth() * widthMultiplier),
                    (int) (object.getHeight() * heightMultiplier));
        }
    }

    /**
     * Paint our pause screen frame.
     * This involves just a simple message on how to unpause.
     */
    private void paintPauseFrame(Graphics g) {
        setResizable(false);

        final String text = "Press P to unpause.";

        g.setFont(new Font(getFont().getName(), getFont().getStyle(), getWidth() / (text.length() * 2)));

        g.drawString(text, (getWidth() / 2) - (g.getFontMetrics().stringWidth(text) / 2), getHeight() / 2);
    }

    private String calculateFps() {
        double total = 0.0d;
        for (double deltaTime : frameDeltaTimeArray) {
            total += deltaTime;
        }
        double averageFrameTime = total / frameDeltaTimeArray.length;
        double fps = 1000.0 / averageFrameTime;
        return String.format("%.0f", fps);
    }
}