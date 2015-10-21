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

import java.util.Properties;
import java.util.logging.Logger;

import coral.PC;
import junit.framework.Assert;
import gov.nasa.jpf.constraints.api.ConstraintSolver;
import gov.nasa.jpf.constraints.api.ConstraintSolver.Result;
import gov.nasa.jpf.constraints.api.Expression;
import gov.nasa.jpf.constraints.api.Valuation;
import gov.nasa.jpf.constraints.api.ValuationEntry;
import gov.nasa.jpf.constraints.solvers.ConstraintSolverFactory;

public class TstUtil {

	public static final boolean PRINT_CORAL_EXPR = true;
	
	final static Logger logger = Logger.getLogger(TstUtil.class.getName());
	
	public static CoralSolver createCoralSolver(Properties conf) {
		conf.setProperty("symbolic.dp", "coral");
		conf.setProperty("coral.solver", "PSO_OPT4J");
		ConstraintSolverFactory factory = new ConstraintSolverFactory(conf);
		ConstraintSolver solver = factory.createSolver();
		return (CoralSolver) solver;
	}
	
	public static Valuation runTest(ConstraintSolver solver, Expression<Boolean> expr, Result expectedRes, boolean printCoralExpr) {
	  logger.info("Expr: " + expr.toString());
		try {
			if(printCoralExpr) {
				CoralExpressionGenerator expGen = new CoralExpressionGenerator();
				PC pc = expGen.generateAssertion(expr);
				logger.info("CORAL Expr: " + pc.toString());
			}
			Valuation val = new Valuation();
			long start = System.currentTimeMillis();
	        Result res = solver.solve(expr, val);
	        long solverTime = (System.currentTimeMillis() - start);
	        logger.info("Solver time: " + solverTime + "ms");
	        logger.info("Expected " + expectedRes + " got " + res);
	        if(res == Result.SAT) {
	          logger.info("-------Valuation-------");
		        for(ValuationEntry<?> exp : val)
		          logger.info(exp.getVariable() + "=" + exp.getValue());
		        logger.info("-----------------------");
	        }
	        Assert.assertEquals(expectedRes, res);
	        return val;
		} catch(Exception e) {
			throw e;
		} finally {
		  logger.info("======================================================================");
		}
	}

}
