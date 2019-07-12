package emulation.fhdl;

import java.util.ArrayList;
import java.util.HashMap;

import emulation.Bus;

public class ScopeController {

	private HashMap<String, Bus> variables = new HashMap<String, Bus>();
	private ArrayList<String> scopeStack = new ArrayList<String>();

	public ScopeController() {

	}

	public void enterScope(String name) {
		scopeStack.add(name);
	}

	public void exitScope() {
		scopeStack.remove(scopeStack.size() - 1);
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
	
	public void createVariable(String name, Bus bus) {
		String scope = getScope();
		variables.put(scope+name, bus);
	}
	
	public Bus getVariable(String name) {
		for(int i=0; i < scopeStack.size(); i++) {
			String variable = getScope(i)+name;
			if(variables.containsKey(variable)) {
				return variables.get(variable);
			}
		}
		// if here... varaible does not exist in visible scope
		return null;
	}
	
	
}
