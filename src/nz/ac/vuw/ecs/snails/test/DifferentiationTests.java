package nz.ac.vuw.ecs.snails.test;

import static org.junit.Assert.assertTrue;
import nz.ac.vuw.ecs.fgpj.core.GPConfig;
import nz.ac.vuw.ecs.snails.functions.Add;
import nz.ac.vuw.ecs.snails.functions.Cos;
import nz.ac.vuw.ecs.snails.functions.Divide;
import nz.ac.vuw.ecs.snails.functions.Exp;
import nz.ac.vuw.ecs.snails.functions.Ln;
import nz.ac.vuw.ecs.snails.functions.Minus;
import nz.ac.vuw.ecs.snails.functions.RandomDouble;
import nz.ac.vuw.ecs.snails.functions.Sin;
import nz.ac.vuw.ecs.snails.functions.T;
import nz.ac.vuw.ecs.snails.functions.Times;

import org.junit.BeforeClass;
import org.junit.Test;

public class DifferentiationTests {

	static GPConfig conf;

	@BeforeClass
	public static void Setup() {
		conf = new GPConfig(0.3, 0.3, 0.4);
	}

	@Test
	public void testRandomDouble() {
		RandomDouble r = new RandomDouble(0, 5, conf);
		RandomDouble dr = r.differentiate(conf);
		assertTrue(dr.toString().equals("RandomDoublex0.000000"));
	}

	@Test
	public void testT() {
		T t = new T();
		RandomDouble dt = t.differentiate(conf);
		assertTrue(dt.toString().equals("RandomDoublex1.000000"));
	}

	@Test
	public void testSin() {
		Sin sin = new Sin();
		Times times = new Times();
		T t1 = new T();
		T t2 = new T();

		times.setArgN(0, t1);
		times.setArgN(1, t2);
		sin.setArgN(0, times);

		assertTrue(sin
				.differentiate(conf)
				.toString()
				.trim()
				.equals("( *  ( Cos  ( * t t )  )   ( +  ( * t RandomDoublex1.000000 )   ( * RandomDoublex1.000000 t )  )  )"));

	}

	@Test
	public void testCos() {
		Cos cos = new Cos();
		T t = new T();

		cos.setArgN(0, t);

		assertTrue(cos
				.differentiate(conf)
				.toString()
				.trim()
				.equals("( *  ( * RandomDoublex-1.000000  ( Sin t )  )  RandomDoublex1.000000 )"));
	}

	@Test
	public void testLn() {
		Ln ln = new Ln();
		T t = new T();

		ln.setArgN(0, t);

		assertTrue(ln.differentiate(conf).toString().trim()
				.equals("( / RandomDoublex1.000000 t )"));
	}

	@Test
	public void testLnProduct() {
		Ln ln = new Ln();
		Times times = new Times();
		T t1 = new T();
		T t2 = new T();

		times.setArgN(0, t1);
		times.setArgN(1, t2);

		ln.setArgN(0, times);

		assertTrue(ln
				.differentiate(conf)
				.toString()
				.trim()
				.equals("( /  ( +  ( * t RandomDoublex1.000000 )   ( * RandomDoublex1.000000 t )  )   ( * t t )  )"));
	}

	@Test
	public void testExp() {
		Exp exp = new Exp();
		T t = new T();

		exp.setArgN(0, t);

		assertTrue(exp.differentiate(conf).toString().trim()
				.equals("( *  ( e t )  RandomDoublex1.000000 )"));
	}
	
	@Test
	public void testAdd() {
		Add add = new Add();
		T t1 = new T();
		T t2 = new T();

		add.setArgN(0, t1);
		add.setArgN(1, t2);

		assertTrue(add.differentiate(conf).toString().trim()
				.equals("( + RandomDoublex1.000000 RandomDoublex1.000000 )"));
	}
	
	@Test
	public void testMinus() {
		Minus minus = new Minus();
		T t1 = new T();
		T t2 = new T();

		minus.setArgN(0, t1);
		minus.setArgN(1, t2);

		assertTrue(minus.differentiate(conf).toString().trim()
				.equals("( - RandomDoublex1.000000 RandomDoublex1.000000 )"));
	}
	
	@Test
	public void testDivide() {
		Divide divide = new Divide();
		T t1 = new T();
		T t2 = new T();

		divide.setArgN(0, t1);
		divide.setArgN(1, t2);

		assertTrue(divide.differentiate(conf).toString().trim()
				.equals("( /  ( -  ( * RandomDoublex1.000000 t )   ( * t RandomDoublex1.000000 )  )   ( * t t )  )"));
	}
}
