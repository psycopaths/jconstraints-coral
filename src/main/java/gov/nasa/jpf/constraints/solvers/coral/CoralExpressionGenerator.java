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

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import coral.PC;
import symlib.SymAsDouble;
import symlib.SymBool;
import symlib.SymDouble;
import symlib.SymDoubleConstant;
import symlib.SymFloat;
import symlib.SymFloatConstant;
import symlib.SymInt;
import symlib.SymIntConstant;
import symlib.SymLiteral;
import symlib.SymLong;
import symlib.SymLongConstant;
import symlib.SymNumber;
import symlib.Util;
import gov.nasa.jpf.constraints.api.Expression;
import gov.nasa.jpf.constraints.api.Variable;
import gov.nasa.jpf.constraints.expressions.AbstractExpressionVisitor;
import gov.nasa.jpf.constraints.expressions.BitvectorExpression;
import gov.nasa.jpf.constraints.expressions.BitvectorOperator;
import gov.nasa.jpf.constraints.expressions.CastExpression;
import gov.nasa.jpf.constraints.expressions.Constant;
import gov.nasa.jpf.constraints.expressions.Negation;
import gov.nasa.jpf.constraints.expressions.NumericBooleanExpression;
import gov.nasa.jpf.constraints.expressions.NumericComparator;
import gov.nasa.jpf.constraints.expressions.NumericCompound;
import gov.nasa.jpf.constraints.expressions.PropositionalCompound;
import gov.nasa.jpf.constraints.expressions.UnaryMinus;
import gov.nasa.jpf.constraints.expressions.functions.FunctionExpression;
import gov.nasa.jpf.constraints.types.BuiltinTypes.BigDecimalType;
import gov.nasa.jpf.constraints.types.BuiltinTypes.DoubleType;
import gov.nasa.jpf.constraints.types.BuiltinTypes.FloatType;
import gov.nasa.jpf.constraints.types.BuiltinTypes.SInt32Type;
import gov.nasa.jpf.constraints.types.BuiltinTypes.SInt64Type;
import gov.nasa.jpf.constraints.types.IntegerType;
import gov.nasa.jpf.constraints.types.RealType;
import gov.nasa.jpf.constraints.types.Type;
import gov.nasa.jpf.constraints.types.BuiltinTypes.BoolType;

/*
 * The syntax tree of coral does not have an Expression root. 
 * Therefore we need to return Object for all visit methods...
 */
public class CoralExpressionGenerator extends AbstractExpressionVisitor<Object, Void> {
  
	private HashMap<Variable<?>, SymLiteral> vars;
	
	public CoralExpressionGenerator() {
		Util.resetID(); //resets Coral's var counter...
		this.vars = new HashMap<>();
	}
	
	public HashMap<Variable<?>, SymLiteral> getVariables() {
		return this.vars;
	}


	public PC generateAssertion(Expression<Boolean> e) {
		List<SymBool> constraints = new LinkedList<>(Arrays.asList((SymBool)visit(e, null)));
		return new PC(constraints);
	}
	
	@Override
	public <E> Object visit(Constant<E> c, Void data) {
		Type<E> type = c.getType();

		if(type instanceof BoolType) {
			return Util.createConstant(((Boolean)c.getValue()).booleanValue());
		} else if(type instanceof IntegerType) {
			if(type instanceof SInt64Type)
				return Util.createConstant(((Long)c.getValue()).longValue());
			else
				return Util.createConstant(((Integer)c.getValue()).intValue());
		} else if(type instanceof FloatType) {
			return Util.createConstant(((Float)c.getValue()).floatValue());
		} else if(type instanceof DoubleType) {
			return Util.createConstant(((Double)c.getValue()).doubleValue());
		} //Well... this introduces some imprecision 
		else if(type instanceof BigDecimalType) { 
		  return Util.createConstant(((BigDecimal)c.getValue()).doubleValue());
		}
		throw new IllegalStateException("Cannot handle expression type " + type);
	}
	
	@Override
	public Object visit(Negation n, Void data) {
	    SymBool negatedExpr = (SymBool)visit(n.getNegated());
	    return Util.neg(negatedExpr);
  }
	
	@Override
	public Object visit(NumericBooleanExpression n, Void data) {
		Object left = visit(n.getLeft(), null);
		Object right = visit(n.getRight(), null);
		NumericComparator cmp = n.getComparator();

    //type checking
    //We always convert to double types if left and right hand sides are incompatible...
    if(!areTypeCompatible(left, right)) {
      if(isIntegerType(left))
        left = Util.createASDouble((SymInt)left);
      else
        right = Util.createASDouble((SymInt)right);
    } else {
      if(!areSameType(left, right)) {
        if(isRealType(left) || isRealType(right)) { //we know they are type compatible, so this is sound
          if(!(left instanceof SymDouble && right instanceof SymDouble)) {
            if(left instanceof SymDouble) {
              right = Util.createASDouble((SymNumber)right);
            } else if(right instanceof SymDouble) {
              left = Util.createASDouble((SymNumber)left);
            }
          }
        } else if(isIntegerType(left) || isIntegerType(right)) { //This check is probably superfluous
          if(!(left instanceof SymInt && right instanceof SymInt)) {
            if(left instanceof SymLong) { //We lose precision here, but what can we do?
              left = Util.createASInt((SymNumber)left);
            } else if(right instanceof SymLong) {
              right = Util.createASInt((SymNumber)right);
            }
          }
        } else {
          throw new IllegalArgumentException("Cannot type cast " + left.getClass().getName() + " and " + right.getClass().getName());
        }
      }
    }

		switch(cmp) {
		case EQ:
		  if(left instanceof SymDouble)
        return Util.eq((SymDouble)left, (SymDouble)right);
      else if(left instanceof SymFloat)
        return Util.eq((SymFloat)left, (SymFloat)right);
      else if(left instanceof SymInt)
        return Util.eq((SymInt)left, (SymInt)right);
      else if(left instanceof SymLong)
        return Util.eq((SymLong)left, (SymLong)right);
		case NE:
      if(left instanceof SymDouble)
        return Util.ne((SymDouble)left, (SymDouble)right);
      else if(left instanceof SymFloat)
        return Util.ne((SymFloat)left, (SymFloat)right);
      else if(left instanceof SymInt)
        return Util.ne((SymInt)left, (SymInt)right);
      else if(left instanceof SymLong)
        return Util.ne((SymLong)left, (SymLong)right);
		case GE:
		  if(left instanceof SymDouble)
        return Util.ge((SymDouble)left, (SymDouble)right);
      else if(left instanceof SymFloat)
        return Util.ge((SymFloat)left, (SymFloat)right);
      else if(left instanceof SymInt)
        return Util.ge((SymInt)left, (SymInt)right);
      else if(left instanceof SymLong)
        return Util.ge((SymLong)left, (SymLong)right);
		case GT:
		  if(left instanceof SymDouble) {
		    /*if(right instanceof SymFloat) {
          right = Util.createASDouble((SymNumber)right);
        }*/
		    return Util.gt((SymDouble)left, (SymDouble)right);
		  }
      else if(left instanceof SymFloat)
        return Util.gt((SymFloat)left, (SymFloat)right);
      else if(left instanceof SymInt)
        return Util.gt((SymInt)left, (SymInt)right);
      else if(left instanceof SymLong)
        return Util.gt((SymLong)left, (SymLong)right);
		case LE:
		  if(left instanceof SymDouble) {
        /*if(right instanceof SymFloat) {
          right = Util.createASDouble((SymNumber)right);
        }*/
		    return Util.le((SymDouble)left, (SymDouble)right);
		  } else if(left instanceof SymFloat)
        return Util.le((SymFloat)left, (SymFloat)right);
      else if(left instanceof SymInt)
        return Util.le((SymInt)left, (SymInt)right);
      else if(left instanceof SymLong)
        return Util.le((SymLong)left, (SymLong)right);
		case LT:
		  if(left instanceof SymDouble)
        return Util.lt((SymDouble)left, (SymDouble)right);
      else if(left instanceof SymFloat)
        return Util.lt((SymFloat)left, (SymFloat)right);
      else if(left instanceof SymInt)
        return Util.lt((SymInt)left, (SymInt)right);
      else if(left instanceof SymLong)
        return Util.lt((SymLong)left, (SymLong)right);
		  default:
		    throw new UnsupportedOperationException("No support for cmp operator: " + cmp.toString());  
		}
	}
	
	private boolean isIntegerType(Object a) {
		return (a instanceof SymInt) || (a instanceof SymLong);
	}
	
	private boolean isRealType(Object a) {
		return (a instanceof SymDouble) || (a instanceof SymFloat);
	}
	
	private boolean areTypeCompatible(Object a, Object b) {
		if((isIntegerType(a) && isIntegerType(b)) ||
		   (isRealType(a) && isRealType(b))) {
			return true;
		} else
			return false;
	}
	
	@Override
	public <F,E> Object visit(CastExpression<F,E> cast, Void data) {
		Expression<F> casted = cast.getCasted();
	    Type<F> ft = casted.getType();
	    Type<E> tt = cast.getType();
	    
	    if(ft.equals(tt))
	      return visit(casted, null);
	    
	    Object castedExpr = visit(casted, null);
	    if(tt instanceof IntegerType<?>) {
	    	return Util.createASInt((SymNumber)castedExpr);
	    } else if(tt instanceof RealType<?>) {
	    	return Util.createASDouble((SymNumber)castedExpr);
	    }
	    throw new UnsupportedOperationException("Cast from " + ft.getName() + " to type " + tt.getName() + " is not supported");
	}
	
	private boolean isZeroConstant(Expression<?> expr) {
		if(expr instanceof Constant<?>) {
			Constant<?> co = (Constant<?>)expr;
			Object val = co.getValue();
			Type<?> t = co.getType();
			if(t instanceof SInt32Type) {
				return (((Integer)val).intValue() == 0);
			} else if(t instanceof SInt64Type) {
				return (((Long)val).longValue() == 0L);
			} else if(t instanceof FloatType) {
				return (((Float)val).floatValue() == 0.0f);
			} else if(t instanceof DoubleType) {
				return (((Double)val).doubleValue() == 0.0d);
			}
		}
		return false;		
	}
	
	private boolean areSameType(Object a, Object b) {
	  return (a instanceof SymDouble && b instanceof SymDouble) ||
	         (a instanceof SymInt && b instanceof SymInt) ||
           (a instanceof SymFloat && b instanceof SymFloat) ||
           (a instanceof SymLong && b instanceof SymLong);
	}
	
	@Override
	public <E> Object visit(NumericCompound<E> n, Void data) {
		Object left = null, right = null;

		left = visit(n.getLeft());
		right = visit(n.getRight());
		
		//type checking
		//We always convert to double types if left and right hand sides are incompatible...
		if(!areTypeCompatible(left, right)) {
			if(isIntegerType(left))
				left = Util.createASDouble((SymInt)left);
			else
				right = Util.createASDouble((SymInt)right);
		} else {
		  if(!areSameType(left, right)) {
		    if(isRealType(left) || isRealType(right)) { //we know they are type compatible, so this is sound
		      if(!(left instanceof SymDouble && right instanceof SymDouble)) {
  		      if(left instanceof SymDouble) {
  		        right = Util.createASDouble((SymNumber)right);
  		      } else if(right instanceof SymDouble) {
  		        left = Util.createASDouble((SymNumber)left);
  		      }
		      }
		    } else if(isIntegerType(left) || isIntegerType(right)) { //This check is probably superfluous
		      if(!(left instanceof SymInt && right instanceof SymInt)) {
            if(left instanceof SymLong) { //We lose precision here, but what can we do?
              left = Util.createASInt((SymNumber)left);
            } else if(right instanceof SymLong) {
              right = Util.createASInt((SymNumber)right);
            }
		      }
		    } else {
		      throw new IllegalArgumentException("Cannot type cast " + left.getClass().getName() + " and " + right.getClass().getName());
		    }
		  }
		}
		
		switch(n.getOperator()) {
		case PLUS:
		  //Not sure if these checks optimizes anything. At least the clauses sent to coral will be smaller
			if(isZeroConstant(n.getLeft()))
				return right;
			else if(isZeroConstant(n.getRight()))
				return left;
			
			if(left instanceof SymDouble)
				return Util.add((SymDouble)left, (SymDouble)right);
			else if(left instanceof SymFloat)
				return Util.add((SymFloat)left, (SymFloat)right);
			else if(left instanceof SymInt)
				return Util.add((SymInt)left, (SymInt)right);
			else if(left instanceof SymLong)
				return Util.add((SymLong)left, (SymLong)right);
		case MINUS:
      //Not sure if these checks optimizes anything. At least the clauses sent to coral will be smaller
			if(isZeroConstant(n.getLeft()))
				return right;
			else if(isZeroConstant(n.getRight()))
				return left;
			
			if(left instanceof SymDouble)
				return Util.sub((SymDouble)left, (SymDouble)right);
			else if(left instanceof SymFloat)
				return Util.sub((SymFloat)left, (SymFloat)right);
			else if(left instanceof SymInt)
				return Util.sub((SymInt)left, (SymInt)right);
			else if(left instanceof SymLong)
				return Util.sub((SymLong)left, (SymLong)right);
		case DIV:
			if(left instanceof SymDouble)
				return Util.div((SymDouble)left, (SymDouble)right);
			else if(left instanceof SymFloat)
				return Util.div((SymFloat)left, (SymFloat)right);
			else if(left instanceof SymInt)
				return Util.div((SymInt)left, (SymInt)right);
			else if(left instanceof SymLong)
				return Util.div((SymLong)left, (SymLong)right);
		case MUL:
			if(left instanceof SymDouble || left instanceof SymAsDouble) {
	       /*if(right instanceof SymFloat) {
	          right = Util.createASDouble((SymNumber)right);
	        }*/
			  try {
				return Util.mul((SymDouble)left, (SymDouble)right);
			  }
			  catch(ClassCastException e) {
			    System.out.println("here");
			  }
			} else if(left instanceof SymFloat) {
				/*if(right instanceof SymDouble) {
				  left = Util.createASDouble((SymNumber)left);
				  return Util.mul((SymDouble)left, (SymDouble)right);
				}*/
			  try {
			  return Util.mul((SymFloat)left, (SymFloat)right);
      }
      catch(ClassCastException e) {
        System.out.println("here");
      }
			}
			else if(left instanceof SymInt)
				return Util.mul((SymInt)left, (SymInt)right);
			else if(left instanceof SymLong)
				return Util.mul((SymLong)left, (SymLong)right);
		case REM: //TODO: Check if semantics of REM and MOD are the same here
			if(left instanceof SymDouble)
				return Util.mod((SymDouble)left, (SymDouble)right);
			else if(left instanceof SymFloat)
				return Util.mod((SymFloat)left, (SymFloat)right);
			else if(left instanceof SymInt)
				return Util.mod((SymInt)left, (SymInt)right);
			else if(left instanceof SymLong)
				return Util.mod((SymLong)left, (SymLong)right);
		default:
			throw new IllegalArgumentException("Cannot handle numeric operator " + n.getOperator());
		}
	}
	@Override
	public Object visit(PropositionalCompound n, Void data) {
		SymBool left = null, right = null;         
		left = (SymBool)visit(n.getLeft(), null);
		right = (SymBool)visit(n.getRight(), null);
		switch(n.getOperator()) {
		case AND:
			return Util.and(left, right);
		case OR:
			return Util.or(left, right);
		case XOR:
			return Util.xor(left, right);
		case EQUIV:
		case IMPLY:
		default:
			throw new IllegalStateException("Cannot handle logical operator " + n.getOperator());
		}
	}
	@Override
	public <E> Object visit(FunctionExpression<E> f, Void data) {
		String funcName = f.getFunction().getName();
		SymDouble[] args = new SymDouble[f.getArgs().length];

		int i = 0;
		for(Expression<?> exp : f.getArgs()) {
			Object e = (Object)visit(exp);
			if(e instanceof SymDouble)
				args[i++] = (SymDouble)e;
			else if(e instanceof SymInt)
				args[i++] = Util.createASDouble((SymInt)e);
			else
				new IllegalArgumentException(funcName + " does not expect arg of type: " + e.getClass().toString()); 
		}
		switch(funcName) {
		case "cos":
			return Util.cos(args[0]);
		case "sin":
			return Util.sin(args[0]);
		case "tan":
			return Util.tan(args[0]);
		case "acos":
			return Util.acos(args[0]);
		case "asin":
			return Util.asin(args[0]);
		case "atan":
			return Util.atan(args[0]);
		case "exp":
			return Util.exp(args[0]);
		case "log10":
		  return Util.log10(args[0]);
		case "log": //Natural (e) log
			return Util.log(args[0]);
		case "sqrt":
			return Util.sqrt(args[0]);
		case "atan2":
			return Util.atan2(args[0], args[1]);
		case "pow":
			return Util.pow(args[0], args[1]);
		case "round":
		  return Util.round(args[0]);
    case "java.lang.Double.isNaN":
      return Util.ne(args[0], args[0]);
		default:
			throw new IllegalStateException("Cannot handle function " + funcName);
		}
	}

	@Override
	public <E> Object visit(UnaryMinus<E> n, Void data) {
		Object negated = null;
		negated = visit(n.getNegated(), null);
		if(negated instanceof SymInt) {
			return Util.mul(new SymIntConstant(-1), (SymInt)negated);
		} else if(negated instanceof SymLong) {
      return Util.mul(new SymLongConstant(-1L), (SymLong)negated);
		} else if(negated instanceof SymDouble) {
			return Util.mul(new SymDoubleConstant(-1.0), (SymDouble)negated);
		} else if(negated instanceof SymFloat) {
      return Util.mul(new SymFloatConstant(-1.0f), (SymFloat)negated);
    } else
			throw new UnsupportedOperationException("Unary minus not defined for " + negated.getClass());
	}
	
	@Override
	public <E> Object visit(BitvectorExpression<E> bv, Void data) {
		//int: xor, or, and, usr, sr, sl, cmp
		//long: xor, or, and, arithmeticshiftleft, arithmeticshiftright, logicalshiftright
		
		//TODO: Missing:
		//int: cmp
		//long: logicalshiftright
		
		Object left = visit(bv.getLeft());
		Object right = visit(bv.getRight());
		
		//Type checking
		if(!(left instanceof SymInt) && !(left instanceof SymLong))
			throw new IllegalArgumentException("First operand must be of type " + SymInt.class.getName() + " or " + SymLong.class.getName());				
	
		BitvectorOperator op = bv.getOperator();
		switch(op) {
		case AND:
			if(left instanceof SymInt) {
				if(right instanceof SymInt)
					return Util.and((SymInt)left, (SymInt)right);
				else
					throw new IllegalArgumentException("Second operand must be of type " + SymInt.class);
			} else if(left instanceof SymLong) {
				if(right instanceof SymLong)
					return Util.and((SymLong)left, (SymLong)right);
				else
					throw new IllegalArgumentException("Second operand must be of type " + SymLong.class);
			}
		case OR:
			if(left instanceof SymInt) {
				if(right instanceof SymInt)
					return Util.or((SymInt)left, (SymInt)right);
				else
					throw new IllegalArgumentException("Second operand must be of type " + SymInt.class);
			} else if(left instanceof SymLong) {
				if(right instanceof SymLong)
					return Util.or((SymLong)left, (SymLong)right);
				else
					throw new IllegalArgumentException("Second operand must be of type " + SymLong.class);
			}
		case XOR:
			if(left instanceof SymInt) {
				if(right instanceof SymInt)
					return Util.xor((SymInt)left, (SymInt)right);
				else
					throw new IllegalArgumentException("Second operand must be of type " + SymInt.class);
			} else if(left instanceof SymLong) {
				if(right instanceof SymLong)
					return Util.xor((SymLong)left, (SymLong)right);
				else
					throw new IllegalArgumentException("Second operand must be of type " + SymLong.class);
			}
		case SHIFTL:
			if((left instanceof SymLong) && (right instanceof SymInt))
				return Util.arithmeticShiftLeft((SymLong)left, (SymInt)right);
			else if((left instanceof SymInt) && (right instanceof SymInt))
				return Util.sl((SymInt)left, (SymInt)right);
			else
				throw new IllegalArgumentException("Incompatible types for " + BitvectorOperator.SHIFTL);
		case SHIFTR:
			if((left instanceof SymLong) && (right instanceof SymInt))
				return Util.arithmeticShiftRight((SymLong)left, (SymInt)right);
			else if((left instanceof SymInt) && (right instanceof SymInt))
				return Util.sr((SymInt)left, (SymInt)right);
			else
				throw new IllegalArgumentException("Incompatible types for " + BitvectorOperator.SHIFTR);
		case SHIFTUR:
			if((left instanceof SymInt) && (right instanceof SymInt))
				return Util.usr((SymInt)left, (SymInt)right);
			else
				throw new IllegalArgumentException("Incompatible types for " + BitvectorOperator.SHIFTUR);
		default:
			throw new UnsupportedOperationException(op.name() + " operator not supported");
		}
	}

	@Override
	public <E> Object visit(Variable<E> v, Void data) {
		SymLiteral var = this.vars.get(v);
		if(var == null) {
			Type<?> type = v.getType();
			if(type instanceof BoolType)
				var = Util.createSymLiteral(true);
			else if(type instanceof IntegerType) {
				if(type instanceof SInt64Type)
					var = Util.createSymLiteral(0l);
				else
					var = Util.createSymLiteral(0);
			}
			else if(type instanceof FloatType) {
				var = Util.createSymLiteral(0f);
			} else if(type instanceof DoubleType)
				var = Util.createSymLiteral(0d);
			else
				throw new IllegalArgumentException("Cannot handle variable type " + type);
			this.vars.put(v, var);
		}
		return var;		
	}

}
