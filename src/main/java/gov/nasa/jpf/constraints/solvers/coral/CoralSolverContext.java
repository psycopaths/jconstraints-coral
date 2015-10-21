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

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import gov.nasa.jpf.constraints.api.ConstraintSolver.Result;
import gov.nasa.jpf.constraints.api.Expression;
import gov.nasa.jpf.constraints.api.SolverContext;
import gov.nasa.jpf.constraints.api.Valuation;
import gov.nasa.jpf.constraints.expressions.LogicalOperator;
import gov.nasa.jpf.constraints.expressions.PropositionalCompound;
import gov.nasa.jpf.constraints.util.ExpressionUtil;

public class CoralSolverContext extends SolverContext {

  private final CoralSolver coral;
  private Deque<List<Expression<Boolean>>> exprStack = new ArrayDeque<List<Expression<Boolean>>>();

  public CoralSolverContext(CoralSolver solver) {
    this.coral = solver;

    //Push initial context
    exprStack.push(new LinkedList<Expression<Boolean>>());
  }

  @Override
  public void push() {
    depth++;
    exprStack.push(new LinkedList<Expression<Boolean>>());
  }
  int depth = 0;
  @Override
  public void pop(int n) {
    depth -= n;
    for(int i = 0; i < n; i++)
      exprStack.pop();
  }

  @Override
  public Result solve(Valuation val) {
    if(this.exprStack.isEmpty()) {
      return Result.UNSAT;
      //throw new IllegalStateException("No expression to solve!");
    }
    Expression<Boolean> expr = combineDeque(this.exprStack);
    //Expression<Boolean> expr = getExpr(this.exprStack);
    return this.coral.solve(expr, val);
  }

  private Expression<Boolean> combineDeque(Deque<List<Expression<Boolean>>> deq) {
    List<Expression<Boolean>> cList = new ArrayList<>();
    for(List<Expression<Boolean>> l : deq) {
      cList.add(ExpressionUtil.and(l));
    }
    return ExpressionUtil.and(cList);
  }

  @Override
  public void add(List<Expression<Boolean>> expressions) {
    exprStack.peek().addAll(expressions);
  }

  @Override
  public void dispose() {

  }
}
