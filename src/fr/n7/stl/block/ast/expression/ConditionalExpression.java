/**
 * 
 */
package fr.n7.stl.block.ast.expression;

import fr.n7.stl.block.ast.SemanticsUndefinedException;
import fr.n7.stl.block.ast.expression.accessible.AccessibleExpression;
import fr.n7.stl.block.ast.instruction.Conditional;
import fr.n7.stl.block.ast.scope.Declaration;
import fr.n7.stl.block.ast.scope.HierarchicalScope;
import fr.n7.stl.block.ast.type.Type;
import fr.n7.stl.tam.ast.Fragment;
import fr.n7.stl.tam.ast.TAMFactory;

/**
 * Abstract Syntax Tree node for a conditional expression.
 * 
 * @author Marc Pantel
 *
 */
public class ConditionalExpression implements Expression {

	/**
	 * AST node for the expression whose value is the condition for the conditional
	 * expression.
	 */
	protected Expression condition;

	/**
	 * AST node for the expression whose value is the then parameter for the
	 * conditional expression.
	 */
	protected Expression thenExpression;

	/**
	 * AST node for the expression whose value is the else parameter for the
	 * conditional expression.
	 */
	protected Expression elseExpression;

	/**
	 * Builds a binary expression Abstract Syntax Tree node from the left and right
	 * sub-expressions
	 * and the binary operation.
	 * 
	 * @param _left     : Expression for the left parameter.
	 * @param _operator : Binary Operator.
	 * @param _right    : Expression for the right parameter.
	 */
	public ConditionalExpression(Expression _condition, Expression _then, Expression _else) {
		this.condition = _condition;
		this.thenExpression = _then;
		this.elseExpression = _else;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * fr.n7.stl.block.ast.expression.Expression#collect(fr.n7.stl.block.ast.scope.
	 * Scope)
	 */
	@Override
	public boolean collectAndBackwardResolve(HierarchicalScope<Declaration> _scope) {
		return condition.collectAndBackwardResolve(_scope) && thenExpression.collectAndBackwardResolve(_scope)
				&& elseExpression.collectAndBackwardResolve(_scope);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * fr.n7.stl.block.ast.expression.Expression#resolve(fr.n7.stl.block.ast.scope.
	 * Scope)
	 */
	@Override
	public boolean fullResolve(HierarchicalScope<Declaration> _scope) {
		return condition.fullResolve(_scope) && thenExpression.fullResolve(_scope)
				&& elseExpression.fullResolve(_scope);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "(" + this.condition + " ? " + this.thenExpression + " : " + this.elseExpression + ")";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.n7.stl.block.ast.Expression#getType()
	 */
	@Override
	public Type getType() {
		return thenExpression.getType();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.n7.stl.block.ast.Expression#getCode(fr.n7.stl.tam.ast.TAMFactory)
	 */
	@Override
	public Fragment getCode(TAMFactory _factory) {
		int id = _factory.createLabelNumber();
		Fragment ret = this.condition.getCode(_factory);
		ret.add(_factory.createJumpIf("elseExpression_" + id, 0));
		ret.append(this.thenExpression.getCode(_factory));

		ret.add(_factory.createJump("finIf_" + id));
		ret.addSuffix("elseExpression_" + id);
		ret.append(this.elseExpression.getCode(_factory));
		ret.addSuffix("finIf_" + id);
		return ret;
	}

}
