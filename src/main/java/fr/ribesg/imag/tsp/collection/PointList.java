package fr.ribesg.imag.tsp.collection;
import java.util.Arrays;

/**
 * This collection stores an array of points.
 * It has a lot of handy methods to play with the array,
 * like dividing it in multiple arrays, appending it to another array,
 * find distances between points and permut points.
 *
 * @author Ribesg
 */
public class PointList {

	// The square in which all those points are
	private final double minX, maxX, minY, maxY;

	// The array of points
	private float[][] points;

	// This is used to add new points to the correct location
	private int nextAvailableIndex;

	public PointList(final int initialSize, final double minX, final double maxX, final double minY, final double maxY) {
		this.points = new float[initialSize][2];
		this.nextAvailableIndex = 0;
		this.minX = minX;
		this.maxX = maxX;
		this.minY = minY;
		this.maxY = maxY;
	}

	// ### Collection methods ### //

	public void add(final float x, final float y) {
		this.points[this.nextAvailableIndex][0] = x;
		this.points[this.nextAvailableIndex][1] = y;
		this.nextAvailableIndex++;
	}

	public int size() {
		return this.points.length;
	}

	public void trim() {
		this.points = Arrays.copyOf(this.points, this.nextAvailableIndex);
	}

	public void append(final PointList list) {
		final float[][] map = list.getFloatMap();
		if (map.length > 0) {
			final float[] last = this.points[this.points.length - 1];
			final boolean reverse = distanceSquared(map[0], last) > distanceSquared(map[map.length - 1], last);
			if (reverse) {
				for (int i = map.length - 1; i >= 0; i--) {
					this.add(map[i][0], map[i][1]);
				}
			} else {
				for (int i = 0; i < map.length; i++) {
					this.add(map[i][0], map[i][1]);
				}
			}
		}
	}

	// ### Array modification and conversion methods ### //

	public void permut(final int i, final int j) {
		final float[] tmp = this.points[i];
		this.points[i] = this.points[j];
		this.points[j] = tmp;
	}

	/** Converts this PointList to something that JFreeChart likes */
	public double[][] toDoubleMap(final boolean closeTour) {
		final double[][] result = new double[2][this.points.length + (closeTour ? 1 : 0)];
		for (int i = 0; i < this.points.length; i++) {
			result[0][i] = this.points[i][0];
			result[1][i] = this.points[i][1];
		}
		if (closeTour) {
			result[0][this.points.length] = this.points[0][0];
			result[1][this.points.length] = this.points[0][1];
		}
		return result;
	}

	/** Divides this PointList into 4 PointList */
	public PointList[] divide() {
		final PointList[] result = new PointList[4];
		result[0] = this.getQuarter(minX, minX + (maxX - minX) / 2f, minY, minY + (maxY - minY) / 2f);
		result[1] = this.getQuarter(minX, minX + (maxX - minX) / 2f, minY + (maxY - minY) / 2f, maxY);
		result[2] = this.getQuarter(minX + (maxX - minX) / 2f, maxX, minY, minY + (maxY - minY) / 2f);
		result[3] = this.getQuarter(minX + (maxX - minX) / 2f, maxX, minY + (maxY - minY) / 2f, maxY);
		return result;
	}

	/** Gets all points in a square in this PointList as a new PointList */
	private PointList getQuarter(final double minX, final double maxX, final double minY, final double maxY) {
		final PointList result = new PointList(this.size(), minX, maxX, minY, maxY);
		for (final float[] p : this.points) {
			if (p[0] >= minX && p[0] < maxX && p[1] >= minY && p[1] < maxY) {
				result.add(p[0], p[1]);
			}
		}
		result.trim();
		return result;
	}

	// ### Computation methods ### //

	public float distanceSquared(final float[] p1, final float[] p2) {
		final float xDiff = p2[0] - p1[0];
		final float yDiff = p2[1] - p1[1];
		return xDiff * xDiff + yDiff * yDiff;
	}

	public float distanceSquared(final int i, final int j) {
		final float[] p1 = this.points[i];
		final float[] p2 = this.points[j];
		return distanceSquared(p1, p2);
	}

	public double distance(final int i, final int j) {
		return Math.sqrt(this.distanceSquared(i, j));
	}

	/** Only searches in the [i+1..n] interval */
	public int getClosestPointForIndex(final int index) {
		float distanceSquared = Float.MAX_VALUE, tmp;
		int result = index;
		for (int i = index + 1; i < this.points.length; i++) {
			tmp = this.distanceSquared(index, i);
			if (tmp < distanceSquared) {
				result = i;
				distanceSquared = tmp;
			}
		}
		return result;
	}

	/** @param closeTour if we want the loop length or the start-end length */
	public double getTotalLength(final boolean closeTour) {
		double result = 0;
		for (int i = 0; i < this.points.length - 1; i++) {
			result += this.distance(i, i + 1);
		}
		if (closeTour) {
			result += this.distance(0, this.points.length - 1);
		}
		return result;
	}

	// ### Getters ### //

	public float[][] getFloatMap() {
		return this.points;
	}

	public double getMaxX() {
		return maxX;
	}

	public double getMaxY() {
		return maxY;
	}

	public double getMinX() {
		return minX;
	}

	public double getMinY() {
		return minY;
	}
}
