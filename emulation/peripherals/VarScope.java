package emulation.peripherals;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import emulation.console;
import emulation.fhdl.Script;

public class VarScope extends Peripheral {
	
	public VarScope(Script script) {
		super(script, 50);
	}

	public void update() {
		console.log(1, script.getScopeController().getVariable("add.sum_"));
	}

}
