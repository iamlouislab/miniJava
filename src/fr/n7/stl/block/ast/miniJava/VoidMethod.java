package fr.n7.stl.block.ast.miniJava;

import java.util.Iterator;
import java.util.List;

import fr.n7.stl.block.ast.SemanticsUndefinedException;
import fr.n7.stl.block.ast.expression.Expression;
import fr.n7.stl.block.ast.instruction.Instruction;
import fr.n7.stl.block.ast.instruction.declaration.ParameterDeclaration;
import fr.n7.stl.block.ast.instruction.declaration.VariableDeclaration;
import fr.n7.stl.block.ast.scope.Declaration;
import fr.n7.stl.block.ast.scope.HierarchicalScope;
import fr.n7.stl.block.ast.scope.SymbolTable;
import fr.n7.stl.block.ast.type.AtomicType;
import fr.n7.stl.block.ast.type.Type;
import fr.n7.stl.tam.ast.Fragment;
import fr.n7.stl.tam.ast.Register;
import fr.n7.stl.tam.ast.TAMFactory;
import fr.n7.stl.util.Logger;

/**
 * Implementation of the Abstract Syntax Tree node for a method call expression
 * where the method result is discarded.
 * in the MiniJava language.
 *
 */
public class VoidMethod implements Instruction {

    protected Expression callerObject;
    protected String name;
    protected List<Expression> parameters;
    protected MethodDeclaration method;

    public VoidMethod(Expression _callerObject, String _name, List<Expression> _parameters) {
        this.callerObject = _callerObject;
        this.name = _name;
        this.parameters = _parameters;
    }

    @Override
    public String toString() {
        String ret = this.callerObject + "(";
        if (parameters != null) {
            Iterator<Expression> _iter = this.parameters.iterator();
            if (_iter.hasNext()) {
                ret += _iter.next();
                while (_iter.hasNext()) {
                    ret += " ," + _iter.next();
                }
            }
        }
        return ret + ")";

    }

    public Expression getCallerObject() {
        return this.callerObject;
    }

    public String getName() {
        return this.name;
    }

    public List<Expression> getParameters() {
        return this.parameters;
    }

    @Override
    public boolean collectAndBackwardResolve(HierarchicalScope<Declaration> _scope) {
        boolean ret = true;
        String id = name;

        ((SymbolTable) _scope).displayEntries();

        Logger.warning("replaced : start" + this.callerObject.toString().replaceAll(" ", "") + "end");
        String nameTofind = this.callerObject.toString().replaceAll(" ", "");
        Logger.warning("nameTofind : " + nameTofind);
        Logger.warning("isEqual : " + nameTofind.equals("a1"));

        if (this.parameters != null) {
            for (Expression p : this.parameters) {
                id += p.getType().toString();
            }
        }
        if (((SymbolTable) _scope).knows(nameTofind)) {
            Declaration _declaration = ((SymbolTable) _scope).get(this.callerObject.toString());
            if (_declaration instanceof VariableDeclaration) {
                VariableDeclaration declaration = ((VariableDeclaration) _declaration);
                Type _type = declaration.getType();
                if (_type instanceof Instance) {
                    Declaration d = ((SymbolTable) _scope).get(_type.toString());
                    if (d instanceof ClassDeclaration) {
                        List<MethodDeclaration> methods = ((ClassDeclaration) d).getClassMethods();
                        boolean found = false;
                        for (MethodDeclaration m : methods) {
                            if (id.equals(m.getName())) {
                                if (m.getAccessRight().equals(AccessRight.Private)) {
                                    Logger.error("The method " + name + " is private. It can't be called !");
                                    return false;
                                } else {
                                    found = true;
                                    this.method = m;
                                    if (this.parameters != null) {
                                        for (Expression exp : this.parameters) {
                                            ret = ret && exp.collectAndBackwardResolve(_scope);
                                        }
                                    }
                                }
                            }
                        }
                        if (found) {
                            return ret;
                        } else {
                            Logger.error("No method of the class " + ((ClassDeclaration) d).getName()
                                    + " with such parameters was found !");
                            return false;
                        }
                    } else {
                        Logger.error("The declaration for " + this.callerObject + " doesn't exist.");
                        return false;
                    }
                } else {
                    Logger.error("The identifier " + this.callerObject + " has not been found.");
                    return false;
                }
            } else {
                Logger.error("The identifier " + this.callerObject + " has not been found.");
                return false;
            }
        } else {
            Logger.error("The identifier " + this.callerObject + " has not been found.");
            return false;
        }
    }

    @Override
    public boolean fullResolve(HierarchicalScope<Declaration> _scope) {
        boolean ret = true;
        String id = name;
        if (this.parameters != null) {
            for (Expression p : this.parameters) {
                id += p.getType().toString();
            }
        }
        Logger.warning("current scope: " + _scope);
        if (((HierarchicalScope<Declaration>) _scope).knows(this.callerObject.toString())) {
            Declaration _declaration = _scope.get(this.callerObject.toString());
            if (_declaration instanceof VariableDeclaration) {
                VariableDeclaration declaration = ((VariableDeclaration) _declaration);
                Type _type = declaration.getType();
                if (_type instanceof Instance) {
                    Declaration d = _scope.get(_type.toString());
                    if (d instanceof ClassDeclaration) {
                        List<MethodDeclaration> methods = ((ClassDeclaration) d).getClassMethods();
                        boolean found = false;
                        for (MethodDeclaration m : methods) {
                            if (id.equals(m.getName())) {
                                if (m.getAccessRight().equals(AccessRight.Private)) {
                                    Logger.error("The method " + name + " is private. It can't be called !");
                                    return false;
                                } else {
                                    found = true;
                                    if (this.parameters != null) {
                                        for (Expression exp : this.parameters) {
                                            ret = ret && exp.fullResolve(_scope);
                                        }
                                    }
                                }
                            }
                        }
                        if (found) {
                            return ret;
                        } else {
                            Logger.error("No method of the class " + ((ClassDeclaration) d).getName()
                                    + " with such parameters was found !");
                            return false;
                        }
                    } else {
                        Logger.error("The declaration for " + this.callerObject + " doesn't exist.");
                        return false;
                    }
                } else {
                    Logger.error("The identifier " + this.callerObject + " has not been found.");
                    return false;
                }
            } else {
                Logger.error("The identifier " + this.callerObject + " has not been found.");
                return false;
            }
        } else {
            Logger.error("The identifier " + this.callerObject + " has not been found.");
            return false;
        }
    }

    @Override
    public Fragment getCode(TAMFactory _factory) {
        String paramsString = "_method";
        if (this.parameters != null) {
            for (Expression parameterDeclaration : this.parameters) {
                paramsString += "_" + parameterDeclaration.getType().toString();
            }
        }
        Fragment ret = _factory.createFragment();
        if (this.parameters != null) {
            for (Expression parameterDeclaration : this.parameters) {
                paramsString += "_" + parameterDeclaration.getType().toString();
            }
        }
        ret.add(_factory.createCall("BEGIN:" + this.getName() + paramsString, Register.SB));
        return ret;
    }

    @Override
    public boolean checkType() {
        return true;
    }

    @Override
    public int allocateMemory(Register _register, int _offset) {
        throw new SemanticsUndefinedException("allocateMemory undefined");
    }

}
