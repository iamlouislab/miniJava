/**
 * 
 */
package fr.n7.stl.block.ast.scope;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import fr.n7.stl.block.ast.miniJava.ClassDeclaration;
import fr.n7.stl.block.ast.miniJava.MainDeclaration;
import fr.n7.stl.block.ast.miniJava.MethodDeclaration;

/**
 * Implementation of a hierarchical scope using maps.
 * 
 * @author Marc Pantel
 *
 */
public class SymbolTable implements HierarchicalScope<Declaration> {

	private Map<String, Declaration> declarations;
	private Scope<Declaration> context;
	private static List<ClassDeclaration> classDeclarations = new LinkedList<ClassDeclaration>();

	private static ClassDeclaration currentClassDeclaration;
	private static MethodDeclaration currentMethodDeclaration;
	private static MainDeclaration currentMainDeclaration;

	public static MainDeclaration getCurrentMainDeclaration() {
		return currentMainDeclaration;
	}

	public static void setCurrentMainDeclaration(MainDeclaration _currentMainDeclaration) {
		currentMainDeclaration = _currentMainDeclaration;
	}

	public static MethodDeclaration getCurrentMethodDeclaration() {
		return currentMethodDeclaration;
	}

	public static void setCurrentMethodDeclaration(MethodDeclaration _currentMethodDeclaration) {
		currentMethodDeclaration = _currentMethodDeclaration;
	}

	public static ClassDeclaration getCurrentClassDeclaration() {
		return currentClassDeclaration;
	}

	public static void setCurrentClassDeclaration(ClassDeclaration _currentClassDeclaration) {
		currentClassDeclaration = _currentClassDeclaration;
	}

	public static List<ClassDeclaration> getClassDeclarations() {
		return classDeclarations;
	}

	public static void addClassDeclaration(ClassDeclaration _classDeclaration) {
		classDeclarations.add(_classDeclaration);
	}

	public static List<ClassDeclaration> getClassDeclarationsList() {
		return classDeclarations;
	}

	public SymbolTable() {
		this(null);
	}

	public SymbolTable(Scope<Declaration> _context) {
		this.declarations = new HashMap<String, Declaration>();
		this.context = _context;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.n7.stl.block.ast.scope.Scope#get(java.lang.String)
	 */
	@Override
	public Declaration get(String _name) {
		if (this.declarations.containsKey(_name)) {
			return this.declarations.get(_name);
		} else {
			if (this.context != null) {
				return this.context.get(_name);
			} else {
				return null;
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.n7.stl.block.ast.scope.Scope#contains(java.lang.String)
	 */
	@Override
	public boolean contains(String _name) {
		return (this.declarations.containsKey(_name));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.n7.stl.block.ast.scope.Scope#accepts(fr.n7.stl.block.ast.scope.
	 * Declaration)
	 */
	@Override
	public boolean accepts(Declaration _declaration) {
		return (!this.contains(_declaration.getName()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.n7.stl.block.ast.scope.Scope#register(fr.n7.stl.block.ast.scope.
	 * Declaration)
	 */
	@Override
	public void register(Declaration _declaration) {
		if (this.accepts(_declaration)) {
			this.declarations.put(_declaration.getName(), _declaration);
		} else {
			throw new IllegalArgumentException();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.n7.stl.block.ast.scope.HierarchicalScope#knows(java.lang.String)
	 */
	@Override
	public boolean knows(String _name) {
		if (this.contains(_name)) {
			return true;
		} else {
			if (this.context != null) {
				if (this.context instanceof HierarchicalScope<?>) {
					return ((HierarchicalScope<?>) this.context).knows(_name);
				} else {
					return this.context.contains(_name);
				}
			} else {
				return false;
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String _local = "";
		if (this.context != null) {
			_local += "Hierarchical definitions :\n" + this.context.toString();
		}
		_local += "Local definitions : ";
		for (Entry<String, Declaration> _entry : this.declarations.entrySet()) {
			_local += _entry.getKey() + " -> " + _entry.getValue().toString() + "\n";
		}
		return _local;
	}


	public void displayEntries() {
		for (Entry<String, Declaration> _entry : this.declarations.entrySet()) {
			System.out.println(_entry.getKey() + " -> " + _entry.getValue().toString());
		}
	}

}
