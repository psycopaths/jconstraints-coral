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
import gov.nasa.jpf.constraints.expressions.LogicalOperator;
import gov.nasa.jpf.constraints.expressions.NumericBooleanExpression;
import gov.nasa.jpf.constraints.expressions.NumericComparator;
import gov.nasa.jpf.constraints.expressions.PropositionalCompound;
import gov.nasa.jpf.constraints.types.BuiltinTypes;

import java.util.Properties;

import org.junit.Test;


public class RelationTest {

	private void testNumericRelation(Expression<?> lhs, NumericComparator comp, Expression<?> rhs) {
		CoralSolver solver = TstUtil.createCoralSolver(new Properties());		
		Expression<Boolean> expr = new NumericBooleanExpression(
				lhs,
				comp, rhs);
		TstUtil.runTest(solver, expr, Result.SAT, TstUtil.PRINT_CORAL_EXPR);
	}
	
	private void testNumericRelation(Expression<?> lhs, Expression<?> rhs) {
		testNumericRelation(lhs, NumericComparator.LT, rhs);
		testNumericRelation(lhs, NumericComparator.LE, rhs);
		testNumericRelation(lhs, NumericComparator.EQ, rhs);
		testNumericRelation(lhs, NumericComparator.GE, rhs);
		testNumericRelation(lhs, NumericComparator.GT, rhs);
	}
	
	
	@Test
	public void doubleRelations() {
		testNumericRelation(new Variable<Double>(BuiltinTypes.DOUBLE, "x"), new Constant<Double>(BuiltinTypes.DOUBLE, 20.3576));
	}
	
	//TODO: Float relations are not supported in CORAL 0.7!
	/*@Test
	public void floatRelations() {
		testNumericRelation(new Variable<Float>(BuiltinTypes.FLOAT, "x"), new Constant<Float>(BuiltinTypes.FLOAT, 20.1f));
	}*/
	
	@Test
	public void integerRelations() {
		testNumericRelation(new Variable<Integer>(BuiltinTypes.SINT32, "x"), new Constant<Integer>(BuiltinTypes.SINT32, 20));
	}
	
	//TODO: Long relations are not supported in CORAL 0.7!
	/*@Test
	public void longRelations() {
		testNumericRelation(new Variable<Long>(BuiltinTypes.SINT64, "x"), new Constant<Long>(BuiltinTypes.SINT64, 200000L));
	}*/
	
	@Test
	public void boolAND() {
		CoralSolver solver = TstUtil.createCoralSolver(new Properties());
		
		//true && true
		Expression<Boolean> expr = new PropositionalCompound(new Constant<Boolean>(BuiltinTypes.BOOL, true), LogicalOperator.AND, new Constant<Boolean>(BuiltinTypes.BOOL, true));
		TstUtil.runTest(solver, expr, Result.SAT, TstUtil.PRINT_CORAL_EXPR);
		
		//true && false
		expr = new PropositionalCompound(new Constant<Boolean>(BuiltinTypes.BOOL, true), LogicalOperator.AND, new Constant<Boolean>(BuiltinTypes.BOOL, false));
		TstUtil.runTest(solver, expr, Result.UNSAT, TstUtil.PRINT_CORAL_EXPR);
		
		//false && false
		expr = new PropositionalCompound(new Constant<Boolean>(BuiltinTypes.BOOL, false), LogicalOperator.AND, new Constant<Boolean>(BuiltinTypes.BOOL, false));
		TstUtil.runTest(solver, expr, Result.UNSAT, TstUtil.PRINT_CORAL_EXPR);
		
	}
	@Test
	public void boolOR() {
		CoralSolver solver = TstUtil.createCoralSolver(new Properties());
		//false || false
		Expression<Boolean> expr = new PropositionalCompound(new Constant<Boolean>(BuiltinTypes.BOOL, false), LogicalOperator.OR, new Constant<Boolean>(BuiltinTypes.BOOL, false));
		TstUtil.runTest(solver, expr, Result.UNSAT, TstUtil.PRINT_CORAL_EXPR);
		
		//true || false
		expr = new PropositionalCompound(new Constant<Boolean>(BuiltinTypes.BOOL, true), LogicalOperator.OR, new Constant<Boolean>(BuiltinTypes.BOOL, false));
		TstUtil.runTest(solver, expr, Result.SAT, TstUtil.PRINT_CORAL_EXPR);
		
		//false || true
		expr = new PropositionalCompound(new Constant<Boolean>(BuiltinTypes.BOOL, false), LogicalOperator.OR, new Constant<Boolean>(BuiltinTypes.BOOL, true));
		TstUtil.runTest(solver, expr, Result.SAT, TstUtil.PRINT_CORAL_EXPR);
		
		//true || true
		expr = new PropositionalCompound(new Constant<Boolean>(BuiltinTypes.BOOL, true), LogicalOperator.OR, new Constant<Boolean>(BuiltinTypes.BOOL, true));
		TstUtil.runTest(solver, expr, Result.SAT, TstUtil.PRINT_CORAL_EXPR);
	}
	
	@Test
	public void boolXOR() {
		CoralSolver solver = TstUtil.createCoralSolver(new Properties());
		//true xor true
		Expression<Boolean> expr = new PropositionalCompound(new Constant<Boolean>(BuiltinTypes.BOOL, true), LogicalOperator.XOR, new Constant<Boolean>(BuiltinTypes.BOOL, true));
		TstUtil.runTest(solver, expr, Result.UNSAT, TstUtil.PRINT_CORAL_EXPR);
		
		//true xor false
		expr = new PropositionalCompound(new Constant<Boolean>(BuiltinTypes.BOOL, true), LogicalOperator.XOR, new Constant<Boolean>(BuiltinTypes.BOOL, false));
		TstUtil.runTest(solver, expr, Result.SAT, TstUtil.PRINT_CORAL_EXPR);
		
		//false xor false
		expr = new PropositionalCompound(new Constant<Boolean>(BuiltinTypes.BOOL, false), LogicalOperator.XOR, new Constant<Boolean>(BuiltinTypes.BOOL, false));
		TstUtil.runTest(solver, expr, Result.UNSAT, TstUtil.PRINT_CORAL_EXPR);
		
		//false xor true
		expr = new PropositionalCompound(new Constant<Boolean>(BuiltinTypes.BOOL, false), LogicalOperator.XOR, new Constant<Boolean>(BuiltinTypes.BOOL, true));
		TstUtil.runTest(solver, expr, Result.SAT, TstUtil.PRINT_CORAL_EXPR);
		
	}
	
	/*
	 * TODO: This fails with null pointer exceptions due to a bug in CORAL!
	 *
	@Test
	public void boolNOT() {

		CoralSolver solver = TstUtil.createCoralSolver(new Properties());
		//true && not(false)
		Expression<Boolean> expr = new PropositionalCompound(new Constant<Boolean>(BuiltinTypes.BOOL, false), LogicalOperator.AND, new Negation(new Constant<Boolean>(BuiltinTypes.BOOL, false)));
		TstUtil.runTest(solver, expr, Result.SAT, TstUtil.PRINT_CORAL_EXPR);
		
		//true && not(true)
		expr = new PropositionalCompound(new Constant<Boolean>(BuiltinTypes.BOOL, false), LogicalOperator.AND, new Negation(new NumericBooleanExpression(
				new Constant<Double>(BuiltinTypes.DOUBLE, 2.3),
				NumericComparator.GE, new Constant<Double>(BuiltinTypes.DOUBLE, 1.0))));
		TstUtil.runTest(solver, expr, Result.UNSAT, TstUtil.PRINT_CORAL_EXPR);
	}
	*/
}
