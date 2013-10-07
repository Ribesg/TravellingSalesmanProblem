package fr.ribesg.imag.tsp.algorithm;
import fr.ribesg.imag.tsp.collection.PointList;

/**
 * The Greedy algorithm. Simple.
 *
 * @author Ribesg
 */
public class GreedyTSP implements TSP {

	@Override
	public PointList run(PointList list) {
		// For each point, we seek for the closest one in [i+1..n],
		// and permut it with the point at index i+1
		for (int i = 0; i < list.size() - 2; i++) {
			list.permut(i + 1, list.getClosestPointForIndex(i));
		}
		return list;
	}
}
