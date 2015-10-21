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

import coral.solvers.SolverKind;
import gov.nasa.jpf.constraints.api.ConstraintSolver;
import gov.nasa.jpf.constraints.solvers.ConstraintSolverProvider;
import gov.nasa.jpf.constraints.solvers.coral.CoralSolver.CoralSolverBuilder;
import gov.nasa.jpf.constraints.solvers.coral.exceptions.CoralConfigurationException;

public class CoralSolverProvider implements ConstraintSolverProvider {

	@Override
	public String[] getNames() {
		return new String[]{"coral"};
	}

	@Override
	public ConstraintSolver createSolver(Properties props) {
		CoralSolverBuilder solverBuilder = new CoralSolverBuilder();
		try {
			if(props.containsKey(CoralConfig.SEED.getPropStr()))
				solverBuilder.seed(Long.parseLong(props.getProperty(CoralConfig.SEED.getPropStr())));
			
			if(props.containsKey(CoralConfig.ITERATIONS.getPropStr()))
				solverBuilder.iterations(Integer.parseInt(props.getProperty(CoralConfig.ITERATIONS.getPropStr())));
			
			if(props.containsKey(CoralConfig.SOLVER_KIND.getPropStr()))
				solverBuilder.solverKind(SolverKind.valueOf(props.getProperty(CoralConfig.SOLVER_KIND.getPropStr()).toUpperCase()));
			
			if(props.containsKey(CoralConfig.OPTIMIZE.getPropStr()))
				solverBuilder.optimize(Boolean.parseBoolean(props.getProperty(CoralConfig.OPTIMIZE.getPropStr())));
			
			if(props.containsKey(CoralConfig.INTERVAL_SOLVER.getPropStr())) {
				IntervalSolver iSolver = null;
				String intervalSolverStr = props.getProperty(CoralConfig.INTERVAL_SOLVER.getPropStr()).toUpperCase();
				IntervalSolver.Solver s = IntervalSolver.Solver.valueOf(intervalSolverStr);
				boolean hasPath = props.containsKey(CoralConfig.INTERVAL_SOLVER_PATH.getPropStr());
				if(s != IntervalSolver.Solver.NONE && !hasPath)
					throw new CoralConfigurationException("Need to specify path when using the interval solver " + s);
				if(hasPath)
					iSolver = new IntervalSolver(s, props.getProperty(CoralConfig.INTERVAL_SOLVER_PATH.getPropStr()));
				else
					iSolver = new IntervalSolver(s);
				solverBuilder.intervalSolver(iSolver);
			}
		} catch(Exception e) {
			throw new CoralConfigurationException("Invalid configuration of Coral.", e);
		}
		return solverBuilder.buildCoralSolver();
	}
}
