package main.am.noah.utils;

public class RayCastUtil {

    public static double calculateDirectionDegrees(double x1, double y1, double x2, double y2) {
        // Calculate the direction in radians
        double directionRadians = Math.atan2(y2 - y1, x2 - x1);

        // Convert radians to degrees
        double directionDegrees = directionRadians * (180 / Math.PI);

        // Ensure the result is between 0 and 360 degrees
        if (directionDegrees < 0) {
            directionDegrees += 360;
        }

        return directionDegrees;
    }
}
