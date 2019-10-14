package fhdl.testing;

import fhdl.lang.Script;
import peripherals.Peripheral;
import peripherals.VarScope;

public class Main {

	public static void main(String[] args) {

		Script script = new Script();
		
		//Peripheral scope = (Peripheral) new VarScope(script, "count");
		//scope.on();
		script.runFile("./src/scripts/rom.fhdl");
		//scope.off();
	}

	
}
