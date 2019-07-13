package emulation;

import emulation.fhdl.Bus;
import emulation.fhdl.MathEngine;
import emulation.fhdl.ScopeController;
import emulation.fhdl.Script;

public class Main {

	public static void main(String[] args) {

		ScopeController scope = new ScopeController();
		MathEngine engine = new MathEngine(scope);
		Script script = new Script(scope, engine);
		script.runFile("C:\\Users\\hunte\\OneDrive\\DDD\\emulation\\src\\scripts\\fullAdder.fhdl");
		//console.log(1, scope);
	}

	
}
