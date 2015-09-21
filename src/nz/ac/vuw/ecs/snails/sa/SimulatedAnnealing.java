package nz.ac.vuw.ecs.snails.sa;

import java.util.List;
import java.util.Random;

import javafx.geometry.Point3D;

public class SimulatedAnnealing {

	private int maxIterations = 10000;
	private float maxTemperature = 1000.0f;
	private float minTemperature = 0.00001f;
	private Scale scale;
	private Random r;

	public SimulatedAnnealing() {
		scale = new GeometricScale(maxIterations, minTemperature, maxTemperature);
		r = new Random();
	}

	public RaupState minimise(List<Point3D> reference) {
		RaupState s = new RaupState();
		double distance = s.distanceTo(reference); // the error between s and the reference

		for (int k = 0; k < maxIterations; k++) {
			double temperature = temperature(k);
			RaupState sNew = neighbour(s);

			double distanceNew = sNew.distanceTo(reference); //The error between Snew and the reference
			if (r.nextDouble() < accept(distance, distanceNew, temperature)) {
				s = sNew;
				distance = distanceNew;
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

	public static void main(String[] args){


	}
}
