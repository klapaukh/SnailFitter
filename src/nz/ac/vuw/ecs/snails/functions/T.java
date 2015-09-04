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

import nz.ac.vuw.ecs.fgpj.core.GPConfig;
import nz.ac.vuw.ecs.fgpj.core.ReturnData;
import nz.ac.vuw.ecs.fgpj.core.Terminal;
import nz.ac.vuw.ecs.fgpj.core.Node;

/**
 * This class represents a single input value called t. This is the input to the
 * curve generator. It does not store the value it returns directly and takes
 * its value from the return double being passed though. This is because this is
 * a fast mechanism for doing this that is also thread safe allowing us to use
 * the ParallelFitness function.
 *
 * @author Roman Klapaukh
 *
 */
public class T extends Terminal implements DifferentiableNode {

	public T() {
		// We return a ReturnDouble and print ourselves as "t"
		super(ReturnDouble.TYPENUM, "t");
	}

	@Override
	public T getNew(GPConfig config) {
		// calls own constructor
		return new T();
	}

	@Override
	public void evaluate(ReturnData out) {
		// Safely can case to ReturnDouble as we specified that is what we
		// expect
		ReturnDouble d = (ReturnDouble) out;
		// We just return the value give to use by the ReturnDouble
		d.setValue(d.getT());
	}

	public RandomDouble differentiate(){
		return new RandomDouble(1);
	}


}
