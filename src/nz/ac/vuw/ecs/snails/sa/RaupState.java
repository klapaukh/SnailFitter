package nz.ac.vuw.ecs.snails.sa;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javafx.geometry.Point3D;

public class RaupState {

	public final double w, r0, y0, rc, t;

	public RaupState(double r0, double y0, double rc, double w, double t) {
		this.w = w;
		this.rc = rc;
		this.r0 = r0;
		this.y0 = y0;
		this.t = t;
	}

	public RaupState(Random r) {
		this(r.nextDouble(), r.nextDouble(), r.nextDouble(), r.nextDouble() * 30 , r.nextDouble() * 5);
	}

	public RaupState moveRandom(Random r, double range) {
		double neww = w;
		double newr0 = r0;
		double newrc = rc;
		double newy0 = y0;
		double newt = t;

		double shift = (r.nextGaussian() * range);
		int position = r.nextInt(5);

		switch (position) {
		case 0:
			neww += shift;
			break;
		case 1:
			newt += shift;
			break;
		case 2:
			newrc += shift;
			break;
		case 3:
			newy0 += shift;
			break;
		case 4:
			newr0 += shift;
			break;
		default:
			throw new RuntimeException("Ah the move went wrong");
		}

		return new RaupState(newr0, newy0, newrc, neww, newt);
	}

	public String toString() {
		StringBuilder s = new StringBuilder();

		s.append("{w: ");
		s.append(w);
		s.append(", r0: ");
		s.append(r0);
		s.append(", rc: ");
		s.append(rc);
		s.append(", y0: ");
		s.append(y0);
		s.append(", t: ");
		s.append(t);
		s.append("}");
		return s.toString();
	}

	public double distanceTo(List<Point3D> reference) {
		List<Point3D> points = genPoints(reference);

		return rmse(reference, points);
	}

	public List<Point3D> genPoints(List<Point3D> reference){
		List<Point3D> points = new ArrayList<>();
		for (Point3D p : reference) {
			points.add(new Point3D(p.getX(), r(p.getX()), y(p.getX())));
		}
		return points;
	}

	private double r(double theta) {
		return r0 * Math.pow(w, theta / (2.0 * Math.PI));
	}

	private double y(double theta) {
		return y0 * Math.pow(w, theta / (2.0 * Math.PI)) + rc * t * (Math.pow(w, theta / (2.0 * Math.PI)) - 1);
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
}
