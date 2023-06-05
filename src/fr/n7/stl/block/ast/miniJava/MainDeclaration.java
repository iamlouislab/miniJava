package fr.n7.stl.block.ast.miniJava;

import fr.n7.stl.block.ast.Block;
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

import java.util.List;

import java.util.Iterator;

public class MainDeclaration implements Element, Declaration {

    protected List<ParameterDeclaration> parameters;
    protected Block block;
    protected HierarchicalScope<Declaration> _localScope;

    /**
     * Constructor for a MainDeclaration
     */
    public MainDeclaration(List<ParameterDeclaration> _parameters, Block _block) {
        this.parameters = _parameters;
        this.block = _block;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        String _result = "public class Main {\npublic static void main(";
        Iterator<ParameterDeclaration> _iter = this.parameters.iterator();
        if (_iter.hasNext()) {
            _result += _iter.next();
            while (_iter.hasNext()) {
                _result += " ," + _iter.next();
            }
        }
        return _result + " )" + this.block + "}";
    }

    @Override
    public boolean collect(HierarchicalScope<Declaration> _scope) {
        if (SymbolTable.getCurrentMainDeclaration() != null && SymbolTable.getCurrentMainDeclaration() != this) {
            Logger.error("There is already a main declaration.");
            return false;
        }
        SymbolTable.setCurrentMainDeclaration(this);
        _localScope = new SymbolTable(_scope);
        for (ParameterDeclaration d : this.parameters) {
            _scope.register(d);
        }
        return this.block.collect(_localScope);
    }

    @Override
    public boolean resolve(HierarchicalScope<Declaration> _scope) {
        return this.block.resolve(_localScope);
    }

    @Override
    public boolean checkType() {
        SymbolTable.setCurrentMainDeclaration(this);
        if (this.parameters != null) {
            for (ParameterDeclaration parameterDeclaration : this.parameters) {
                if (parameterDeclaration.getType().equalsTo(AtomicType.ErrorType)) {
                    Logger.error(parameterDeclaration + " is not compatible with parameters type.");
                    return false;
                }
            }
        }
        return this.block.checkType();
    }

    @Override
    public int allocateMemory(Register _register, int _offset) {
        int _result = 0;
        this.block.allocateMemory(_register, _offset);
        return _result;
    }

    @Override
    public Fragment getCode(TAMFactory _factory) {
        Fragment ret = _factory.createFragment();
        ret.append(this.block.getCode(_factory));
        ret.addPrefix("BEGIN:MAIN");
        ret.add(_factory.createHalt());
        ret.addSuffix("END:MAIN");
        return ret;

    }

    @Override
    public String getName() {
        return "main";
    }

    @Override
    public Type getType() {
        return AtomicType.VoidType;
    }

}
