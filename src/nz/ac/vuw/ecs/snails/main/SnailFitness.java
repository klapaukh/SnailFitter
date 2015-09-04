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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.geometry.Point3D;
import nz.ac.vuw.ecs.fgpj.core.Fitness;
import nz.ac.vuw.ecs.fgpj.core.GPConfig;
import nz.ac.vuw.ecs.fgpj.core.GeneticProgram;
import nz.ac.vuw.ecs.fgpj.core.Node;
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
	//The values are all the points of the Snail model
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
		//TODO put numbers in values

	}

	public boolean isDirty() {
		// Fitness function never changes
		return false;
	}

	@Override
	public void assignFitness(GeneticProgram p, GPConfig config) {
		// Create space for the return values and variables
		ReturnDouble d[] = new ReturnDouble[] { new ReturnDouble() };

		DifferentiableNode n0 = (DifferentiableNode) p.getRoot(0);
		DifferentiableNode n1 = (DifferentiableNode) p.getRoot(0);
		DifferentiableNode n2 = (DifferentiableNode) p.getRoot(0);

		//The derivatives let us estimate the speed of line length
		Node dn0 = n0.differentiate(config);
		Node dn1 = n1.differentiate(config);
		Node dn2 = n2.differentiate(config);

		// total error starts at zero
		double error = 0;

		// Test each program on every point in the hash map and sum the squared
		// error
		float t = 0;
		for (Point3D c : values) {
			d[0].setT(t);
			p.evaluate(d);
			error += Math.pow(d[0].value() - c.getX(), 2);
		}
		// Make into RMS error and assign to the program
		error /= values.size();
		p.setFitness(Math.sqrt(error));

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
