package nz.ac.vuw.ecs.snails.main;

import java.awt.Color;
import java.util.List;

import javax.swing.JFrame;

import org.math.plot.Plot3DPanel;

import javafx.geometry.Point3D;
import nz.ac.vuw.ecs.fgpj.core.ConfigModifier;
import nz.ac.vuw.ecs.fgpj.core.GPConfig;
import nz.ac.vuw.ecs.fgpj.core.GeneticProgram;
import nz.ac.vuw.ecs.fgpj.core.Population;

public class ProgressTracker implements ConfigModifier {

	SnailFitness sfit;
	Plot3DPanel plot;

	public ProgressTracker(SnailFitness fit) {
		this.sfit = fit;
		List<Point3D> points = fit.getReferenceCurve();

		plot = new Plot3DPanel();

		double[] x = new double[points.size()];
		double[] y = new double[points.size()];
		double[] z = new double[points.size()];

		for (int i = 0; i < points.size(); i++) {
			Point3D pi = points.get(i);
			x[i] = pi.getX();
			y[i] = pi.getY();
			z[i] = pi.getZ();
		}

		// add the reference curve
		plot.addScatterPlot("Reference", Color.BLUE, x, y, z);

		// Add a space for the current best to be displayed
		plot.addScatterPlot("Best individual", Color.RED, x,y,z);

		// put the PlotPanel in a JFrame, as a JPanel
		JFrame frame = new JFrame("Snail Hugging Progress");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setContentPane(plot);
		frame.setSize(1000, 800);
		frame.setVisible(true);

	}

	@Override
	public void ModifyConfig(GPConfig g, Population pop) {
		GeneticProgram p = pop.getBest();
		List<Point3D> points = sfit.genPoints(p, g);
		
		double[][] XY = new double[points.size()][3];
		for(int i =0 ; i < points.size(); i++){
			Point3D pi = points.get(i);
			XY[i][0] = pi.getX();
			XY[i][1] = pi.getY();
			XY[i][2] = pi.getZ();
			
		}
		
		plot.changePlotData(1, XY);
		plot.repaint();
		
	}

}
