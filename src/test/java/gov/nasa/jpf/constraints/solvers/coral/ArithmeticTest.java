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
import gov.nasa.jpf.constraints.api.ConstraintSolver.Result;
import gov.nasa.jpf.constraints.api.Variable;
import gov.nasa.jpf.constraints.expressions.BitvectorExpression;
import gov.nasa.jpf.constraints.expressions.BitvectorOperator;
import gov.nasa.jpf.constraints.expressions.Constant;
import gov.nasa.jpf.constraints.expressions.NumericBooleanExpression;
import gov.nasa.jpf.constraints.expressions.NumericComparator;
import gov.nasa.jpf.constraints.expressions.NumericCompound;
import gov.nasa.jpf.constraints.expressions.NumericOperator;
import gov.nasa.jpf.constraints.types.BuiltinTypes;

import java.util.Properties;

import org.junit.Test;


public class ArithmeticTest {

	private <E> void testArithmeticExpr(Variable<E> var, Expression<E> rhs) {
		CoralSolver solver = TstUtil.createCoralSolver(new Properties());		
		Expression<Boolean> expr = new NumericBooleanExpression(
				var,
				NumericComparator.EQ,
				rhs);
		TstUtil.runTest(solver, expr, Result.SAT, TstUtil.PRINT_CORAL_EXPR);
	}
	
	private <E> void testNumericArithmeticExpr(Variable<E> var, Expression<E> leftOp, NumericOperator op, Expression<E> rightOp) {
		testArithmeticExpr(var, new NumericCompound(leftOp, op, rightOp));
	}
	
	private <E> void testOperators(Variable<E> x, Expression<E> leftOp, Expression<E> rightOp) {
		testNumericArithmeticExpr(x, leftOp, NumericOperator.PLUS, rightOp);
		testNumericArithmeticExpr(x, leftOp, NumericOperator.MINUS, rightOp);
		testNumericArithmeticExpr(x, leftOp, NumericOperator.DIV, rightOp);
		testNumericArithmeticExpr(x, leftOp, NumericOperator.MUL, rightOp);
		testNumericArithmeticExpr(x, leftOp, NumericOperator.REM, rightOp);
	}
	/*
	@Test
	public void intArithmetic() {
		testOperators(new Variable<Integer>(BuiltinTypes.SINT32, "x"), new Constant<Integer>(BuiltinTypes.SINT32, 3), new Constant<Integer>(BuiltinTypes.SINT32, 3));
	}
	
	@Test
	public void longArithmetic() {
		testOperators(new Variable<Long>(BuiltinTypes.SINT64, "x"), new Constant<Long>(BuiltinTypes.SINT64, 30000L), new Constant<Long>(BuiltinTypes.SINT64, 3000000L));
	}
	
	@Test
	public void floatArithmetic() {
		testOperators(new Variable<Float>(BuiltinTypes.FLOAT, "x"), new Constant<Float>(BuiltinTypes.FLOAT, 30.0F), new Constant<Float>(BuiltinTypes.FLOAT, 2.5F));
	}
	
	
	@Test
	public void doubleArithmetic() {
		testOperators(new Variable<Double>(BuiltinTypes.DOUBLE, "x"), new Constant<Double>(BuiltinTypes.DOUBLE, 30.0), new Constant<Double>(BuiltinTypes.DOUBLE, 2.5));
	}

	//TODO: Bit operators are not supported
	
	
	private void testIntBitOperator(BitvectorOperator op) {
		testArithmeticExpr(new Variable<Integer>(BuiltinTypes.SINT32, "x"), new BitvectorExpression(new Constant<Integer>(BuiltinTypes.SINT32, 3), op, new Constant<Integer>(BuiltinTypes.SINT32, 3)));
	}
	
	@Test
	public void bitAND() {
		testIntBitOperator(BitvectorOperator.AND);
	}

	@Test
	public void bitOR() {
		testIntBitOperator(BitvectorOperator.OR);
	}
	
	@Test
	public void bitXOR() {
		testIntBitOperator(BitvectorOperator.XOR);
	}
	
	@Test
	public void bitSHIFTL() {
		testIntBitOperator(BitvectorOperator.SHIFTL);
	}
	
	@Test
	public void bitSHIFTR() {
		testIntBitOperator(BitvectorOperator.SHIFTR);
	}
	
	@Test
	public void bitSHIFTUR() {
		testIntBitOperator(BitvectorOperator.SHIFTUR);
	}
*/
}
