package fr.n7.stl.block.ast.miniJava;

import fr.n7.stl.block.ast.scope.Declaration;
import fr.n7.stl.block.ast.type.Type;

public class SingleInheritsDeclaration implements Declaration {

    protected Instance instance;

    public SingleInheritsDeclaration(Instance instance) {
        this.instance = instance;
    }

    public String toString() {
        return " extends " + this.instance.toString();
    }

    public String getName() {
        return this.instance.getName();
    }

    @Override
    public Type getType() {
        return this.instance.getType();
    }
}
