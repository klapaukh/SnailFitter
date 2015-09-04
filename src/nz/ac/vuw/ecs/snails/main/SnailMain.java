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
import nz.ac.vuw.ecs.snails.functions.Divide;
import nz.ac.vuw.ecs.snails.functions.Minus;
import nz.ac.vuw.ecs.snails.functions.RandomDouble;
import nz.ac.vuw.ecs.snails.functions.ReturnDouble;
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

		// Flag to determine if the fitness function should be run in parallel
		boolean parallel = false;

		// The GPConfig is the settings for the GP algorithm. It controls all
		// the parameters and how the algorithm behaves. It sets mutation,
		// crossover and elistism to their default functions. These can of
		// course be simply replaced by directly assigning to them if special
		// versions are required.
		// Here we set the following parameters in order
		// numParts = 1. Our trees only have one root, as there is only 1 result
		// for each input value
		// minDepth = 1. Program trees must have depth at least 1. Or else they
		// might be silly
		// maxDepth = 5. Program trees cannot be deeper than 5 levels. This is
		// because the larger the tree is
		// the more complexity, and the slower it runs. We assume that 5 is deep
		// enough for any
		// problem this example will be used on
		// mutationRate = 0.6. Each generation 60% of the new population will
		// come from mutation
		// crossoverRate = 0.38. Each generation 38% of the new population will
		// come from crossover
		// elitismRate = 0.02. Each generation 2% of the population will come
		// from elitism. While elitism is
		// is good in that it preserves good programs, too much of it will not
		// allow
		// many new programs to be created.
		GPConfig conf = new GPConfig(1, 1, 6, 0.28, 0.70, 0.02);

		// Each generation basic statistics about that generation will be logged
		// to this file.
		conf.setLogFile("run-log.txt");

		// Every 10,000 generations, write the current population to a file
		conf.loggingFrequency(10000);

		// For selection, use tournament selection with a pool of size 5. We use
		// tournament selection
		// instead of roulette wheel, because the fitness function uses error as
		// the fitness which is, lower is better. Roulette wheel is good for the
		// opposite case,
		// however, its general statement doesn't work as well for this case.
		conf.selectionOperator = new TournamentSelection(5);

		// Add the terminals
		// This is the X terminal. It takes a value from the input. Essentially,
		// as X is the only input variable the program will be a function of at
		// most 1 variable
		conf.addTerminal(new T());
		// Add a random double terminal that ranges from [1,5]
		conf.addTerminal(new RandomDouble(1, 5, conf));
		// conf.addTerminal(new RandomInt(1, 5, conf)); // same as above but
		// integer

		// Add mathematical operators to the function set
		// commented out functions are available in the library and can be
		// safely uncommented
		conf.addFunction(new Add());
		conf.addFunction(new Times());
		conf.addFunction(new Minus());
		conf.addFunction(new Divide());
		// conf.addFunction(new Exp());
		// conf.addFunction(new Sin());
		// conf.addFunction(new Min());
		// conf.addFunction(new Max());
		// conf.addFunction(new Tan());

		// Create and appropriate fitness function, based the parallel value at
		// the start of the method
		if (parallel) {
			// This creates a parallel fitness function. It takes the normal
			// fitness function as an argument
			// and transparently parallelised the checking of fitness. There is
			// only one instance of the actual fitness function -
			// SymbolicFitness - and it just has its methods called on it by
			// several different threads
			// If your fitness function or program representation are not thread
			// safe, then this class should not be used
			// The additional parameters are the number of threads (4) and the
			// size of the chunk alloted
			// to each thread in the thread pool. It is important to note that
			// the number of threads should
			// not be as large as possible, but rather it should be about the
			// number of cores present in the computer to minimise overheads.
			// The size of the chunk also has problems if it gets to big or too
			// small. It will not cause an error to occur, but will result in
			// suboptimal performance
			conf.fitnessObject = new ParallelFitness<SnailFitness>(new SnailFitness(), 4, 125);
		} else {

			// Create an instance of the SymbolicFitness class without any fancy
			// automagical parallelism
			conf.fitnessObject = new SnailFitness();
		}

		// Create a population of programs (that is currently empty).
		// It takes a size (100) which defines how many individuals there will
		// be, and also
		// a GP config so it knows about the terminal and function sets
		Population p = new Population(1000, conf);

		// Set the return type of the root node. This is important to ensure
		// that the program returns the right thing. If there are multiple roots
		// (not in this example) then the overloaded version that takes an index
		// must be used.
		p.setReturnType(ReturnDouble.TYPENUM);

		// p.readFromFile("population.txt")//the population can be generated
		// from a file (like the logs) if needed

		// Generate the intial population for the GP run. Assumes that
		// setReturnType has been called
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
