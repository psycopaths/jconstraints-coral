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

import gov.nasa.jpf.constraints.api.ConstraintSolver.Result;
import gov.nasa.jpf.constraints.api.Expression;
import gov.nasa.jpf.constraints.api.Variable;
import gov.nasa.jpf.constraints.expressions.Constant;
import gov.nasa.jpf.constraints.expressions.NumericBooleanExpression;
import gov.nasa.jpf.constraints.expressions.NumericComparator;
import gov.nasa.jpf.constraints.expressions.NumericCompound;
import gov.nasa.jpf.constraints.expressions.NumericOperator;
import gov.nasa.jpf.constraints.expressions.functions.FunctionExpression;
import gov.nasa.jpf.constraints.expressions.functions.math.MathFunctions;
import gov.nasa.jpf.constraints.expressions.functions.math.UnaryDoubleFunction;
import gov.nasa.jpf.constraints.types.BuiltinTypes;

import java.util.Properties;

import org.junit.Test;


public class TrigonometricTest {

	private void testSimpleTrig(UnaryDoubleFunction trig) {
		CoralSolver solver = TstUtil.createCoralSolver(new Properties());
		Variable<Double> x = new Variable<Double>(BuiltinTypes.DOUBLE, "x");
		Variable<Double> y = new Variable<Double>(BuiltinTypes.DOUBLE, "y");

		Expression<Boolean> expr = new NumericBooleanExpression(y,
				NumericComparator.EQ, new FunctionExpression<>(trig, x));

		TstUtil.runTest(solver, expr, Result.SAT, TstUtil.PRINT_CORAL_EXPR);
	}

	@Test
	public void simpleSin() {
		testSimpleTrig(MathFunctions.SIN);
	}

	@Test
	public void simpleCos() {
		testSimpleTrig(MathFunctions.COS);
	}

	@Test
	public void simpleTan() {
		testSimpleTrig(MathFunctions.TAN);
	}

	@Test
	public void simpleAtan() {
		testSimpleTrig(MathFunctions.ATAN);
	}

	@Test
	public void simpleAcos() {
		testSimpleTrig(MathFunctions.ACOS);
	}

	@Test
	public void simpleAsin() {
		testSimpleTrig(MathFunctions.ASIN);
	}
	
	@Test
	public void simpleAtan2() {
		CoralSolver solver = TstUtil.createCoralSolver(new Properties());
		Variable<Double> x = new Variable<Double>(BuiltinTypes.DOUBLE, "x");
		
		Expression<Boolean> expr = new NumericBooleanExpression(x,
				NumericComparator.EQ, new FunctionExpression<>(MathFunctions.ATAN2, new Constant<Double>(BuiltinTypes.DOUBLE, -1.0), new Constant<Double>(BuiltinTypes.DOUBLE, -1.0)));

		TstUtil.runTest(solver, expr, Result.SAT, TstUtil.PRINT_CORAL_EXPR);
	}
	

	@Test
	public void identityTest() {
		CoralSolver solver = TstUtil.createCoralSolver(new Properties());
		Variable<Double> x = new Variable<Double>(BuiltinTypes.DOUBLE, "x");

		Expression<Boolean> expr = new NumericBooleanExpression(
				new FunctionExpression<>(MathFunctions.TAN, x),
				NumericComparator.EQ, new NumericCompound<Double>(
						new FunctionExpression<>(MathFunctions.SIN, x),
						NumericOperator.DIV, new FunctionExpression<>(
								MathFunctions.COS, x)));

		TstUtil.runTest(solver, expr, Result.SAT, TstUtil.PRINT_CORAL_EXPR);
	}

	@Test
	public void identityFailTest() {
		CoralSolver solver = TstUtil.createCoralSolver(new Properties());
		Variable<Double> x = new Variable<Double>(BuiltinTypes.DOUBLE, "x");

		Expression<Boolean> expr = new NumericBooleanExpression(
				new NumericCompound<Double>(new FunctionExpression<>(
						MathFunctions.TAN, x), NumericOperator.PLUS,
						new Constant<>(BuiltinTypes.DOUBLE, 3.0)),
				NumericComparator.EQ, new NumericCompound<Double>(
						new FunctionExpression<>(MathFunctions.SIN, x),
						NumericOperator.DIV, new FunctionExpression<>(
								MathFunctions.COS, x)));

		TstUtil.runTest(solver, expr, Result.DONT_KNOW, TstUtil.PRINT_CORAL_EXPR);
	}

	@Test
	public void exprSin() {
		CoralSolver solver = TstUtil.createCoralSolver(new Properties());
		Variable<Double> x = new Variable<Double>(BuiltinTypes.DOUBLE, "x");
		Variable<Double> y = new Variable<Double>(BuiltinTypes.DOUBLE, "y");

		Expression<Boolean> expr = new NumericBooleanExpression(
				new NumericCompound<Double>(y, NumericOperator.PLUS,
						new Constant<Double>(BuiltinTypes.DOUBLE, 7.2)),
				NumericComparator.EQ, new FunctionExpression<>(
						MathFunctions.SIN, x));

		TstUtil.runTest(solver, expr, Result.SAT, TstUtil.PRINT_CORAL_EXPR);
	}

	@Test
	public void constSin() {
		CoralSolver solver = TstUtil.createCoralSolver(new Properties());
		Variable<Double> x = new Variable<Double>(BuiltinTypes.DOUBLE, "x");

		Expression<Boolean> expr = new NumericBooleanExpression(
				new Constant<Double>(BuiltinTypes.DOUBLE, 7.2),
				NumericComparator.EQ, new FunctionExpression<>(
						MathFunctions.SIN, x));

		TstUtil.runTest(solver, expr, Result.DONT_KNOW, TstUtil.PRINT_CORAL_EXPR);
	}

	@Test
	public void exampleFromHP() {
		CoralSolver solver = TstUtil.createCoralSolver(new Properties());
		Variable<Double> x = new Variable<Double>(BuiltinTypes.DOUBLE, "x");
		Variable<Double> y = new Variable<Double>(BuiltinTypes.DOUBLE, "y");

		Expression<Boolean> expr = new NumericBooleanExpression(
				new NumericCompound<Double>(new FunctionExpression<>(
						MathFunctions.SIN, x), NumericOperator.PLUS,
						new FunctionExpression<>(MathFunctions.COS, y)),
				NumericComparator.EQ, new Constant<Double>(BuiltinTypes.DOUBLE,
						2.0));

		TstUtil.runTest(solver, expr, Result.SAT, TstUtil.PRINT_CORAL_EXPR);
	}

	@Test
	public void testJconstraintsZ3Expr() {
		CoralSolver solver = TstUtil.createCoralSolver(new Properties());
		Variable<Double> head = new Variable<Double>(BuiltinTypes.DOUBLE,
				"head");
		NumericCompound<Double> cosArg = new NumericCompound<Double>(
				new Constant<>(BuiltinTypes.DOUBLE, 3.141592653589793),
				NumericOperator.PLUS, new NumericCompound<Double>(
						new Constant<>(BuiltinTypes.DOUBLE,
								0.017453292519943295), NumericOperator.MUL,
						head));

		NumericCompound<Double> asinArg = new NumericCompound<Double>(
				new Constant<>(BuiltinTypes.DOUBLE, 0.6130455374565814),
				NumericOperator.PLUS,
				new NumericCompound<Double>(new Constant<>(BuiltinTypes.DOUBLE,
						0.01204238407208075), NumericOperator.MUL,
						new FunctionExpression<>(MathFunctions.COS, cosArg)));

		Expression<Boolean> expr = new NumericBooleanExpression(new Constant<>(
				BuiltinTypes.DOUBLE, 90.0), NumericComparator.GE,
				new NumericCompound<Double>(new Constant<>(BuiltinTypes.DOUBLE,
						57.29577951308232), NumericOperator.MUL,
						new FunctionExpression<>(MathFunctions.ASIN, asinArg)));
		TstUtil.runTest(solver, expr, Result.SAT, TstUtil.PRINT_CORAL_EXPR);
	}

	@Test
	public void testJconstraintsZ3Coral1() {
		CoralSolver solver = TstUtil.createCoralSolver(new Properties());
		Variable<Double> x1 = new Variable<Double>(BuiltinTypes.DOUBLE, "x1");
		Variable<Double> x2 = new Variable<Double>(BuiltinTypes.DOUBLE, "x2");
		Expression<Boolean> expr = new NumericBooleanExpression(
				new NumericCompound<Double>(new FunctionExpression<Double>(
						MathFunctions.SIN, x1), NumericOperator.MINUS,
						new FunctionExpression<>(MathFunctions.COS, x2)),
				NumericComparator.EQ, new Constant<>(BuiltinTypes.DOUBLE, 0.0));
		TstUtil.runTest(solver, expr, Result.SAT, TstUtil.PRINT_CORAL_EXPR);
	}

	@Test
	public void testJconstraintsZ3Coral2() {
		CoralSolver solver = TstUtil.createCoralSolver(new Properties());

		Constant<Double> c1 = new Constant<Double>(BuiltinTypes.DOUBLE,
				0.017453292519943295);
		Constant<Double> zero = new Constant<Double>(BuiltinTypes.DOUBLE, 0.0);
		Variable<Double> x1 = new Variable<Double>(BuiltinTypes.DOUBLE, "x1");
		Variable<Double> x2 = new Variable<Double>(BuiltinTypes.DOUBLE, "x2");
		Variable<Double> x3 = new Variable<Double>(BuiltinTypes.DOUBLE, "x3");
		Variable<Double> x4 = new Variable<Double>(BuiltinTypes.DOUBLE, "x4");

		// a1: pow(((x1 * sin(((c1 * x2) - (c1 * x3)))) - (0.0 * x4)), 2.0)
		FunctionExpression<Double> pow1 = new FunctionExpression<Double>(
				MathFunctions.POW,
				new NumericCompound<Double>(new NumericCompound<Double>(x1,
						NumericOperator.MUL, new FunctionExpression<>(
								MathFunctions.SIN, new NumericCompound<Double>(
										new NumericCompound<Double>(c1,
												NumericOperator.MUL, x2),
										NumericOperator.MINUS,
										new NumericCompound<Double>(c1,
												NumericOperator.MUL, x3)))),
						NumericOperator.MINUS, new NumericCompound<Double>(
								zero, NumericOperator.MUL, x4)),
				new Constant<>(BuiltinTypes.DOUBLE, 2.0));

		// + pow((x1 * cos((((c1 * x2) - (c1 * x3)) + 0.0))), 2.0)
		FunctionExpression<Double> pow2 = new FunctionExpression<Double>(
				MathFunctions.POW,
				new NumericCompound<Double>(
						x1,
						NumericOperator.MUL,
						new FunctionExpression<>(
								MathFunctions.COS,
								new NumericCompound<Double>(
										new NumericCompound<Double>(
												new NumericCompound<Double>(c1,
														NumericOperator.MUL, x2),
												NumericOperator.MINUS,
												new NumericCompound<Double>(c1,
														NumericOperator.MUL, x3)),
										NumericOperator.PLUS, zero))),
				new Constant<>(BuiltinTypes.DOUBLE, 2.0));

		// Constraint: 0.0 == a1
		NumericCompound<Double> a1 = new NumericCompound<Double>(pow1,
				NumericOperator.PLUS, pow2);
		Expression<Boolean> expr = new NumericBooleanExpression(zero,
				NumericComparator.EQ, a1);

		TstUtil.runTest(solver, expr, Result.SAT, TstUtil.PRINT_CORAL_EXPR);
	}
}
