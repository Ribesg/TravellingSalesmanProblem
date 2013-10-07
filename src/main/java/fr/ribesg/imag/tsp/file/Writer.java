package fr.ribesg.imag.tsp.file;
import fr.ribesg.imag.tsp.collection.PointList;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Writer {

	private static final String RESULT_FILENAME = "result.txt";

	public static void write(final PointList toBeWritten) {
		try (final BufferedWriter writer = new BufferedWriter(new FileWriter(RESULT_FILENAME))) {
			final float[][] floatMap = toBeWritten.getFloatMap();
			for (int i = 0; i < toBeWritten.size(); i++) {
				writer.write(Float.toString(floatMap[i][0]) + ' ' + Float.toString(floatMap[i][1]) + '\n');
			}
		} catch (IOException e) {
			System.out.println("\tFailed to write to '" + RESULT_FILENAME + "', aborting.");
		}
	}
}
