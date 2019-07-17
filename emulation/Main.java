package emulation;

import emulation.fhdl.Script;
import emulation.peripherals.Peripheral;
import emulation.peripherals.VarScope;

public class Main {

	public static void main(String[] args) {

		Script script = new Script();
		
		Peripheral scope = (Peripheral) new VarScope(script, "count");
		scope.on();
		script.runFile("C:/Users/hunte/OneDrive/DDD/emulation/src/scripts/rom.fhdl");
//		scope.off();
	}

	
}
