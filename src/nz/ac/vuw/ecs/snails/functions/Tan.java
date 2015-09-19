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
 * Implements the Tan function for ReturnDoubles. It computes the tangent of the
 * one subtree it has which returns a ReturnDouble. Note that tangent is
 * discontinuous
 *
 * @author roma
 *
 */
public class Tan extends Function {

	public Tan() {
		// This Function returns a Return double, has one child and is written
		// "Tan"
		super(ReturnDouble.TYPENUM, 1, "Tan");
		for (int i = 0; i < numArgs; i++) {
			// for each child, set the return type of the child to be
			// ReturnDouble
			setArgNReturnType(i, ReturnDouble.TYPENUM);
		}
	}

	@Override
	public Tan getNew(GPConfig config) {
		// Return a new Tan
		return new Tan();
	}

	@Override
	public void evaluate(ReturnData out) {
		// Cast is safe as we specified we are expecting this
		ReturnDouble d = (ReturnDouble) out;
		// Evaluate the subtree
		getArgN(0).evaluate(d);
		// Set the result to being tan(subtree)
		d.setValue(Math.tan(d.value()));
	}

}
