package emulation.fhdl;

import java.util.ArrayList;

public class Mem extends Variable {
	public ArrayList<Bus> list = new ArrayList<Bus>();
	int width;

	public Mem(int width) {
		this.width = width;
	}

	public void add(Bus b) {
		Bus temp = new Bus(width, 0);
		temp.set(b); // to comply with width constraints
		list.add(b);
	}

	public void set(Variable v) {
		list = ((Mem)v).list; 

	}
	
	public void set(Mem m) {
		list.clear();
		for(Bus b : m.list) {
			add(new Bus(b));
		}
	}

	@Override
	public Object get() {
		// TODO Auto-generated method stub
		return list.size() + "";
	}

	@Override
	public Object get(int index) {
		// TODO Auto-generated method stub
		return list.get(index);
	}
	
	public String toString() {
		return (String) get();
	}

}
