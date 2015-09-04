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

import nz.ac.vuw.ecs.fgpj.core.GPConfig;
import nz.ac.vuw.ecs.fgpj.core.GeneticProgram;
import nz.ac.vuw.ecs.fgpj.core.ParallelFitness;
import nz.ac.vuw.ecs.fgpj.core.Population;
import nz.ac.vuw.ecs.fgpj.core.TournamentSelection;
import nz.ac.vuw.ecs.snails.functions.Add;
import nz.ac.vuw.ecs.snails.functions.Cos;
import nz.ac.vuw.ecs.snails.functions.Exp;
import nz.ac.vuw.ecs.snails.functions.Minus;
import nz.ac.vuw.ecs.snails.functions.RandomDouble;
import nz.ac.vuw.ecs.snails.functions.ReturnDouble;
import nz.ac.vuw.ecs.snails.functions.Sin;
import nz.ac.vuw.ecs.snails.functions.T;
import nz.ac.vuw.ecs.snails.functions.Times;

/**
 * This is the main entry point to the snail comparer. Ideally this tool will be
 * responsible primarily for computation, and a different tool will be used for
 * visualisation.
 *
 * @author Roman Klapaukh
 *
 */
public class SnailMain {

	/**
	 * The main method. This will run the GP algorithm for the symbolic
	 * regression example
	 *
	 * @param args
	 *            The values passed in this array are ignored
	 */
	public static void main(String[] args) {

		// 3 trees - one for each dimension
		GPConfig conf = new GPConfig(3, 1, 6, 0.28, 0.70, 0.02);

		// Each generation basic statistics about that generation will be logged
		// to this file.
		conf.setLogFile("run-log.txt");

		// Every 10,000 generations, write the current population to a file
		conf.loggingFrequency(10000);

		// Error must be minimised so not roulette wheel.
		conf.selectionOperator = new TournamentSelection(5);

		// Add the terminals: t and a random number
		conf.addTerminal(new T());
		conf.addTerminal(new RandomDouble(1, 5, conf));

		// Add functions (only ones with derivatives
		conf.addFunction(new Add());
		conf.addFunction(new Times());
		conf.addFunction(new Minus());
		// conf.addFunction(new Divide());
		conf.addFunction(new Exp());
		conf.addFunction(new Sin());
		conf.addFunction(new Cos());
		// conf.addFunction(new Tan());
		// conf.addFunction(new Sec());
		// conf.addFunction(new ln());

		// Fitness functions
		conf.fitnessObject = new ParallelFitness<SnailFitness>(new SnailFitness(), 16, 125);

		// Create a population
		Population p = new Population(1000, conf);

		// Everything just returns a double
		p.setReturnType(ReturnDouble.TYPENUM);

		// p.readFromFile("population.txt")//the population can be generated
		// from a file (like the logs) if needed

		// Generate the intial population for the GP run.
		p.generateInitialPopulation();

		// Begin timing
		long start = System.currentTimeMillis();

		// Run the GP algorithm for 500 generations
		int numGenerations = p.evolve(1000); // return how many generations
												// actually happened
		if (numGenerations < 1000) {
			// If numGenerations < 500, then it terminated before the 500
			// generations finished
			// because it found a solution
			System.out.println("Terminated early");
		}
		// end timing
		long end = System.currentTimeMillis();

		// Get the best program
		GeneticProgram s = p.getBest();

		System.out.println("Best program fitness: " + s.getFitness());
		System.out.println("Number of generations this program has been selected for by elitism immediately prior: " + s.lastChange());
		System.out.println("Crossover usage (ignoring data from other parents): " + s.numCrossovers());
		System.out.println("Mutation usage (ignoring data from other parents): " + s.numMutations());
		System.out.println("Elitism usage (ignoring data from other parents): " + s.numElitisms());
		System.out.println("Best program:");
		System.out.println(s);

		System.out.println("Run time (excluding setup and tear down): " + (end - start) + "ms");

	}

}
