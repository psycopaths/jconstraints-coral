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
import gov.nasa.jpf.constraints.expressions.CastExpression;
import gov.nasa.jpf.constraints.expressions.Constant;
import gov.nasa.jpf.constraints.expressions.NumericBooleanExpression;
import gov.nasa.jpf.constraints.expressions.NumericComparator;
import gov.nasa.jpf.constraints.types.BuiltinTypes;

import java.util.Properties;

import org.junit.Test;


public class BoolExpTest {
	
	
	@Test
	public void typeCastImplicitIntToDouble() {
		CoralSolver solver = TstUtil.createCoralSolver(new Properties());		
		Expression<Boolean> expr = new NumericBooleanExpression(
				new Constant<Double>(BuiltinTypes.DOUBLE, 2.42),
				NumericComparator.GT,
				new Constant<Integer>(BuiltinTypes.SINT32, 2));
		TstUtil.runTest(solver, expr, Result.SAT, TstUtil.PRINT_CORAL_EXPR);
	}
	
	@Test
	public void typeCastExplicitIntToDouble() {
		CoralSolver solver = TstUtil.createCoralSolver(new Properties());		
		Expression<Boolean> expr 	= new NumericBooleanExpression(
				new Constant<Double>(BuiltinTypes.DOUBLE, 2.42),
				NumericComparator.GT,
				CastExpression.create(new Constant<Integer>(BuiltinTypes.SINT32, 2), BuiltinTypes.DOUBLE));
		TstUtil.runTest(solver, expr, Result.SAT, TstUtil.PRINT_CORAL_EXPR);
	}
	
	@Test
	public void typeCastExplicitDoubleToInt() {
		CoralSolver solver = TstUtil.createCoralSolver(new Properties());		
		Expression<Boolean> expr = new NumericBooleanExpression(
				new Constant<Integer>(BuiltinTypes.SINT32, 1),
				NumericComparator.LT,
				CastExpression.create(new Constant<Double>(BuiltinTypes.DOUBLE, 2.35), BuiltinTypes.SINT32));
		TstUtil.runTest(solver, expr, Result.SAT, TstUtil.PRINT_CORAL_EXPR);
	}


    	
    	/*@Test
    	public void tsafe() 
    	/*
    	 * TSAFE example?
    	 *     	/*if(C_v > 0.0 && A_v > 0.0 && bank_ang < 90.0 &&
  			  bank_ang > 0.0 &&
  			  Cdir < 3 &&
  			  Cdir >= 0)

  			  if(0.0 == (( Math.pow (((C_v * ( Math.sin ((((C_Psi0_deg * 0.017453292519943295) -
  					  (A_Psi0_deg * 0.017453292519943295)) + (((((((( Math.pow (A_v,2.0)) /
  							  (( Math.sin ((bank_ang * 0.017453292519943295))) / ( Math.cos ((bank_ang * 0.017453292519943295)))))
  							  / 68443.0) * 0.0) / A_v) * -1.0) * C_v) / ((( Math.pow (C_v,2.0)) /
  									  (( Math.sin ((bank_ang * 0.017453292519943295))) / ( Math.cos ((bank_ang * 0.017453292519943295))))) /
  									  68443.0)))))) - (A_v * 0.0)),2.0)) + ( Math.pow (((C_v * ( Math.cos ((((C_Psi0_deg * 0.017453292519943295)
  											  - (A_Psi0_deg * 0.017453292519943295)) + (((((((( Math.pow (A_v,2.0)) /
  													  (( Math.sin ((bank_ang * 0.017453292519943295))) /
  															  ( Math.cos ((bank_ang * 0.017453292519943295))))) / 68443.0) * 0.0) / A_v)
  					  *
  					  -1.0) * C_v) / ((( Math.pow (C_v,2.0)) / (( Math.sin ((bank_ang * 0.017453292519943295))) /
  							  ( Math.cos ((bank_ang * 0.017453292519943295))))) / 68443.0)))))) - (A_v * 1.0)),2.0))) &&
  					  Cturn != 0 &&
  					  Aturn != 0 &&
  					 // Cdir == 0 &&
  					  C_v > 0.0 &&
  					  A_v > 0.0 &&
  					  bank_ang < 90.0 &&
  					  bank_ang > 0.0 &&
  					  Cdir < 3 &&
  					  Cdir >= 0)
    	 * 
    	 *
    	}*/

}
