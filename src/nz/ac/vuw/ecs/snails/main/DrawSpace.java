package nz.ac.vuw.ecs.snails.main;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.swing.JFrame;

import org.math.plot.Plot3DPanel;

import javafx.geometry.Point3D;
import nz.ac.vuw.ecs.snails.sa.RaupState;

public class DrawSpace {

	public static void main(String[] args) {
		if (args.length < 1 || args.length > 2) {
			System.err.println("Usage:");
			System.err.println("java -jar sa.jar <targetFilename> number");
			System.exit(-1);
		}

		String filename = args[0];
		int numberOfRuns = Integer.parseInt(args[1]);

		final List<Point3D> values = new ArrayList<>();

		try {
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

		ExecutorService threadPool = Executors.newFixedThreadPool(2 * Runtime.getRuntime().availableProcessors());

		List<Future<Point3D>> results = new ArrayList<>();

		double wMin = 1;
		double wMax = 10000;

		double tMin = 0;
		double tMax = 4;

		for (double w = wMin; w <= wMax; w += (wMax - wMin) / numberOfRuns) {
			for (double t = tMin; t <= tMax; t += (tMax - tMin) / numberOfRuns) {
				final double wf = w;
				final double tf = t;
				results.add(threadPool.submit(() -> {
					RaupState s = new RaupState(1, 0, 1, wf, tf);
					return new Point3D(wf, tf, s.distanceTo(values));
				}));
			}
		}

		threadPool.shutdown();
		
		Plot3DPanel plot = new Plot3DPanel();
		double[] x = new double[results.size()];
		double[] y = new double[results.size()];
		double[] z = new double[results.size()];

		int i = 0;
		for (Future<Point3D> s : results) {
			try {
				Point3D pi = s.get();
				x[i] = pi.getX();
				y[i] = pi.getY();
				z[i] = pi.getZ() < 1 ? pi.getZ():Math.log(pi.getZ());
				i++;
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
		}

		// add the reference curve
		plot.addScatterPlot("Reference", Color.BLUE, x, y, z);
		
		JFrame frame = new JFrame("Snail Hugging Progress");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setContentPane(plot);
		frame.setSize(1000, 800);
		frame.setVisible(true);
		
	}

}
