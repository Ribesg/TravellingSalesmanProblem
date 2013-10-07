package fr.ribesg.imag.tsp.utils;
import fr.ribesg.imag.tsp.TSPMain;

public class Timer {

	private long startTime, endTime;

	public Timer start() {
		startTime = System.nanoTime();
		return this;
	}

	public Timer stop() {
		endTime = System.nanoTime();
		return this;
	}

	public long nanoDiff() {
		if (startTime != -1 && endTime != -1) {
			return endTime - startTime;
		} else {
			return -1;
		}
	}

	public String diffString() {
		return parseDiff(nanoDiff());
	}

	public long hotNanoDiff() {
		return System.nanoTime() - startTime;
	}

	public String hotDiffString() {
		return parseDiff(hotNanoDiff());
	}

	public static String parseDiff(final long nano) {
		if (nano < 1_000L) {
			return nano + "ns";
		} else if (nano < 1_000_000L) {
			return TSPMain.getFormatter().format(nano / 1_000D) + "µs";
		} else if (nano < 1_000_000_000L) {
			return TSPMain.getFormatter().format(nano / 1_000_000D) + "ms";
		} else {
			return TSPMain.getFormatter().format(nano / 1_000_000_000D) + "s";
		}
	}
}