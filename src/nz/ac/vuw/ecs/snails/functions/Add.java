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
 * This class represents the addition operator. It takes the results of its two children and returns the sum of them as Java doubles
 *
 * @author Roman Klapaukh
 *
 */
public class Add extends Function implements DifferentiableNode {

	public Add() {
		// The add Function returns a ReturnDouble, has 2 children and is represented by "+"
		super(ReturnDouble.TYPENUM, 2, "+");
		for (int i = 0; i < numArgs; i++) {
			// Set the expected return type of each child to ReturnDouble
			setArgNReturnType(i, ReturnDouble.TYPENUM);
		}
	}

	@Override
	public Add getNew(GPConfig config) {
		// Return a new Add
		return new Add();
	}

	@Override
	public void evaluate(ReturnData out) {
		// Cast is safe as we specified what the expected type is
		ReturnDouble d = (ReturnDouble) out;
		// Evaluate the first subtree
		getArgN(0).evaluate(d);
		// save the value
		double d1 = d.value();
		// evaluate the second subtree
		getArgN(1).evaluate(d);
		// Set the result to be the sum
		d.setValue(d.value() + d1);
	}

	@Override
	public <F extends Node> F differentiate() {
		// TODO Auto-generated method stub
		return null;
	}

}
