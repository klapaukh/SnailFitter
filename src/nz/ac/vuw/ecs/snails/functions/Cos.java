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
import nz.ac.vuw.ecs.fgpj.core.ReturnData;

/**
 * Implements a Cos function node for GP with ReturnDoubles. It has one subtree
 * which must return a ReturnDouble
 *
 * @author Roman Klapaukh
 *
 */
public class Cos extends Function {

	public Cos() {
		// This node returns a ReturnDouble, has one subtree and is called "Cos"
		super(ReturnDouble.TYPENUM, 1, "Cos");
		for (int i = 0; i < numArgs; i++) {
			// Set the expected return value of each child to ReturnDouble
			setArgNReturnType(i, ReturnDouble.TYPENUM);
		}
	}

	@Override
	public Cos getNew(GPConfig config) {
		// Return a new Cos
		return new Cos();
	}

	@Override
	public void evaluate(ReturnData out) {
		// Cast is safe as we specified our return type
		ReturnDouble d = (ReturnDouble) out;
		// Evaluate the subtree
		getArgN(0).evaluate(d);
		// Set the result to be cos(subtreeResult)
		d.setValue(Math.cos(d.value()));
	}

}
