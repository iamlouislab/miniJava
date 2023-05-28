/**
 * 
 */
package fr.n7.stl.block.ast.instruction;

import java.util.Optional;

import fr.n7.stl.block.ast.Block;
import fr.n7.stl.block.ast.SemanticsUndefinedException;
import fr.n7.stl.block.ast.expression.Expression;
import fr.n7.stl.block.ast.scope.Declaration;
import fr.n7.stl.block.ast.scope.HierarchicalScope;
import fr.n7.stl.block.ast.type.AtomicType;
import fr.n7.stl.tam.ast.Fragment;
import fr.n7.stl.tam.ast.Register;
import fr.n7.stl.tam.ast.TAMFactory;

/**
 * Implementation of the Abstract Syntax Tree node for a conditional instruction.
 * @author Marc Pantel
 *
 */
public class Conditional implements Instruction {

	protected Expression condition;
	protected Block thenBranch;
	protected Block elseBranch;

	public Conditional(Expression _condition, Block _then, Block _else) {
		this.condition = _condition;
		this.thenBranch = _then;
		this.elseBranch = _else;
	}

	public Conditional(Expression _condition, Block _then) {
		this.condition = _condition;
		this.thenBranch = _then;
		this.elseBranch = null;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "if (" + this.condition + " )" + this.thenBranch + ((this.elseBranch != null)?(" else " + this.elseBranch):"");
	}
	
	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.instruction.Instruction#collect(fr.n7.stl.block.ast.scope.Scope)
	 */
	@Override
	public boolean collectAndBackwardResolve(HierarchicalScope<Declaration> _scope) {
		if (elseBranch == null) {
			return condition.collectAndBackwardResolve(_scope) && thenBranch.collect(_scope);
		} else {
			return condition.collectAndBackwardResolve(_scope) && thenBranch.collect(_scope) && elseBranch.collect(_scope);
		}
	}
	
	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.instruction.Instruction#resolve(fr.n7.stl.block.ast.scope.Scope)
	 */
	@Override
	public boolean fullResolve(HierarchicalScope<Declaration> _scope) {
		if (elseBranch == null) {
			return condition.fullResolve(_scope) && thenBranch.resolve(_scope);
		} else {
			return condition.fullResolve(_scope) && thenBranch.resolve(_scope) && elseBranch.resolve(_scope);
		}
		
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Instruction#checkType()
	 */
	@Override
	public boolean checkType() {
		return condition.getType().compatibleWith(AtomicType.BooleanType) && thenBranch.checkType() && (elseBranch == null || elseBranch.checkType());
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Instruction#allocateMemory(fr.n7.stl.tam.ast.Register, int)
	 */
	@Override
	public int allocateMemory(Register _register, int _offset) {
		thenBranch.allocateMemory(_register, _offset);
		if (elseBranch != null) {			
			elseBranch.allocateMemory(_register, _offset);
		}
		return  _offset;	
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Instruction#getCode(fr.n7.stl.tam.ast.TAMFactory)
	 */
	@Override
	public Fragment getCode(TAMFactory _factory) {
		Fragment ret = condition.getCode(_factory);
		int id = _factory.createLabelNumber();
		
		String label_then = "condition_then_" +id;
		String label_else = "condition_else_" + id;
		String label_end = "condition_end_" + id;
		
		ret.add(_factory.createJumpIf(label_then, 0));
		
		if (elseBranch != null) {
			Fragment else_result = elseBranch.getCode(_factory);
			else_result.addPrefix(label_else);
			ret.append(else_result);
		}
		ret.add(_factory.createJump(label_end));
		Fragment then_result = thenBranch.getCode(_factory);
		then_result.addPrefix(label_then);
		then_result.addSuffix(label_end);
		ret.append(then_result);
		return ret;
		
	}

}
