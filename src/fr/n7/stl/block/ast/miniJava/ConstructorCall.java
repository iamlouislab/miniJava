package fr.n7.stl.block.ast.miniJava;

import java.util.Iterator;
import java.util.List;

import fr.n7.stl.block.ast.SemanticsUndefinedException;
import fr.n7.stl.block.ast.expression.Expression;
import fr.n7.stl.block.ast.scope.Declaration;
import fr.n7.stl.block.ast.scope.HierarchicalScope;
import fr.n7.stl.block.ast.type.Type;
import fr.n7.stl.tam.ast.Fragment;
import fr.n7.stl.tam.ast.Register;
import fr.n7.stl.tam.ast.TAMFactory;
import fr.n7.stl.util.Logger;

public class ConstructorCall implements Expression {

    protected Type element;

    protected List<Expression> parameters;

    public ConstructorCall(Type _element, List<Expression> _parametres) {
        this.element = _element;
        this.parameters = _parametres;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        String _result = "new " + this.element + "(";
        if (parameters != null) {
            Iterator<Expression> _iter = this.parameters.iterator();
            if (_iter.hasNext()) {
                _result += _iter.next();
                while (_iter.hasNext()) {
                    _result += " ," + _iter.next();
                }
            }
        }
        return _result + ")";
    }

    @Override
    public boolean collectAndBackwardResolve(HierarchicalScope<Declaration> _scope) {
        String identifiant = this.element.toString();
        Declaration d = _scope.get(identifiant);
        String name = identifiant;
        /**
         * if (this.parameters != null) {
         * for (Expression p : this.parameters) {
         * name += p.getType().toString();
         * }
         * }
         */
        boolean _result = true;
        if (d instanceof ClassDeclaration) {
            List<ConstructorDeclaration> constructors = ((ClassDeclaration) d).getClassConstructors();
            boolean found = false;
            for (ConstructorDeclaration c : constructors) {
                if (name.equals(c.getName())) {
                    found = true;
                    if (this.parameters != null) {
                        for (Expression exp : this.parameters) {
                            _result = _result && exp.collectAndBackwardResolve(_scope);
                        }
                    }
                }
            }
            if (found) {
                return _result;
            } else {
                Logger.error(
                        "No constructor of the class " + this.element.toString() + " with such parameters was found !");
                return false;
            }
        } else {
            Logger.error("The class identifier " + this.element.toString() + " is not defined.");
            return false;
        }
    }

    @Override
    public boolean fullResolve(HierarchicalScope<Declaration> _scope) {
        String identifiant = this.element.toString();
        Declaration d = _scope.get(identifiant);
        String name = identifiant;
        if (this.parameters != null) {
            for (Expression p : this.parameters) {
                name += p.getType().toString();
            }
        }
        boolean _result = true;
        if (d instanceof ClassDeclaration) {
            List<ConstructorDeclaration> constructors = ((ClassDeclaration) d).getClassConstructors();
            boolean found = false;
            for (ConstructorDeclaration c : constructors) {
                if (name.equals(c.getName())) {
                    found = true;
                    if (this.parameters != null) {
                        for (Expression exp : this.parameters) {
                            _result = _result && exp.fullResolve(_scope);
                        }
                    }
                }
            }
            if (found) {
                return _result;
            } else {
                Logger.error(
                        "No constructor of the class" + this.element.toString() + " with such parameters was found !");
                return false;
            }
        } else {
            Logger.error("The class identifier " + this.element.toString() + " is not defined.");
            return false;
        }
    }

    @Override
    public Type getType() {
        return this.element;
    }

    @Override
    public Fragment getCode(TAMFactory _factory) {
        Fragment ret = _factory.createFragment();
        String name = this.element.toString();
        String paramsString = "_constructor";
        if (this.parameters != null) {
            for (Expression parameterDeclaration : this.parameters) {
                paramsString += "_" + parameterDeclaration.getType().toString();
            }
        }
        ret.add(_factory.createLoadL(1));
        if (this.parameters != null) {
            for (Expression _parameter : this.parameters) {
                ret.append(_parameter.getCode(_factory));
            }
        }
        ret.add(_factory.createCall("BEGIN:" + name + paramsString, Register.SB));
        return ret;
    }

}
