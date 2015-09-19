package nz.ac.vuw.ecs.snails.main;

import java.awt.Color;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.math.plot.Plot3DPanel;

import javafx.geometry.Point3D;
import nz.ac.vuw.ecs.fgpj.core.ConfigModifier;
import nz.ac.vuw.ecs.fgpj.core.GPConfig;
import nz.ac.vuw.ecs.fgpj.core.GeneticProgram;
import nz.ac.vuw.ecs.fgpj.core.Population;

/**
 * The class will create a GUI that shows the best result so far against the
 * reference shell. It will update as the evolution progresses. To prevent this
 * from holding up execution, as much as possible of this job will run in a
 * separate thread.
 * 
 * @author Roman Klapaukh
 *
 */
public class ProgressTracker implements ConfigModifier {

	SnailFitness sfit;
	Plot3DPanel plot;
	ExecutorService tp;

	/**
	 * Create new ProgressTracker. This requires a SnailFitness to both get the
	 * reference curve and then later to evaluate the best program and gets its
	 * curve.
	 * 
	 * @param fit
	 *            The SnailFitness to use for the reference curve and then later
	 *            for getting the curves of generated solutions.
	 */
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
		plot.addScatterPlot("Best individual", Color.RED, x, y, z);

		// put the PlotPanel in a JFrame, as a JPanel
		JFrame frame = new JFrame("Snail Hugging Progress");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setContentPane(plot);
		frame.setSize(1000, 800);
		frame.setVisible(true);

		// Don't want this to get in the way of other stuff so lock it to only
		// having a single thread. The fitness evaluation should always have
		// most of the cores available to it
		tp = Executors.newSingleThreadExecutor();
	}

	@Override
	public void ModifyConfig(GPConfig g, Population pop) {
		GeneticProgram p = pop.getBest();
		if(p.lastChange() > 0){
			//Don't waste CPU cycles on elitism
			return; 
		}
		// Don't actually do it in the main program thread. Just add it to the
		// todo
		tp.submit(new UpdateJob(p.copy(g), g));
	}

	/**
	 * This represents a single update the plot task.
	 * 
	 * @author Roman Klapaukh
	 *
	 */
	private class UpdateJob implements Runnable {
		private final GeneticProgram p;
		private final GPConfig conf;

		/**
		 * Create a new update the plot task.
		 * 
		 * @param p
		 *            The individual to plot
		 * @param conf
		 *            The GPConfig being used
		 * 
		 */
		public UpdateJob(GeneticProgram p, GPConfig conf) {
			this.p = p;
			this.conf = conf;
		}

		/**
		 * Use the SnailFitness to generate the curve of the program and then
		 * update the plot
		 */
		public void run() {
			List<Point3D> points = sfit.genPoints(p, conf);

			double[][] XY = new double[points.size()][3];
			for (int i = 0; i < points.size(); i++) {
				Point3D pi = points.get(i);
				XY[i][0] = pi.getX();
				XY[i][1] = pi.getY();
				XY[i][2] = pi.getZ();

			}

			plot.changePlotData(1, XY);
			SwingUtilities.invokeLater(()-> plot.repaint());
		}
	}

}
