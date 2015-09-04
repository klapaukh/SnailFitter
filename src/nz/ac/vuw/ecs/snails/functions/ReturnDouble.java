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

import nz.ac.vuw.ecs.fgpj.core.ReturnData;

/**
 * The return double represents the return value from a computation. In the case
 * of symbolic regression a double is sufficient, but in other cases, much more
 * complex data types may be needed. This implementation is intended to be
 * efficient and thread safe. It has support for GP trees which have at most two
 * input variables, though can easily be extended to handle more.
 *
 * This class stores the input values to the GP program. This seems like
 * behaviour that should not be attributed to this class, but rather should be a
 * part of the X and Y classes. However, there is no easy way to give the values
 * for each computation to the X and Y instances. While static fields can be
 * used, doing that is not thread safe and will break any attempts to use
 * parallel fitness evaluation. As the systems that this is developed on are all
 * multicore, this functionality was considered to be important. By attaching
 * the values to this class, which is already being passed all the way through
 * the tree, we can easily give the X and Ys the appropriate values, without any
 * performance penalty of having an additional pass.
 *
 * @author roma
 *
 */
public class ReturnDouble extends ReturnData {

	// Space to hold the correct value for the current input data(T)
	private double t;

	// Magic constant that identifies what the return type of this value is.
	// Must not clash with any other
	// in the same GP run. This is not checked.
	public final static int TYPENUM = 4;

	// The return value that it will hold
	private double value;

	/**
	 * Set the value of this return double to a specific value
	 *
	 * @param val
	 *            The new value that this return double contains
	 */
	public void setValue(double val) {
		this.value = val;
	}

	/**
	 * Get the value that this ReturnDouble represents
	 *
	 * @return the double contained within
	 */
	public double value() {
		return value;
	}

	/**
	 * Constructor. Needs no arguments
	 */
	public ReturnDouble() {
		// This informs allows for TypeNum tests without knowing the exact type
		// by making it available in the supertype
		super(TYPENUM);
	}

	/**
	 * Get the T input value this is carrying. Should only be used by the X
	 * class.
	 *
	 * @return the t input value carried
	 */
	public double getT() {
		return t;
	}

	/**
	 * Set the T input value for this computation
	 *
	 * @param t
	 *            the X input value
	 */
	public void setT(double t) {
		this.t = t;
	}

}
