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
 * Function representing taking the natural logarithm of a number
 *
 * @author Roman Klapaukh
 *
 */
public class Ln extends Function {

	public Ln() {
		// This returns a ReturnDouble, has one child and is written "ln"
		super(ReturnDouble.TYPENUM, 1, "ln");
		for (int i = 0; i < numArgs; i++) {
			// set the expected child return type to being ReturnDouble
			setArgNReturnType(i, ReturnDouble.TYPENUM);
		}
	}

	@Override
	public Ln getNew(GPConfig config) {
		// Return a new Ln
		return new Ln();
	}

	@Override
	public void evaluate(ReturnData out) {
		// The cast is safe as we specified what we expect in the constructor
		ReturnDouble d = (ReturnDouble) out;
		// evaluate the subtree
		getArgN(0).evaluate(d);
		// The result is ln(subtree)
		// You can't just take a natural log.
		if (d.value() < 0.00005) {
			d.setValue(0);
		} else {
			d.setValue(Math.log(d.value()));
		}
	}

}
