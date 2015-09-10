package nz.ac.vuw.ecs.snails.test;

import static org.junit.Assert.assertTrue;
import javafx.geometry.Point3D;
import javafx.scene.transform.Rotate;
import nz.ac.vuw.ecs.fgpj.core.GPConfig;
import nz.ac.vuw.ecs.fgpj.core.GeneticProgram;
import nz.ac.vuw.ecs.snails.functions.Cos;
import nz.ac.vuw.ecs.snails.functions.Sin;
import nz.ac.vuw.ecs.snails.functions.T;
import nz.ac.vuw.ecs.snails.main.SnailFitness;

import org.junit.Test;

public class FitnessTests {

	@Test
	public void testRotation() {
		SnailFitness sf = new SnailFitness(null);

		for (int i = 0; i < 100; i++) {
			Point3D r1 = new Point3D(Math.random() * 5, Math.random() * 2,
					Math.random() * 6);
			Point3D r2 = new Point3D(Math.random() * 5, Math.random() * 2,
					Math.random() * 6);

			Rotate r = sf.getRotationBetween(r1, r2);

			assertTrue(r.transform(r1).normalize().subtract(r2.normalize()).magnitude() < 0.001);
		}

	}
	
	@Test
	public void testFitness(){
		String programString = "3 Program0 ( Cos t ) | Program1 ( Sin t ) | Program2 t |";
		String programString2 = "3 Program0 ( Sin t ) | Program1 ( Cos t ) | Program2 t |";
		
		GeneticProgram p1 = new GeneticProgram(3);
		GeneticProgram p2 = new GeneticProgram(3);
		
		GPConfig conf = new GPConfig(3, 1 , 6, 0.28,0.7,0.02);
		conf.addTerminal(new T());
		conf.addFunction(new Sin());
		conf.addFunction(new Cos());
		
		p1.parseProgram(programString, conf);
		p2.parseProgram(programString2, conf);
		
		SnailFitness sf = new SnailFitness(null);
		sf.initFitness();
		sf.assignFitness(p1, conf);
		sf.assignFitness(p2, conf);
		
//		System.out.println(p1.getFitness());
//		System.out.println(p2.getFitness());
		
		assertTrue(p1.getFitness() < p2.getFitness());
	}
}
