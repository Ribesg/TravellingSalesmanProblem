package fr.ribesg.imag.tsp.algorithm;
import fr.ribesg.imag.tsp.collection.PointList;

/**
 * Represents a algorithm solving the TSP
 *
 * @author Ribesg
 */
public interface TSP {

	public PointList run(final PointList list);
}
