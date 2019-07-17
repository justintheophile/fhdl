package emulation.peripherals;

import emulation.fhdl.Script;

public abstract class Peripheral {
	Script script;
	public Peripheral(Script script) {
		this.script = script;
	}
	
	public abstract void on();
	public abstract void off();
	public abstract void update();

}
