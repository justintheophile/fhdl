package emulation.peripherals;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import emulation.console;
import emulation.fhdl.Script;

public abstract class Peripheral {
	Script script;
	int delay;
	Thread process;

	public Peripheral(Script script, int delay) {
		this.script = script;
		this.delay = delay;
		process = new Thread(new Runnable() {

			public void run() {
				boolean run = true;
				while (run) {
					if(Thread.interrupted()) {
						break;
					}
					update();
					Script.milli(delay);
				}
			}

		});
	}

	public void on() {
		process.start();
	}

	public void off() {
		process.interrupt();
	}

	public abstract void update();

}
