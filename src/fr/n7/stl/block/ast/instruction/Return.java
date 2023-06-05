/**
 * 
 */
package fr.n7.stl.block.ast.instruction;

import fr.n7.stl.block.ast.SemanticsUndefinedException;
import fr.n7.stl.block.ast.expression.AbstractField;
import fr.n7.stl.block.ast.expression.Expression;
import fr.n7.stl.block.ast.scope.Declaration;
import fr.n7.stl.block.ast.scope.HierarchicalScope;
import fr.n7.stl.block.ast.scope.SymbolTable;
import fr.n7.stl.block.ast.type.AtomicType;
import fr.n7.stl.tam.ast.Fragment;
import fr.n7.stl.tam.ast.Register;
import fr.n7.stl.tam.ast.TAMFactory;
import fr.n7.stl.util.Logger;

/**
 * Implementation of the Abstract Syntax Tree node for a return instruction.
 * 
 * @author Marc Pantel
 *
 */
public class Return implements Instruction {

	protected Expression value;

	public Return(Expression _value) {
		this.value = _value;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "return " + this.value + ";\n";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * fr.n7.stl.block.ast.instruction.Instruction#collect(fr.n7.stl.block.ast.scope
	 * .Scope)
	 */
	@Override
	public boolean collectAndBackwardResolve(HierarchicalScope<Declaration> _scope) {
		Logger.warning("Return collectAndBackwardResolve with value: " + value);
		return value.collectAndBackwardResolve(_scope);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * fr.n7.stl.block.ast.instruction.Instruction#resolve(fr.n7.stl.block.ast.scope
	 * .Scope)
	 */
	@Override
	public boolean fullResolve(HierarchicalScope<Declaration> _scope) {
		return value.fullResolve(_scope);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.n7.stl.block.ast.Instruction#checkType()
	 */
	@Override
	public boolean checkType() {
		if (this.value.getType().compatibleWith(AtomicType.ErrorType) && this.value instanceof AbstractField) {
			if (SymbolTable.getCurrentMethodDeclaration().getType()
					.compatibleWith(SymbolTable.getCurrentClassDeclaration().getElementsTable()
							.get(((AbstractField) this.value).getName()).getType())) {
				return true;
			} else {
				Logger.error(
						SymbolTable.getCurrentMethodDeclaration() + " is not compatible with " + this.value);
				return false;
			}
		} else {
			return this.value.getType().compatibleWith(SymbolTable.getCurrentMethodDeclaration().getType());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * fr.n7.stl.block.ast.Instruction#allocateMemory(fr.n7.stl.tam.ast.Register,
	 * int)
	 */
	@Override
	public int allocateMemory(Register _register, int _offset) {
		return _offset + value.getType().length();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.n7.stl.block.ast.Instruction#getCode(fr.n7.stl.tam.ast.TAMFactory)
	 */
	@Override
	public Fragment getCode(TAMFactory _factory) {
		Logger.warning("Return getCode with value: " + value);
		Fragment ret = _factory.createFragment();

		if (this.value.toString().startsWith("this")) {
			ret.add(_factory.createLoad(Register.LB, 0, 1));
			ret.add(_factory.createLoadI(1));
		} else if (this.value.getType() != AtomicType.ErrorType) {
			ret.add(_factory.createReturn(this.value.getType().length(), this.value.getType().length()));
		} else {
			Logger.warning("nothing");
		}
		Logger.warning("Return getCode with value: " + value + " and ret: " + ret);
		return ret;
	}

}
