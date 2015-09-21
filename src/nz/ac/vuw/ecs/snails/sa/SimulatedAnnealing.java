package nz.ac.vuw.ecs.snails.sa;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javafx.geometry.Point3D;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.math.plot.Plot3DPanel;

public class SimulatedAnnealing {

	private int maxIterations = 10000;
	private float maxTemperature = 1000.0f;
	private float minTemperature = 0.00001f;
	private Scale scale;
	private Random r;
	private List<Point3D> reference;

	// Visualisation
	Plot3DPanel plot;
	ExecutorService tp;

	public SimulatedAnnealing(List<Point3D> reference) {
		scale = new GeometricScale(maxIterations, minTemperature, maxTemperature);
		r = new Random();
		this.reference = reference;
	}

	public RaupState minimise() {
		plot = new Plot3DPanel();

		double[] x = new double[reference.size()];
		double[] y = new double[reference.size()];
		double[] z = new double[reference.size()];

		for (int i = 0; i < reference.size(); i++) {
			Point3D pi = reference.get(i);
			x[i] = pi.getY() * Math.sin(pi.getX());
			y[i] = pi.getY() * Math.cos(pi.getX());
			z[i] = pi.getZ();
		}

		// add the reference curve
		plot.addScatterPlot("Reference", Color.BLUE, x, y, z);

		// Add a space for the current best to be displayed
		plot.addScatterPlot("Best individual", Color.RED, x, y, z);

		// put the PlotPanel in a JFrame, as a JPanel
		JFrame frame = new JFrame("Snail Hugging Progress");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setContentPane(plot);
		frame.setSize(1000, 800);
		frame.setVisible(true);

		// Don't want this to get in the way of other stuff so lock it to only
		// having a single thread. The fitness evaluation should always have
		// most of the cores available to it
		tp = Executors.newSingleThreadExecutor();

		RaupState s = new RaupState();
		double distance = s.distanceTo(reference); // the error between s and
													// the reference

		for (int k = 0; k < maxIterations; k++) {
			double temperature = temperature(k);
			RaupState sNew = neighbour(s);

			double distanceNew = sNew.distanceTo(reference); // The error
																// between Snew
																// and the
																// reference
			if (r.nextDouble() < accept(distance, distanceNew, temperature)) {
				s = sNew;
				distance = distanceNew;
				tp.submit(new UpdateJob(sNew));
			}
		}
		return s;
	}

	private RaupState neighbour(RaupState s) {
		return s.moveRandom(r, 0.1f);
	}

	private double temperature(int f) {
		return scale.scale(f);
	}

	private double accept(double oldCost, double newCost, double temperature) {
		if (newCost < oldCost) {
			return 1;
		}
		return Math.exp(-(newCost - oldCost) / temperature);
	}

	public static void main(String[] args) {
		String filename = "raupCyl.csv";
		List<Point3D> values = null;

		try {
			values = new ArrayList<>();
			Scanner scan = new Scanner(new File(filename));
			while (scan.hasNext()) {
				values.add(new Point3D(scan.nextDouble(), scan.nextDouble(), scan.nextDouble()));
			}
			scan.close();
		} catch (FileNotFoundException e) {
			System.err.println("Failed to read in reference model file");
			e.printStackTrace();
			System.exit(-1);
		}

		SimulatedAnnealing sa = new SimulatedAnnealing(values);
		RaupState s = sa.minimise();
		System.out.println(s.toString() + " - " + s.distanceTo(values));
	}

	/**
	 * This represents a single update the plot task.
	 *
	 * @author Roman Klapaukh
	 *
	 */
	private class UpdateJob implements Runnable {
		private final RaupState s;

		/**
		 * Create a new update the plot task.
		 *
		 * @param p
		 *            The individual to plot
		 * @param conf
		 *            The GPConfig being used
		 *
		 */
		public UpdateJob(RaupState s) {
			this.s = s;
		}

		/**
		 * Use the SnailFitness to generate the curve of the program and then
		 * update the plot
		 */
		public void run() {
			List<Point3D> points = s.genPoints(reference);

			double[][] XY = new double[points.size()][3];
			for (int i = 0; i < points.size(); i++) {
				Point3D pi = points.get(i);
				XY[i][0] = pi.getY() * Math.sin(pi.getX());
				XY[i][1] = pi.getY() * Math.cos(pi.getX());
				;
				XY[i][2] = pi.getZ();

			}

			SwingUtilities.invokeLater(() -> {
				plot.changePlotData(1, XY);
				plot.repaint();
			});
		}
	}

}
