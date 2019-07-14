package emulation;

import emulation.fhdl.Script;
import emulation.peripherals.Peripheral;
import emulation.peripherals.VarScope;

public class Main {

	public static void main(String[] args) {

		Script script = new Script();
		Peripheral scope = new VarScope(script);
		scope.on();
		script.runFile("C:\\Users\\hunte\\OneDrive\\DDD\\emulation\\src\\scripts\\alu.fhdl");
		scope.off();
	}

	
}
