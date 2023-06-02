package fr.n7.stl.block.ast.miniJava;

import java.util.List;

import fr.n7.stl.block.ast.scope.Declaration;
import fr.n7.stl.block.ast.type.Type;

public class MultipleInheritsDeclaration implements Declaration {

    protected List<Instance> instances;

    public MultipleInheritsDeclaration(List<Instance> instances) {
        this.instances = instances;
    }

    public String toString() {
        return " extends " + this.instances.toString();
    }

    public String getName() {
        return this.instances.get(0).getName();
    }

    @Override
    public Type getType() {
        return this.instances.get(0).getType();
    }
}
