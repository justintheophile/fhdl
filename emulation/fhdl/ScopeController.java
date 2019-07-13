package emulation.fhdl;

import java.util.ArrayList;
import java.util.HashMap;

import emulation.Variable;

public class ScopeController {

	private HashMap<String, Variable> variables = new HashMap<String, Variable>();
	private ArrayList<String> scopeStack = new ArrayList<String>();
	private int scopes = 0;

	public ScopeController() {
		enterScope("root");

	}

	public void enterScope(String name) {
		scopeStack.add(name);
		scopes++;
	}

	public void enterScope() {
		enterScope("#"+scopes);
	}

	
	public void exitScope() {
		scopeStack.remove(scopeStack.size() - 1);
	}
	
	public String getTopScope() {
		return scopeStack.get(scopeStack.size() - 1);
	}

	public String getScope(int negativeLevels) {
		String scope = "";
		for (int i = 0; i < scopeStack.size() - negativeLevels; i++) {
			scope += scopeStack.get(i) + ".";
		}
		return scope;
	}

	public String getScope() {
		return getScope(0);
	}
	
	public void createVariable(String name, Variable bus) {
		String scope = getScope();
		variables.put(scope+name, bus);
	}
	
	public Variable getVariable(String name) {
		for(int i=0; i < scopeStack.size(); i++) {
			String variable = getScope(i)+name;
			if(variables.containsKey(variable)) {
				return variables.get(variable);
			}
		}
		// if here... varaible does not exist in visible scope
		return null;
	}
	
	
	public void setVariable(String name, Variable bus) {
		Variable b = variables.get(getScope()+name);
		b.set(bus);
	}
	
	public String toString() {
		return variables.toString();
	}
	
}
