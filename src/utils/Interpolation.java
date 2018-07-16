/*
 * Creation : 25 juin 2018
 */
package utils;

import java.util.Arrays;

public final class Interpolation {

    public static final double[] interpLinear(double[] x, double[] y, double[] xi) throws IllegalArgumentException {

        if (x.length != y.length) {
            throw new IllegalArgumentException("X and Y must be the same length");
        }
        if (x.length == 1) {
            throw new IllegalArgumentException("X must contain more than one value");
        }
        double[] dx = new double[x.length - 1];
        double[] dy = new double[x.length - 1];
        double[] slope = new double[x.length - 1];
        double[] intercept = new double[x.length - 1];

        // Calculate the line equation (i.e. slope and intercept) between each point
        for (int i = 0; i < x.length - 1; i++) {
            dx[i] = x[i + 1] - x[i];
            if (dx[i] == 0) {
                throw new IllegalArgumentException("X must be montotonic. A duplicate " + "x-value was found");
            }
            if (dx[i] < 0) {
                throw new IllegalArgumentException("X must be sorted");
            }
            dy[i] = y[i + 1] - y[i];
            slope[i] = dy[i] / dx[i];
            intercept[i] = y[i] - x[i] * slope[i];
        }

        // Perform the interpolation here
        double[] yi = new double[xi.length];
        for (int i = 0; i < xi.length; i++) {
            if ((xi[i] > x[x.length - 1]) || (xi[i] < x[0])) {
                yi[i] = Double.NaN;
            } else {
                int loc = Arrays.binarySearch(x, xi[i]);
                if (loc < -1) {
                    loc = -loc - 2;
                    yi[i] = slope[loc] * xi[i] + intercept[loc];
                } else {
                    yi[i] = y[loc];
                }
            }
        }

        return yi;
    }

    public static final double interpLinear2D(double[][] map, double xDes, double yDes) {

        double xMin = map[0][1];
        double xMax = map[0][map[0].length - 1];
        double yMin = map[1][0];
        double yMax = map[map.length - 1][0];

        double x0, x1, y0, y1, z00, z01, z10, z11, z_y0, z_y1;

        xDes = xDes < xMin ? xMin : xDes;
        xDes = xDes > xMax ? xMax : xDes;

        yDes = yDes < yMin ? yMin : yDes;
        yDes = yDes > yMax ? yMax : yDes;

        int idxX = 0;

        do {
            idxX++;
            x0 = map[0][idxX];
            x1 = map[0][idxX + 1];
        } while (!(xDes >= x0 && xDes <= x1));

        int idxY = 0;

        do {
            idxY++;
            y0 = map[idxY][0];
            y1 = map[idxY + 1][0];
        } while (!(yDes >= y0 && yDes <= y1));

        z00 = map[idxY][idxX];
        z01 = map[idxY][idxX + 1];
        z10 = map[idxY + 1][idxX];
        z11 = map[idxY + 1][idxX + 1];

        z_y0 = z00 + (z01 - z00) / (x1 - x0) * (xDes - x0);
        z_y1 = z10 + (z11 - z10) / (x1 - x0) * (xDes - x0);

        return z_y0 + (z_y1 - z_y0) / (y1 - y0) * (yDes - y0);

    }

}
