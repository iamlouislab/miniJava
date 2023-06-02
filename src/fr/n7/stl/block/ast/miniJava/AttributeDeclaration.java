package fr.n7.stl.block.ast.miniJava;

import fr.n7.stl.block.ast.SemanticsUndefinedException;
import fr.n7.stl.block.ast.expression.Expression;
import fr.n7.stl.block.ast.scope.Declaration;
import fr.n7.stl.block.ast.scope.HierarchicalScope;
import fr.n7.stl.block.ast.type.PartialType;
import fr.n7.stl.block.ast.type.Type;
import fr.n7.stl.tam.ast.Fragment;
import fr.n7.stl.tam.ast.Register;
import fr.n7.stl.tam.ast.TAMFactory;
import fr.n7.stl.util.Logger;
import fr.n7.stl.util.Pair;

public class AttributeDeclaration implements ClassElement {

    protected ElementNature nature;
    protected Type type;
    protected Pair<String, PartialType> identifiant;
    protected Expression valeur;
    protected AccessRight accessRight;

    Register register;

    int offset;

    public AttributeDeclaration(Type _type, Pair<String, PartialType> _identifiant) {
        this.nature = ElementNature.None;
        this.type = _type;
        this.identifiant = _identifiant;
    }

    public AttributeDeclaration(Type _type, Pair<String, PartialType> _identifiant, Expression _valeur,
            ElementNature _nature) {
        this.nature = _nature;
        this.type = _type;
        this.identifiant = _identifiant;
        this.valeur = _valeur;
    }

    public String toString() {
        String _result = this.nature.toString() + this.type.toString() + " "
                + this.identifiant.getLeft();
        if (this.valeur != null) {
            _result += " = " + this.valeur.toString();
        }
        return _result + ";\n";
    }

    public ElementNature getState() {
        return this.nature;
    }

    @Override
    public String getName() {
        return this.identifiant.getLeft();
    }

    @Override
    public Type getType() {
        return this.type;
    }

    @Override
    public boolean collectAndBackwardResolve(HierarchicalScope<Declaration> _scope) {
        Logger.warning("collecting for AttributeDeclaration");
        if (((HierarchicalScope<Declaration>) _scope).accepts(this)) {
            _scope.register(this);
            if (this.valeur != null) {
                return this.valeur.collectAndBackwardResolve(_scope);
            }
            return true;
        } else {
            Logger.error("The identifier " + this.identifiant.getLeft() + " is already defined.");
            return false;
        }
    }

    @Override
    public boolean fullResolve(HierarchicalScope<Declaration> _scope) {
        if (this.valeur != null) {
            return this.type.resolve(_scope) && this.valeur.fullResolve(_scope);
        }
        return this.type.resolve(_scope);
    }

    @Override
    public boolean checkType() {
        if (valeur != null) {
            if (type.compatibleWith(this.valeur.getType()) || this.valeur.getType().compatibleWith(type)) {
                return true;
            } else {
                Logger.error("The type of the attribute " + this.identifiant.getLeft() + " is incompatible.");
                return false;
            }
        } else {
            return true;
        }

    }

    @Override
    public int allocateMemory(Register _register, int _offset) {
        int _result;
        this.register = _register;
        this.offset = _offset;
        _result = this.type.length();
        return _result;
    }

    @Override
    public Fragment getCode(TAMFactory _factory) {
        Fragment _result = _factory.createFragment();
        if (this.valeur != null) {
            _result.append(this.valeur.getCode(_factory));
            _result.add(_factory.createStore(this.register, this.offset, this.type.length()));
        }
        return _result;
    }

    @Override
    public void setAccessRight(AccessRight _accessRight) {
        this.accessRight = _accessRight;
    }

    public AccessRight getAccessRight() {
        return this.accessRight;
    }

}
