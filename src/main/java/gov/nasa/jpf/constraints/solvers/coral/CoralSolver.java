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

import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import symlib.SymDouble;
import symlib.SymFloat;
import symlib.SymInt;
import symlib.SymLiteral;
import symlib.SymLong;
import symlib.SymNumber;
import symlib.Util;
import coral.PC;
import coral.solvers.Env;
import coral.solvers.SolverKind;
import coral.util.Config;
import gov.nasa.jpf.constraints.api.ConstraintSolver;
import gov.nasa.jpf.constraints.api.Expression;
import gov.nasa.jpf.constraints.api.SolverContext;
import gov.nasa.jpf.constraints.api.Variable;
import gov.nasa.jpf.constraints.api.Valuation;
import gov.nasa.jpf.constraints.api.ConstraintSolver.Result;
import gov.nasa.jpf.constraints.solvers.coral.IntervalSolver.Solver;
import gov.nasa.jpf.constraints.solvers.coral.exceptions.CoralConfigurationException;
import gov.nasa.jpf.constraints.solvers.coral.exceptions.CoralSolverException;
import gov.nasa.jpf.constraints.types.BuiltinTypes.FloatType;
import gov.nasa.jpf.constraints.types.BuiltinTypes.SInt32Type;
import gov.nasa.jpf.constraints.types.BuiltinTypes.SInt64Type;
import gov.nasa.jpf.constraints.types.IntegerType;
import gov.nasa.jpf.constraints.types.RealType;
import gov.nasa.jpf.constraints.types.BuiltinTypes.DoubleType;

public class CoralSolver extends ConstraintSolver {
	
	public static class CoralSolverBuilder {
		private long seed = 464655;
		private int iterations = -1;
		private SolverKind solverKind = SolverKind.PSO_OPT4J;
		private boolean optimize = true;
		private IntervalSolver intervalSolver = new IntervalSolver(Solver.NONE);
		
		public CoralSolverBuilder() { }
		
		public CoralSolverBuilder seed(long seed) {
			this.seed = seed;
			return this;
		}
		
		public CoralSolverBuilder iterations(int iterations) {
			this.iterations = iterations;
			return this;
		}
		
		public CoralSolverBuilder solverKind(SolverKind solver) {
			this.solverKind = solver;
			return this;
		}
		
		public CoralSolverBuilder optimize(boolean optimize) {
			this.optimize = optimize;
			return this;
		}
		
		public CoralSolverBuilder intervalSolver(IntervalSolver intervalSolver) {
			this.intervalSolver = intervalSolver;
			return this;
		}
		
		public CoralSolver buildCoralSolver() {
			return new CoralSolver(this.seed,
								   this.iterations,
								   this.solverKind,
								   this.optimize,
								   this.intervalSolver);
		}
	}
	
	private final coral.solvers.Solver coralSolver;
	private static final Logger logger = Logger.getLogger(CoralSolver.class.getName());
	
	private CoralSolver(long seed,
						int iterations,
						SolverKind solver,
						boolean optimize,
						IntervalSolver intervalSolver) {
		
		//Initialize Coral
		Util.resetID(); //resets var counter
		Config.seed = seed;
		if(!intervalSolver.getIntervalSolver().equals(IntervalSolver.Solver.NONE)) {
			Config.intervalSolver = intervalSolver.getIntervalSolver().toString();
			Config.enableIntervalBasedSolver = true;
			if(intervalSolver.getIntervalSolver().equals(IntervalSolver.Solver.REALPAVER)) {
				Config.realPaverLocation = intervalSolver.getPath();
			} else if (intervalSolver.getIntervalSolver().equals(IntervalSolver.Solver.ICOS)) {
				Config.icosLocation = intervalSolver.getPath();
			} else {
				throw new CoralConfigurationException("Unsupported interval solver!");
			}
			Config.simplifyUsingIntervalSolver = optimize ? true : false;
		}
		if(iterations > 0) {
			if(solver.equals(SolverKind.PSO_OPT4J)) {
				Config.nIterationsPSO = iterations;
			} else if(solver.equals(SolverKind.RANDOM)) {
				Config.nIterationsRANDOM = iterations;
			} else if(solver.equals(SolverKind.AVM)) {
				Config.nIterationsAVM = iterations;
			} 
		}
		
		coralSolver = solver.get();
	}
	
	public void resetVarCounter() {
		Util.resetID(); //resets var counter
	}
	
	@Override
	public SolverContext createContext() {
		return new CoralSolverContext(this);
	}

	public Result solve(Expression<Boolean> f) {
		return solve(f, null);
	}
	
	@Override
	public Result solve(Expression<Boolean> f, Valuation result) {
		CoralExpressionGenerator root = new CoralExpressionGenerator();
		final Env[] sol = new Env[1];
    try {
  		final PC pc = root.generateAssertion(f);
  		logger.fine("Coral solving...");
			sol[0] = coralSolver.getCallable(pc).call();
		} catch (Exception e) {
		  logger.severe("Coral threw exception. Returning DONT_KNOW");
			return Result.DONT_KNOW;
		}
		//TODO: not sure why the solution is found as the first element in the Env[]...
		Env coralSol = sol[0];		
		Result coralRes = convertCoralRes(coralSol.getResult());
		
		if(result != null && coralRes == Result.SAT) { //result is requested besides satisfiability check
			HashMap<Variable<?>, SymLiteral> varMap = root.getVariables();
			for(Variable<?> v : varMap.keySet()) {
				SymNumber value = coralSol.getValue(varMap.get(v));
				
				//These checks make my eyes bleed
				if((v.getType() instanceof FloatType) && 
				    !(value instanceof SymFloat)) { //TODO: ensure this is sound. It seems like a patch
				  Float floatVal = value.evalNumber().floatValue();
				  result.setParsedValue(v, floatVal.toString());
				} else if((v.getType() instanceof DoubleType) && 
            !(value instanceof SymDouble)) {
          Double dVal = value.evalNumber().doubleValue();
          result.setParsedValue(v, dVal.toString());
				} else if((v.getType() instanceof SInt32Type) && 
	           !(value instanceof SymInt)) {
          Integer iVal = value.evalNumber().intValue();
          result.setParsedValue(v, iVal.toString());
	      } else if((v.getType() instanceof SInt64Type) && 
            !(value instanceof SymLong)) {
         Long lVal = value.evalNumber().longValue();
         result.setParsedValue(v, lVal.toString());
       } else //This is the default case which should be the most frequent one
	        result.setParsedValue(v, value.toString());
			}
			logger.finer("Satisfiable, valuation " + result);
		}
		return coralRes;
	}
	
	private Result convertCoralRes(coral.solvers.Result coralRes) {
		if(coralRes == coral.solvers.Result.SAT)
			return Result.SAT;
		else if(coralRes == coral.solvers.Result.UNSAT)
			return Result.UNSAT;
		else if(coralRes == coral.solvers.Result.UNK)
			return Result.DONT_KNOW;
		else
			throw new IllegalStateException("Did not return sat, unsat, or dont_know!");
	}
}
