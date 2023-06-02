package fr.n7.stl.block.ast.miniJava;

import fr.n7.stl.block.ast.expression.AbstractIdentifier;
import fr.n7.stl.block.ast.expression.assignable.AssignableExpression;
import fr.n7.stl.block.ast.scope.Declaration;
import fr.n7.stl.block.ast.scope.HierarchicalScope;
import fr.n7.stl.block.ast.type.Type;
import fr.n7.stl.tam.ast.Fragment;
import fr.n7.stl.tam.ast.TAMFactory;

public class This extends AbstractIdentifier implements AssignableExpression {

    protected ClassDeclaration declaration;

    public This() {
        super("this");
    }

    @Override
    public boolean collectAndBackwardResolve(HierarchicalScope<Declaration> _scope) {
        return true;
    }

    @Override
    public boolean fullResolve(HierarchicalScope<Declaration> _scope) {
        return true;
    }

    @Override
    public Type getType() {
        return new Instance(this.name);
    }

    @Override
    public Fragment getCode(TAMFactory _factory) {
        Fragment _result = _factory.createFragment();
        return _result;
    }

}
