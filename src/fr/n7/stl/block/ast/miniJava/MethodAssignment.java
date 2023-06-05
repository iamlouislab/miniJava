package fr.n7.stl.block.ast.miniJava;

import java.util.List;

import fr.n7.stl.block.ast.expression.Expression;
import fr.n7.stl.block.ast.expression.assignable.AssignableExpression;
import fr.n7.stl.block.ast.instruction.declaration.VariableDeclaration;
import fr.n7.stl.block.ast.scope.Declaration;
import fr.n7.stl.block.ast.scope.HierarchicalScope;
import fr.n7.stl.block.ast.type.AtomicType;
import fr.n7.stl.block.ast.type.Type;
import fr.n7.stl.tam.ast.Fragment;
import fr.n7.stl.tam.ast.TAMFactory;
import fr.n7.stl.util.Logger;

public class MethodAssignment implements AssignableExpression {

    protected Expression callerObject;
    protected String name;
    protected List<Expression> parameters;
    protected MethodDeclaration method;

    public MethodAssignment(Expression _callerObject, String _name, List<Expression> _parameters) {
        Logger.warning("MethodAssignment called with " + _callerObject + " " + _name + " " + _parameters);
        this.callerObject = _callerObject;
        this.name = _name;
        this.parameters = _parameters;
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
        boolean _result = true;
        String id = name;
        if (this.parameters != null) {
            for (Expression p : this.parameters) {
                id += p.getType().toString();
            }
        }
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
                                    this.method = m;
                                    if (this.parameters != null) {
                                        for (Expression exp : this.parameters) {
                                            _result = _result && exp.collectAndBackwardResolve(_scope);
                                        }
                                    }
                                    _result = _result && !m.getType().equals(AtomicType.VoidType);
                                }
                            }
                        }
                        if (found) {
                            return _result;
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
        boolean _result = true;
        String id = name;
        if (this.parameters != null) {
            for (Expression p : this.parameters) {
                id += p.getType().toString();
            }
        }
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
                                            _result = _result && exp.fullResolve(_scope);
                                        }
                                    }
                                    _result = _result && !m.getType().equals(AtomicType.VoidType);
                                }
                            }
                        }
                        if (found) {
                            return _result;
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
    public Type getType() {
        return this.method.getType();
    }

    @Override
    public Fragment getCode(TAMFactory _factory) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getCode'");
    }

}
