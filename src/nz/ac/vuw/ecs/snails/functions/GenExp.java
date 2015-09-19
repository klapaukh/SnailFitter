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
 * This class represents the exponentiation operator. It takes the results of its two
 * children and returns the first raised to the power of the second
 *
 * @author Roman Klapaukh
 *
 */
public class GenExp extends Function {

	public GenExp() {
		super(ReturnDouble.TYPENUM, 2, "^");
		for (int i = 0; i < numArgs; i++) {
			// Set the expected return type of each child to ReturnDouble
			setArgNReturnType(i, ReturnDouble.TYPENUM);
		}
	}

	@Override
	public GenExp getNew(GPConfig config) {
		return new GenExp();
	}

	@Override
	public void evaluate(ReturnData out) {
		ReturnDouble d = (ReturnDouble) out;
		getArgN(0).evaluate(d);
		
		double d1 = d.value();
		getArgN(1).evaluate(d);
		
		d.setValue(Math.pow(d.value(), d1));
	}

}
