package nz.ac.vuw.ecs.snails.functions;

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

import nz.ac.vuw.ecs.fgpj.core.Function;
import nz.ac.vuw.ecs.fgpj.core.GPConfig;
import nz.ac.vuw.ecs.fgpj.core.Node;
import nz.ac.vuw.ecs.fgpj.core.ReturnData;

/**
 * This class implements a multiplication function node. It has no state, and
 * simply returns the product of two subtrees which both return ReturnDoubles.
 *
 * @author roma
 *
 */
public class Times extends Function implements DifferentiableNode{

	public Times() {
		// This class return a ReturnDouble, it has two children, and it prints
		// itself as "*"
		super(ReturnDouble.TYPENUM, 2, "*");
		for (int i = 0; i < numArgs; i++) {
			// Each of the children must return a ReturnDouble to type correctly
			setArgNReturnType(i, ReturnDouble.TYPENUM);
		}
	}

	@Override
	public Times getNew(GPConfig config) {
		// Return a new instance of yourself
		return new Times();
	}

	@Override
	public void evaluate(ReturnData out) {
		// Safe as we specified that is the type we expect
		ReturnDouble d = (ReturnDouble) out;

		// Evaluate the first subtree
		getArgN(0).evaluate(d);
		// Save the value of the first tree
		double d1 = d.value();
		// Evaluate the second subtree
		getArgN(1).evaluate(d);
		// Return the product of the two subtrees (as this is multiplication)
		d.setValue(d.value() * d1);
	}

	@Override
	public Add differentiate(GPConfig conf) {
		Add add = new Add();

		Node f = getArgN(0).copy(conf);
		Node g = getArgN(0).copy(conf);

		Node fprime = ((DifferentiableNode)f).differentiate(conf);
		Node gprime = ((DifferentiableNode)f).differentiate(conf);

		Times right = new Times();
		Times left = new Times();

		left.setArgN(0,f);
		left.setArgN(1,gprime);

		right.setArgN(0,fprime);
		right.setArgN(1,g);

		add.setArgN(0, left);
		add.setArgN(1, right);

		return add;
	}

}
