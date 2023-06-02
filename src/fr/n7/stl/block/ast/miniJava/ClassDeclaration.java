package fr.n7.stl.block.ast.miniJava;

import java.util.ArrayList;
import java.util.List;

import fr.n7.stl.block.ast.scope.Declaration;
import fr.n7.stl.block.ast.scope.HierarchicalScope;
import fr.n7.stl.block.ast.scope.SymbolTable;
import fr.n7.stl.block.ast.type.Type;
import fr.n7.stl.tam.ast.Fragment;
import fr.n7.stl.tam.ast.Register;
import fr.n7.stl.tam.ast.TAMFactory;
import fr.n7.stl.util.Logger;

public class ClassDeclaration implements Element, Declaration {

    protected String name;
    protected List<ClassElement> classElements;
    protected SymbolTable classSymbolTable;

    public ClassDeclaration(String _name, List<ClassElement> _classElements) {
        this.name = _name;
        this.classElements = _classElements;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Type getType() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getType'");
    }

    @Override
    public boolean collect(HierarchicalScope<Declaration> _scope) {
        if (_scope.accepts(this)) {
            _scope.register(this);
            SymbolTable.setCurrentClassDeclaration(this);
            SymbolTable.addClassDeclaration(this);
            this.classSymbolTable = new SymbolTable(_scope);
            for (int i = this.classElements.size() - 1; i >= 0; i--) {
                ClassElement e = this.classElements.get(i);
                Logger.warning("Collecting " + e);
                if (!e.collectAndBackwardResolve(this.classSymbolTable)) {
                    Logger.error("The class " + this.name + " is not valid.");
                    return false;
                }
            }
            return true;
        } else {
            Logger.error("The name " + this.name + " is already used in this scope.");
            return false;
        }
    }

    @Override
    public boolean resolve(HierarchicalScope<Declaration> _scope) {
        for (ClassElement e : this.classElements) {
            if (!e.fullResolve(this.classSymbolTable)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean checkType() {
        SymbolTable.setCurrentClassDeclaration(this);
        for (ClassElement e : this.classElements) {
            if (!e.checkType()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int allocateMemory(Register _register, int _offset) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'allocateMemory'");
    }

    @Override
    public Fragment getCode(TAMFactory _factory) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getCode'");
    }

    public List<ClassElement> getClassElements() {
        return this.classElements;
    }

    public List<AttributeDeclaration> getClassAttributes() {
        List<AttributeDeclaration> list = new ArrayList<>();
        for (Declaration d : classElements) {
            if (d instanceof AttributeDeclaration) {
                list.add((AttributeDeclaration) d);
            }
        }
        return list;
    }

    public List<MethodDeclaration> getClassMethods() {
        List<MethodDeclaration> list = new ArrayList<>();
        for (Declaration d : classElements) {
            if (d instanceof MethodDeclaration) {
                list.add((MethodDeclaration) d);
            }
        }
        return list;
    }

    public List<ConstructorDeclaration> getClassConstructors() {
        List<ConstructorDeclaration> list = new ArrayList<>();
        for (Declaration d : classElements) {
            if (d instanceof ConstructorDeclaration) {
                list.add((ConstructorDeclaration) d);
            }
        }
        return list;
    }

    public SymbolTable getElementsTable() {
        return this.classSymbolTable;
    }

}
