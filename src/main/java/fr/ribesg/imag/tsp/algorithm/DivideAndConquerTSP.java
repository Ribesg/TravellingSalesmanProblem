package fr.ribesg.imag.tsp.algorithm;
import fr.ribesg.imag.tsp.collection.PointList;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * A DivideAndConquer algorithm.
 * <p/>
 * This algorithm stops dividing and uses the Greedy algorithm at some
 * point, to prevent stack overflows on big datas, and because it's actually
 * faster.
 * <p/>
 * NOTE: THIS IS STILL A WORK IN PROGRESS /!\
 *
 * @author Ribesg
 */
public class DivideAndConquerTSP implements TSP {

	// Pool of threads
	private static final ExecutorService executors = Executors.newCachedThreadPool();

	// Limit at which we stop dividing
	private final int threshold;

	public DivideAndConquerTSP(int threshold) {
		this.threshold = threshold;
	}

	@Override
	public PointList run(PointList list) {
		if (list.size() > threshold) {
			final PointList[] quarters = list.divide();
			final PointList result = new PointList(list.size(), list.getMinX(), list.getMaxX(), list.getMinY(), list.getMaxY());

			final List<Callable<PointList>> tasks = new ArrayList<>(4);
			for (int i = 0; i < quarters.length; i++) {
				final int index = i;
				tasks.add(new Callable<PointList>() {

					@Override
					public PointList call() throws Exception {
						return new DivideAndConquerTSP(threshold).run(quarters[index]);
					}
				});
			}
			try {
				final List<Future<PointList>> results = executors.invokeAll(tasks);
				result.append(results.get(0).get());
				result.append(results.get(1).get());
				result.append(results.get(3).get());
				result.append(results.get(2).get());
				return result;
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
				return null;
			}
		} else {
			return new GreedyTSP().run(list);
		}
	}
}
