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
 * Implements a Sin function node for GP with ReturnDoubles. It has one subtree
 * which must return a ReturnDouble
 *
 * @author Roman Klapaukh
 *
 */
public class Sin extends Function implements DifferentiableNode {

	public Sin() {
		// This node returns a ReturnDouble, has one subtree and is called "Sin"
		super(ReturnDouble.TYPENUM, 1, "Sin");
		for (int i = 0; i < numArgs; i++) {
			// Set the expected return value of each child to ReturnDouble
			setArgNReturnType(i, ReturnDouble.TYPENUM);
		}
	}

	@Override
	public Sin getNew(GPConfig config) {
		// Return a new Sin
		return new Sin();
	}

	@Override
	public void evaluate(ReturnData out) {
		// Cast is safe as we specified our return type
		ReturnDouble d = (ReturnDouble) out;
		// Evaluate the subtree
		getArgN(0).evaluate(d);
		// Set the result to be sin(subtreeResult)
		d.setValue(Math.sin(d.value()));
	}

	@Override
	public Times differentiate(GPConfig conf) {
		Times times = new Times();
		Cos cos = new Cos();
		Node c = getArgN(0).copy(conf);
		Node gprime = ((DifferentiableNode)c).differentiate(conf);
		cos.setArgN(0, c);

		times.setArgN(0, cos);
		times.setArgN(1, gprime);

		return times;
	}

}
