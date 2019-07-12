package emulation;

import emulation.fhdl.MathEngine;
import emulation.fhdl.ScopeController;

public class Main {

	public static void main(String[] args) {

		ScopeController scope = new ScopeController();
		scope.enterScope("root");
		scope.createVariable("x", new Bus("0101"));
		MathEngine engine = new MathEngine(scope);
		
		
		console.log(engine.evaluate(8, "0b101001 | 0b1011"));
		
	}

	
}
