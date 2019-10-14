package fhdl.lang;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import fhdl.testing.console;

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
		enterScope("F." + scopes);
	}

	public void exitScope() {
		console.log(0,"exiting scope: "+ getScope());
		ArrayList<String> marked = new ArrayList<String>();
		Iterator<?> it = variables.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry) it.next();
			if(pair.getKey().toString().startsWith(getScope()) && !pair.getKey().toString().endsWith("_")) {
				marked.add(pair.getKey().toString());
			}
		}
		for(String key : marked) {
			if(key.contains("sum_"))
			console.log(1, "removing: " + key);
			variables.remove(key);
		}
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
		variables.put(scope + name, bus);
	}

	public Variable getVariable(String name) {
		for (int i = 0; i < scopeStack.size(); i++) {
			String variable = getScope(i) + name;
			if (variables.containsKey(variable)) {
				return variables.get(variable);
			}
		}
		// if here... varaible does not exist in visible scope
		return null;
	}

	public void setVariable(String name, Variable bus) {
		Variable b = variables.get(getScope() + name);
		b.set(bus);
	}

	public String toString() {
		return variables.toString();
	}
	
	public void dump() {
		console.log(1, this);
	}
}
