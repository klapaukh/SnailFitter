package nz.ac.vuw.ecs.snails.main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
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
			values.add(new Point3D(Math.cos(t), Math.sin(t), t));
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

	public Point3D toPoint3D(ReturnDouble[] d) {
		return new Point3D(d[0].value(), d[1].value(), d[2].value());
	}

	public double getTOffset(GeneticProgram p, ReturnDouble[] d, double oldT, double targetDistance) {
		return getTOffsetLinear(p, d, oldT, targetDistance);
	}

	public double getTOffsetLinear(GeneticProgram p, ReturnDouble[] d, double oldT, double targetDistance) {
		// TODO make this even vaguely sensible
		return oldT + 0.01;
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

		ReturnDouble d[] = new ReturnDouble[] { new ReturnDouble(), new ReturnDouble(), new ReturnDouble() };

		// The zeroth point is always the same. In fact it provides the
		// translation required.
		// The translation is then constantly applied to all points to ensure
		// that the origins line up. This means it has zero error
		setT(d, 0);
		p.evaluate(d);
		Point3D translation = values.get(0);
		Point3D model0 = toPoint3D(d);
		translation = translation.subtract(model0);

		// System.out.println(translation.toString());

		double distanceTravelled = values.get(1).distance(values.get(0));
		Point3D targetVector = values.get(1);

		double firstT = getTOffset(p, d, 0, distanceTravelled);

		setT(d, firstT);
		p.evaluate(d);

		// Remember to translate to the transformed origin
		Point3D model1 = toPoint3D(d).subtract(translation);

		Rotate r = getRotationBetween(model1, targetVector);
		// System.out.println(r.toString());

		// Test each program on every point in the hash map and sum the squared
		// error
		double t = 0;
		for (int i = 1; i < values.size(); i++) {
			t = getTOffset(p, d, t, values.get(i).subtract(values.get(i - 1)).magnitude());
			// System.out.println(t);
			setT(d, t);
			p.evaluate(d);
			Point3D genPoint = toPoint3D(d).subtract(translation);
			genPoint = r.transform(genPoint);
			list.add(genPoint);
		}

		return list;

	}

	public void draw(GeneticProgram p, String filename, GPConfig config) {
		try {
			PrintStream out = null;
			out = new PrintStream(new File(filename));
			out.println("x,y,z");
			List<Point3D> points = genPoints(p, config);
			for (Point3D genPoint : points) {
				out.printf("%f,%f,%f\n", genPoint.getX(), genPoint.getY(), genPoint.getZ());
			}
			out.close();
		} catch (FileNotFoundException e) {
			System.err.println("Failed to write the best individual to a file");
			e.printStackTrace();
		}
	}

}
