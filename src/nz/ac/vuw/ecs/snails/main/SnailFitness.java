package nz.ac.vuw.ecs.snails.main;

/*
 SnailFitter snail fitting library
 Copyright (C) 2015  Roman Klapaukh

 This program is free software; you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation; either version 2 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License along
 with this program; if not, write to the Free Software Foundation, Inc.,
 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

import java.util.ArrayList;
import java.util.List;

import javafx.geometry.Point3D;
import javafx.scene.transform.Rotate;
import nz.ac.vuw.ecs.fgpj.core.Fitness;
import nz.ac.vuw.ecs.fgpj.core.GPConfig;
import nz.ac.vuw.ecs.fgpj.core.GeneticProgram;
import nz.ac.vuw.ecs.snails.functions.DifferentiableNode;
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

	@Override
	public int compare(double arg0, double arg1) {
		// Must multiply by -1 as smaller is better rather than larger is better
		// as in normal doubles
		return -1 * Double.compare(arg0, arg1);
	}

	@Override
	/**
	 * This populates the values hash map will all of the test cases that will be used for training
	 */
	public void initFitness() {
		// Create the hashmap
		values = new ArrayList<Point3D>();

		// TODO: This should actually read in a file
		for (double t = 0; t < 100; t += 0.1) {
			values.add(new Point3D(Math.cos(t), Math.sin(t), t));
		}

	}

	public boolean isDirty() {
		// Fitness function never changes
		return false;
	}

	@Override
	public void assignFitness(GeneticProgram p, GPConfig config) {
		// Create space for the return values and variables
		ReturnDouble d[] = new ReturnDouble[] { new ReturnDouble(),new ReturnDouble(),new ReturnDouble() };

		// The derivatives let us estimate the speed of line length
		GeneticProgram dp = new GeneticProgram(3);
		for (int i = 0; i < 3; i++) {
			dp.setRoot(
					((DifferentiableNode) p.getRoot(i)).differentiate(config),
					i);
		}

//		System.out.println(dp.toString());
		// The zeroth point is always the same. In fact it provides the
		// translation required.
		// The translation is then constantly applied to all points to ensure
		// that the origins line up. This means it has zero error
		setT(d, 0);
		p.evaluate(d);
		Point3D translation = values.get(0);
		Point3D model0 = toPoint3D(d);
		translation = translation.subtract(model0);
		
//		System.out.println(translation.toString());

		double distanceTravelled = values.get(1).distance(values.get(0));
		Point3D targetVector = values.get(1);

		double firstT = getTOffset(dp, d, 0, distanceTravelled);

		setT(d, firstT);
		p.evaluate(d);

		// Remember to translate to the transformed origin
		Point3D model1 = toPoint3D(d).subtract(translation);

		Rotate r = getRotationBetween(model1, targetVector);
//		System.out.println(r.toString());
		

		// Test each program on every point in the hash map and sum the squared
		// error
		double t = 0;
		// total error starts at zero
		double error = 0;
		for (int i = 1; i< values.size(); i++) {
			t = getTOffset(dp,d, t, values.get(i).subtract(values.get(i-1)).magnitude());
//			System.out.println(t);
			setT(d, t);
			p.evaluate(d);
			Point3D genPoint = toPoint3D(d).subtract(translation);
			genPoint = r.transform(genPoint);
			error += Math.pow(genPoint.subtract(values.get(i)).magnitude(), 2);
		}
		// Make into RMS error and assign to the program
		error /= values.size();
		p.setFitness(Math.sqrt(error));

	}
	
	public Rotate getRotationBetween(Point3D source, Point3D target) {
		// The rotation is the one which will align targetVector with model1
		target.angle(source);
		Rotate r = new Rotate(source.angle(target),
				source.crossProduct(target));
		return r;
	}

	public Point3D toPoint3D(ReturnDouble[] d) {
		return new Point3D(d[0].value(), d[1].value(), d[2].value());
	}

	public double getTOffset(GeneticProgram dt, ReturnDouble[] d, double oldT,
			double targetDistance) {
		setT(d, oldT);
		dt.evaluate(d);
		double speed = toPoint3D(d).magnitude();

		return oldT + targetDistance / speed;
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

}
