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
 * The division operator. In the case of division by zero, it has the result zero.
 *
 * @author Roman Klapaukh
 *
 */
public class Divide extends Function implements DifferentiableNode {

	public Divide() {
		//Divide returns a ReturnDouble, has to children and is represented by "/"
		super(ReturnDouble.TYPENUM, 2, "/");
		for (int i = 0; i < numArgs; i++) {
			//Set the expected return type of each child to ReturnDouble.
			setArgNReturnType(i, ReturnDouble.TYPENUM);
		}
	}

	@Override
	public Divide getNew(GPConfig config) {
		//Return a new Divide
		return new Divide();
	}

	@Override
	public void evaluate(ReturnData out) {
		//cast is safe as we specified the type we expect
		ReturnDouble d = (ReturnDouble) out;

		//Evaluate the first subtree
		getArgN(0).evaluate(d);
		//save the value
		double d1 = d.value();
		//evaluate the second subtree
		getArgN(1).evaluate(d);

		//Set the right return value
		if (d.value() == 0) {
			//if the bottom part is zero, just return zero
			d.setValue(0);
		} else {
			//actually divide
			d.setValue(d1 / d.value());
		}
	}

	@Override
	public <F extends Node> F differentiate(GPConfig conf) {
		// TODO Auto-generated method stub
		return null;
	}

}
