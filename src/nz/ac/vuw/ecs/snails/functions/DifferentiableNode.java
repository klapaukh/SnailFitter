package nz.ac.vuw.ecs.snails.functions;

import nz.ac.vuw.ecs.fgpj.core.GPConfig;
import nz.ac.vuw.ecs.fgpj.core.Node;

/**
 * This represents a Node in a tree which can be differentiated. This will be
 * the core of how we create landmarks for the fitness function.
 *
 * @author Roman Klapaukh
 *
 */
public interface DifferentiableNode {

	/**
	 * Returns the subtree that will return the derivative of this node.
	 *
	 * @return A subtree representing the derivative
	 */
	public <F extends Node> F differentiate(GPConfig conf);

}
