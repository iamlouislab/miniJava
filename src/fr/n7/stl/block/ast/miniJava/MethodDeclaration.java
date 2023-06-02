package fr.n7.stl.block.ast.miniJava;

import fr.n7.stl.block.ast.Block;
import fr.n7.stl.block.ast.SemanticsUndefinedException;
import fr.n7.stl.block.ast.instruction.declaration.ParameterDeclaration;
import fr.n7.stl.block.ast.scope.Declaration;
import fr.n7.stl.block.ast.scope.HierarchicalScope;
import fr.n7.stl.block.ast.scope.SymbolTable;
import fr.n7.stl.block.ast.type.AtomicType;
import fr.n7.stl.block.ast.type.Type;
import fr.n7.stl.tam.ast.Fragment;
import fr.n7.stl.tam.ast.Register;
import fr.n7.stl.tam.ast.TAMFactory;
import fr.n7.stl.util.Logger;

public class MethodDeclaration implements ClassElement {

    protected AccessRight accessRight;
    protected ElementNature state;
    protected Signature signature;
    protected Block corps;
    protected SymbolTable parametersTable;
    protected int offset;

    public MethodDeclaration(Signature signature, Block corps, ElementNature state) {
        this.state = state;
        this.signature = signature;
        this.corps = corps;
    }

    public String toString() {
        return this.accessRight.toString() + " " + this.state.toString() + " " + this.signature.toString() + " "
                + corps;
    }

    public AccessRight getAccessRight() {
        return this.accessRight;
    }

    public Signature getSignature() {
        return this.signature;
    }

    @Override
    public String getName() {
        return this.signature.getName();
    }

    @Override
    public Type getType() {
        return this.signature.getType();
    }

    @Override
    public boolean collectAndBackwardResolve(HierarchicalScope<Declaration> _scope) {
        String id = signature.identifiant.getLeft();
        String name = signature.identifiant.getLeft();
        if (this.signature.parameters != null) {
            for (ParameterDeclaration p : this.signature.parameters) {
                name += p.getType().toString();
            }
        }
        signature.identifiant.setLeft(name);
        if (((HierarchicalScope<Declaration>) _scope).accepts(this)) {
            _scope.register(this);
            SymbolTable tableParametres = new SymbolTable(_scope);
            boolean result = true;
            if (this.signature.getParameters() != null) {
                for (ParameterDeclaration d : this.signature.getParameters()) {
                    tableParametres.register(d);
                }
            }
            this.parametersTable = tableParametres;
            result = this.corps.collect(tableParametres);
            return result;
        } else {
            Logger.error("The method identifier " + id + " is already defined.");
            return false;
        }
    }

    @Override
    public boolean fullResolve(HierarchicalScope<Declaration> _scope) {
        SymbolTable.setCurrentMethodDeclaration(this);
        return this.corps.resolve(this.parametersTable);
    }

    @Override
    public boolean checkType() {
        SymbolTable.setCurrentMethodDeclaration(this);
        if (this.signature.parameters != null) {
            for (ParameterDeclaration parameterDeclaration : this.signature.parameters) {
                if (parameterDeclaration.getType().equalsTo(AtomicType.ErrorType)) {
                    Logger.error(parameterDeclaration + " is not compatible with parameters type.");
                    return false;
                }
            }
        }
        return corps.checkType();
    }

    @Override
    public int allocateMemory(Register _register, int _offset) {
        this.offset = _offset;
        this.corps.allocateMemory(_register, _offset);
        return _offset;
    }

    @Override
    public Fragment getCode(TAMFactory _factory) {
        throw new SemanticsUndefinedException("Semantics getCode is not implemented in MethodDeclaration.");
    }

    @Override
    public void setAccessRight(AccessRight _accessRight) {
        this.accessRight = _accessRight;
    }

}
