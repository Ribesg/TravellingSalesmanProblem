package fr.ribesg.imag.tsp;
import fr.ribesg.imag.tsp.algorithm.DivideAndConquerTSP;
import fr.ribesg.imag.tsp.algorithm.GreedyTSP;
import fr.ribesg.imag.tsp.algorithm.TSP;
import fr.ribesg.imag.tsp.collection.PointList;
import fr.ribesg.imag.tsp.file.Reader;
import fr.ribesg.imag.tsp.file.Writer;
import fr.ribesg.imag.tsp.utils.Timer;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.DefaultXYDataset;

import javax.swing.*;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

/**
 * This is the main class of the TSP solver.
 *
 * @author Ribesg
 */
public class TSPMain {

	private static DecimalFormat format;

	public static DecimalFormat getFormatter() {
		if (format == null) {
			format = new DecimalFormat("#0.00");
			format.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.ENGLISH));
		}
		return format;
	}

	private static final Random rand = new Random();

	public static void main(final String[] args) {
		if (!new TSPMain().exec(args)) {
			System.out.println("Usage: ");
			System.out.println("\t--nbPoints X                | Choose number of points");
			System.out.println("\t--divLimit X                | Choose a threshold for Divide & Conquer");
			System.out.println("\t--graph                     | Show graphs (not compatible with --bench)");
			System.out.println("\t--bench X                   | Benchmark mode, re-run X times (not compatible with --graph)");
			System.out.println("\t--file internal N           | N = Name of the data set, uses the internal file");
			System.out.println("\t--file F N                  | F = Path to the file ; N = Name of the data set");
			System.out.println("\t                            | File handling is not compatible with --nbPoints");
			System.out.println("\t--write                     | Write the result points to a result.txt file");
			System.out.println("\t                            | --write not available in Benchmark mode");
			System.out.println("\t--algo <greedy|divide|both> | Write the result points to a result.txt file");
			System.out.println("\t                            | --write not available in Benchmark mode");
			System.out.println();
			System.out.println("\t--help / -h / help | Show this help");
		}
	}

	private boolean exec(final String[] args) {
		// Initialize values to default
		int nbPoints = 10_000;
		int divLimit = 20;
		boolean graphical = false;
		boolean benchmark = false;
		int benchmarkAmount = 10;
		String fileName = null;
		String dataSetName = null;
		boolean write = false;
		boolean greedy = true;
		boolean divide = true;

		// Try to get arguments
		try {
			boolean algo = false;
			for (int i = 0; i < args.length; i++) {
				switch (args[i].toLowerCase()) {
					case "--nbpoints":
						nbPoints = Integer.parseInt(args[i + 1]);
						break;
					case "--divlimit":
						divLimit = Integer.parseInt(args[i + 1]);
						break;
					case "--graph":
						graphical = true;
						break;
					case "--bench":
						benchmark = true;
						benchmarkAmount = Integer.parseInt(args[i + 1]);
						if (graphical) {
							// Benchmark mode and graphical mode are not compatible
							return false;
						}
						break;
					case "--file":
						fileName = args[i + 1];
						dataSetName = args[i + 2];
						break;
					case "--write":
						write = true;
						break;
					case "help":
					case "--help":
					case "-h":
						return false;
					case "--algo":
						switch (args[i + 1].toLowerCase()) {
							case "greedy":
								greedy = true;
								divide = false;
								break;
							case "divide":
								greedy = false;
								divide = true;
								break;
							case "both":
								greedy = true;
								divide = true;
								break;
							default:
								return false;
						}
						break;
					default:
						break;
				}
			}
		} catch (Exception e) {
			// Catch every possible Exception here, bad design but prevents
			// having to handle every possible error specifically
			// (Especially IndexOutOfBound)
			return false;
		}

		// The data we will play on
		PointList points;

		// File mode
		if (fileName != null && dataSetName != null)

		{
			System.out.println("File mode selected");
			Map<String, PointList> content;
			if (fileName.equalsIgnoreCase("internal")) {
				content = Reader.read();
			} else {
				content = Reader.read(fileName);
			}
			if (content == null) {
				return true;
			}
			points = content.get(dataSetName);
			if (points == null) {
				System.out.println("\t'" + dataSetName + "' not found in file '" + fileName + "', aborting.");
				return true;
			} else {
				System.out.println("\t'" + dataSetName + "' successfully loaded from file '" + fileName + "'");
			}
		}
		// Random mode
		else {
			System.out.println("Random mode selected");
			System.out.println("\tCreating data structure...");
			points = new PointList(nbPoints, 0d, 1d, 0d, 1d);
			System.out.println("\tGenerating " + nbPoints + " random points...");
			for (int i = 0; i < nbPoints; i++) {
				points.add(rand.nextFloat(), rand.nextFloat());
			}
			System.out.println("\tDone.");
		}

		if (!benchmark) {
			if (greedy) {
				System.out.println("Executing Greedy algorithm");
				final Timer timer1 = new Timer().start();
				final PointList result1 = new GreedyTSP().run(points);
				timer1.stop();
				if (graphical) {
					final StringBuilder titleBuilder = new StringBuilder();
					titleBuilder.append("Greedy Algorithm Result on ");
					titleBuilder.append(points.size());
					titleBuilder.append(" points - Total length: ");
					titleBuilder.append(getFormatter().format(result1.getTotalLength(true)));
					titleBuilder.append(" - Elapsed time: ");
					titleBuilder.append(timer1.diffString());
					this.show(titleBuilder.toString(), result1);
				}
				System.out.println("\tDone.");
				System.out.println("\tElapsed time: " + timer1.diffString());
				System.out.println("\tTotal length found: " + result1.getTotalLength(true));
				if (write) {
					System.out.println("\tWriting result to file...");
					Writer.write(result1, "resultG.txt");
					System.out.println("\tDone.");
				}
			}
			// ############ //
			if (divide) {
				System.out.println("Executing Divide & Conquer algorithm");
				final Timer timer2 = new Timer().start();
				final PointList result2 = new DivideAndConquerTSP(divLimit).run(points);
				timer2.stop();
				if (graphical) {
					final StringBuilder titleBuilder = new StringBuilder();
					titleBuilder.append("Divide & Conquer Algorithm Result on ");
					titleBuilder.append(points.size());
					titleBuilder.append(" points - Total length: ");
					titleBuilder.append(getFormatter().format(result2.getTotalLength(true)));
					titleBuilder.append(" - Elapsed time: ");
					titleBuilder.append(timer2.diffString());
					this.show(titleBuilder.toString(), result2);
				}
				System.out.println("\tDone.");
				System.out.println("\tElapsed time: " + timer2.diffString());
				System.out.println("\tTotal length found: " + result2.getTotalLength(true));
				if (write) {
					System.out.println("\tWriting result to file...");
					Writer.write(result2, "resultD&C.txt");
					System.out.println("\tDone.");
				}
			}
		} else /* Benchmark mode */ {
			System.out.println("Executing benchmark mode (" + benchmarkAmount + " times)");
			long totalGreedy = 0;
			final TSP greedyAlgo = new GreedyTSP();
			final TSP divideAlgo = new DivideAndConquerTSP(divLimit);
			long totalDivide = 0;
			for (int i = 0; i < benchmarkAmount; i++) {
				System.out.print("\tExecution Greedy number " + i + "... ");
				final Timer timer1 = new Timer().start();
				greedyAlgo.run(points);
				totalGreedy += timer1.stop().nanoDiff();
				System.out.println("Done.");
				System.out.print("\tExecution Divide and Conquer number " + i + "... ");
				final Timer timer2 = new Timer().start();
				divideAlgo.run(points);
				totalDivide += timer2.stop().nanoDiff();
				System.out.println("Done.");
			}
			final long averageGreedy = totalGreedy / benchmarkAmount;
			final long averageDivide = totalDivide / benchmarkAmount;
			System.out.println("Benchmark terminated! Results:");
			System.out.println("\tAverage Greedy Algorithm duration: " + Timer.parseDiff(averageGreedy));
			System.out.println("\tAverage Divide Algorithm duration: " + Timer.parseDiff(averageDivide));
		}

		return true;
	}

	/**
	 * This method opens a JDialog with a graph in another Thread.
	 *
	 * @param title  the title of the graph
	 * @param points the points to be shown
	 */
	private void show(final String title, final PointList points) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				final DefaultXYDataset data = new DefaultXYDataset();
				data.addSeries("Points", points.toDoubleMap(true));
				final PlotOrientation orientation = PlotOrientation.VERTICAL;
				final JFreeChart chart = ChartFactory.createXYLineChart(title, "X", "Y", data, orientation, false, false, false);

				final ChartPanel chartPanel = new ChartPanel(chart);

				final JDialog dialog = new JDialog();
				dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
				dialog.getContentPane().add(chartPanel);
				dialog.pack();
				dialog.setSize(1024, 768);
				dialog.setVisible(true);
			}
		}).run();
	}
}
