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
 * Function representing raising e to the power of another value.
 *
 * @author Roman Klapaukh
 *
 */
public class Exp extends Function {

	public Exp() {
		// This returns a ReturnDouble, has one child and is written "e"
		super(ReturnDouble.TYPENUM, 1, "e");
		for (int i = 0; i < numArgs; i++) {
			// set the expected child return type to being ReturnDouble
			setArgNReturnType(i, ReturnDouble.TYPENUM);
		}
	}

	@Override
	public Exp getNew(GPConfig config) {
		// Return a new Exp
		return new Exp();
	}

	@Override
	public void evaluate(ReturnData out) {
		// The cast is safe as we specified what we expect in the constructor
		ReturnDouble d = (ReturnDouble) out;
		// evaluate the subtree
		getArgN(0).evaluate(d);
		// The result is e^(subtree)
		d.setValue(Math.exp(d.value()));
	}

}
