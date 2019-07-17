package emulation.fhdl;

import java.util.HashMap;

public class Entity extends Variable{
	public int start, end;
	public String name;
	public String[] parameters;
	public String body = "";
	public Entity (String name, int index) {
		this.start = index;
	}
	
	public String toString() {
		return start +"";
	}
	public void set(Variable v) {
		// TODO Auto-generated method stub
	}


	public Object get() {
		return null;
	}

	public Object get(int index) {
		return null;
	}
}
