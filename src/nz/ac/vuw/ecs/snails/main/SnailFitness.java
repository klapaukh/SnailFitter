package nz.ac.vuw.ecs.snails.main;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javafx.geometry.Point3D;
import javafx.scene.transform.Rotate;
import nz.ac.vuw.ecs.fgpj.core.Fitness;
import nz.ac.vuw.ecs.fgpj.core.GPConfig;
import nz.ac.vuw.ecs.fgpj.core.GeneticProgram;
import nz.ac.vuw.ecs.snails.functions.ReturnDouble;

/**
 * SymbolicFitness attempts to be a general fitness function using Root Mean
 * Squared error for the fitness. It should be able to generalise to most single
 * variable symbolic regression problems. It also is thread safe with regards to
 * being used with the ParalellFitness class.
 *
 * @author Roman Klapaukh
 *
 */
public class SnailFitness extends Fitness {
	// The values are all the points of the Snail model
	private List<Point3D> values;
	private final String filename;

	public SnailFitness(String filename) {
		this.filename = filename;
	}

	@Override
	public int compare(double arg0, double arg1) {
		// Must multiply by -1 as smaller is better rather than larger is better
		// as in normal doubles
		return -1 * Double.compare(arg0, arg1);
	}

	public void loadFile(String filename) {
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
	}

	@Override
	/**
	 * This populates the values hash map will all of the test cases that will
	 * be used for training
	 */
	public void initFitness() {
		if (filename != null) {
			loadFile(filename);
			return;
		}
		values = new ArrayList<Point3D>();

		for (double t = 0; t < 100; t += 0.1) {
			values.add(new Point3D(t, t, t));
		}

	}

	public List<Point3D> getReferenceCurve() {
		return new ArrayList<Point3D>(values);
	}

	public boolean isDirty() {
		// Fitness function never changes
		return false;
	}

	@Override
	public void assignFitness(GeneticProgram p, GPConfig config) {

		List<Point3D> points = genPoints(p, config);
		double error = rmse(values, points);
		p.setFitness(error);

	}

	public double rmse(List<Point3D> reference, List<Point3D> generated) {
		double error = 0;
		for (int i = 0; i < generated.size(); i++) {
			Point3D r = reference.get(i);
			Point3D g = generated.get(i);

			error += Math.pow(r.subtract(g).magnitude(), 2);
		}
		return Math.sqrt(error);
	}

	public Rotate getRotationBetween(Point3D source, Point3D target) {
		// The rotation is the one which will align targetVector with model1
		target.angle(source);
		Rotate r = new Rotate(source.angle(target), source.crossProduct(target));
		return r;
	}

	public Point3D toPoint3D(double t, ReturnDouble[] d) {
		return new Point3D(t, d[0].value(), d[1].value());
	}

	public void setT(ReturnDouble[] d, double v) {
		for (ReturnDouble rd : d) {
			rd.setT(v);
		}
	}

	@Override
	public boolean solutionFound(List<GeneticProgram> pop) {
		for (GeneticProgram p : pop) {
			// There is a solution if any program has a fitness of 0
			if (Double.compare(p.getFitness(), 0) == 0) {
				return true;
			}
		}
		// otherwise, they can still get better
		return false;
	}

	@Override
	public void finish() {
		// There is no required clean up for this fitness function.
	}

	public List<Point3D> genPoints(GeneticProgram p, GPConfig config) {
		// Create space for the return values and variables
		List<Point3D> list = new ArrayList<>();

		ReturnDouble d[] = new ReturnDouble[] {new ReturnDouble(), new ReturnDouble()};

		for (int i = 0; i < values.size(); i++) {
			Point3D t = values.get(i);
			setT(d, t.getX());
			p.evaluate(d);
			Point3D genPoint = toPoint3D(t.getX(),d);
			list.add(genPoint);
		}

		return list;

	}

}
