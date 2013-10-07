package fr.ribesg.imag.tsp.file;
import fr.ribesg.imag.tsp.collection.PointList;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Reader {

	private static final String INTERNAL_FILE = "data_tsp.txt";

	/**
	 * Reads a file with a weird python formatting and extracts lists of
	 * points from it.
	 *
	 * @param fileName the name of the file to be red
	 *
	 * @return a Map of Point List Name - PointList
	 */
	public static Map<String, PointList> read(final String fileName) {
		// Get the file
		final File file = new File(fileName);

		// Read the whole file
		String fileContent;
		try (final InputStream is = new FileInputStream(file)) {
			fileContent = readContent(is);
		} catch (FileNotFoundException e) {
			System.out.println("\tUnable to find file '" + fileName + "', aborting.");
			return null;
		} catch (IOException e) {
			System.out.println("\tError while reading file, aborting.");
			return null;
		}

		// Parse the file content
		return parse(fileContent);
	}

	/**
	 * Reads the internal file with a weird python formatting and extracts
	 * lists of points from it.
	 *
	 * @return a Map of Point List Name - PointList
	 */
	public static Map<String, PointList> read() {
		// Our final result
		final Map<String, PointList> result = new HashMap<>();

		// Read the whole file
		final String fileContent = readContent(Reader.class.getResourceAsStream('/' + INTERNAL_FILE));

		// Parse the file content
		return parse(fileContent);
	}

	private static Map<String, PointList> parse(final String fileContent) {
		// Our final result
		final Map<String, PointList> result = new HashMap<>();

		// Split the file content to get each block String
		final String[] datas = fileContent.split(";;");

		// This magic regex will extract the point list from the String
		final Pattern pattern = Pattern.compile("let (.+)=\\[\\|(.*)\\|\\]");

		for (String data : datas) {
			final Matcher match = pattern.matcher(data);
			if (match.find()) {
				final String name = match.group(1);
				final String content = match.group(2);

				final List<float[]> pointsList = new ArrayList<>();

				final String[] pointsStrings = content.split(";[ ]*");
				for (String point : pointsStrings) {
					final String[] xy = point.substring(1, point.length() - 1).split(", ");
					final double x = Double.parseDouble(xy[0]);
					final double y = Double.parseDouble(xy[1]);
					pointsList.add(new float[] {(float) x, (float) y});
				}

				final PointList dataResult = new PointList(pointsList.size(), 0d, 1d, 0d, 1d);

				for (final float[] xy : pointsList) {
					dataResult.add(xy[0], xy[1]);
				}

				result.put(name, dataResult);
			} else {
				System.out.println("\tFailed to parse data starting with '" + data.substring(0, Math.min(data.length(), 10)) + "'");
			}
		}
		return result;
	}

	private static String readContent(final InputStream is) {
		final StringBuilder result = new StringBuilder();
		try (final BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
			String line;
			while ((line = reader.readLine()) != null) {
				result.append(line);
			}
		} catch (final IOException e) {
			System.out.println("\tError while reading file, aborting.");
			return null;
		}
		return result.toString();
	}
}
