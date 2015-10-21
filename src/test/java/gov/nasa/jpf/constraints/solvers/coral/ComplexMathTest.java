/*
 * Copyright (C) 2015, United States Government, as represented by the 
 * Administrator of the National Aeronautics and Space Administration.
 * All rights reserved.
 *
 * The PSYCO: A Predicate-based Symbolic Compositional Reasoning environment 
 * platform is licensed under the Apache License, Version 2.0 (the "License"); you 
 * may not use this file except in compliance with the License. You may obtain a 
 * copy of the License at http://www.apache.org/licenses/LICENSE-2.0. 
 *
 * Unless required by applicable law or agreed to in writing, software distributed 
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR 
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the 
 * specific language governing permissions and limitations under the License.
 */
package gov.nasa.jpf.constraints.solvers.coral;

import gov.nasa.jpf.constraints.api.Expression;
import gov.nasa.jpf.constraints.api.Variable;
import gov.nasa.jpf.constraints.api.ConstraintSolver.Result;
import gov.nasa.jpf.constraints.expressions.Constant;
import gov.nasa.jpf.constraints.expressions.NumericBooleanExpression;
import gov.nasa.jpf.constraints.expressions.NumericComparator;
import gov.nasa.jpf.constraints.expressions.functions.FunctionExpression;
import gov.nasa.jpf.constraints.expressions.functions.math.MathFunctions;
import gov.nasa.jpf.constraints.types.BuiltinTypes;

import java.util.Properties;

import org.junit.Test;


/*
 * TODO: not tested: round (in coral: ROUND_) and log10 (in coral: lOG10_)
 */
public class ComplexMathTest {
	
	@Test
	public void simpleExp() {
		CoralSolver solver = TstUtil.createCoralSolver(new Properties());
		Variable<Double> y = new Variable<Double>(BuiltinTypes.DOUBLE, "y");

		Expression<Boolean> expr = new NumericBooleanExpression(
				y,
				NumericComparator.EQ, new FunctionExpression<>(
						MathFunctions.EXP, new Constant<Double>(BuiltinTypes.DOUBLE, 20.3)));

		TstUtil.runTest(solver, expr, Result.SAT, TstUtil.PRINT_CORAL_EXPR);
	}
	/*
	@Test
	public void simpleExp2() {
		CoralSolver solver = TstUtil.createCoralSolver(new Properties());
		Variable<Double> x = new Variable<Double>(BuiltinTypes.DOUBLE, "x");

		Expression<Boolean> expr = new NumericBooleanExpression(
				new Constant<Double>(BuiltinTypes.DOUBLE, 20.5),
				NumericComparator.EQ, new FunctionExpression<>(
						MathFunctions.EXP, x));

		TstUtil.runTest(solver, expr, Result.SAT, TstUtil.PRINT_CORAL_EXPR);
	}*/
	
	@Test
	public void simplePow1() {
		CoralSolver solver = TstUtil.createCoralSolver(new Properties());
		Variable<Double> x = new Variable<Double>(BuiltinTypes.DOUBLE, "x");

		Expression<Boolean> expr = new NumericBooleanExpression(
				x,
				NumericComparator.EQ, new FunctionExpression<>(
						MathFunctions.POW, new Constant<Double>(BuiltinTypes.DOUBLE, 9.5), new Constant<Double>(BuiltinTypes.DOUBLE, 3.0)));

		TstUtil.runTest(solver, expr, Result.SAT, TstUtil.PRINT_CORAL_EXPR);
	}
	
	@Test
	public void simplePow2() {
		CoralSolver solver = TstUtil.createCoralSolver(new Properties());
		Variable<Double> x = new Variable<Double>(BuiltinTypes.DOUBLE, "x");

		Expression<Boolean> expr = new NumericBooleanExpression(
				new Constant<Double>(BuiltinTypes.DOUBLE, 20.5),
				NumericComparator.EQ, new FunctionExpression<>(
						MathFunctions.POW, x, new Constant<Double>(BuiltinTypes.DOUBLE, 3.0)));

		//TODO: should pass?
		TstUtil.runTest(solver, expr, Result.DONT_KNOW, TstUtil.PRINT_CORAL_EXPR);
	}
	
	@Test
	public void simplePow3() {
		CoralSolver solver = TstUtil.createCoralSolver(new Properties());
		Variable<Double> x = new Variable<Double>(BuiltinTypes.DOUBLE, "x");

		Expression<Boolean> expr = new NumericBooleanExpression(
				new Constant<Double>(BuiltinTypes.DOUBLE, 20.5),
				NumericComparator.EQ, new FunctionExpression<>(
						MathFunctions.POW, new Constant<Double>(BuiltinTypes.DOUBLE, 9.5), x));

		TstUtil.runTest(solver, expr, Result.DONT_KNOW, TstUtil.PRINT_CORAL_EXPR);
	}
	
	@Test
	public void simpleLog1() {
		CoralSolver solver = TstUtil.createCoralSolver(new Properties());
		Variable<Double> x = new Variable<Double>(BuiltinTypes.DOUBLE, "x");

		Expression<Boolean> expr = new NumericBooleanExpression(
				x,
				NumericComparator.EQ, new FunctionExpression<>(
						MathFunctions.LOG, new Constant<Double>(BuiltinTypes.DOUBLE, 9.5)));

		TstUtil.runTest(solver, expr, Result.SAT, TstUtil.PRINT_CORAL_EXPR);
	}
	
	@Test
	public void simpleLog2() {
		CoralSolver solver = TstUtil.createCoralSolver(new Properties());
		Variable<Double> x = new Variable<Double>(BuiltinTypes.DOUBLE, "x");

		Expression<Boolean> expr = new NumericBooleanExpression(
				new Constant<Double>(BuiltinTypes.DOUBLE, 9.5),
				NumericComparator.EQ, new FunctionExpression<>(
						MathFunctions.LOG, x));

		//TODO: should pass?
		TstUtil.runTest(solver, expr, Result.SAT, TstUtil.PRINT_CORAL_EXPR);
	}
	/*
	@Test
	public void simpleSqrt() {
		CoralSolver solver = TstUtil.createCoralSolver(new Properties());
		Variable<Double> x = new Variable<Double>(BuiltinTypes.DOUBLE, "x");

		Expression<Boolean> expr = new NumericBooleanExpression(
				new Constant<Double>(BuiltinTypes.DOUBLE, 9.5),
				NumericComparator.EQ, new FunctionExpression<>(
						MathFunctions.SQRT, x));
		TstUtil.runTest(solver, expr, Result.SAT, TstUtil.PRINT_CORAL_EXPR);
	}*/
	
	@Test
	public void simpleSqrt2() {
		CoralSolver solver = TstUtil.createCoralSolver(new Properties());
		Variable<Double> x = new Variable<Double>(BuiltinTypes.DOUBLE, "x");

		Expression<Boolean> expr = new NumericBooleanExpression(
				x,
				NumericComparator.EQ, new FunctionExpression<>(
						MathFunctions.SQRT, new Constant<Double>(BuiltinTypes.DOUBLE, 9.5)));

		TstUtil.runTest(solver, expr, Result.SAT, TstUtil.PRINT_CORAL_EXPR);
	}
}
