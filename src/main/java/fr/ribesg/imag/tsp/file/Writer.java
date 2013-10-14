package fr.ribesg.imag.tsp.file;
import fr.ribesg.imag.tsp.collection.PointList;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Writer {

	public static void write(final PointList toBeWritten, final String fileName) {
		try (final BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
			final float[][] floatMap = toBeWritten.getFloatMap();
			for (int i = 0; i < toBeWritten.size(); i++) {
				writer.write(Float.toString(floatMap[i][0]) + ' ' + Float.toString(floatMap[i][1]) + '\n');
			}
		} catch (IOException e) {
			System.out.println("\tFailed to write to '" + fileName + "', aborting.");
		}
	}
}
