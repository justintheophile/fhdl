package emulation.fhdl;

import emulation.Variable;

public class Bus extends Variable{
	Bit[] bits; //[width] downto [0]
	public Bus(int width, int val) {
		setWidth(width);
		set(val);
	}
	
	public Bus(int width) {
		this(width, 0);
	}
	
	public Bus(String value) {
		this(value.length(), 0);
		set(value);
	}
	
	public Bus(Bus b) {
		this(b.getWidth(), 0);
		set(b);
	}
	
	public Bus subBus(int high, int low) {
		Bus b = new Bus(high-low+1, 0);
		for(int i= low; i <= high; i++) {
			b.bits[i-low] = this.bits[i];
		}
		return b;
	}
	
	public void setWidth(int width) {
		bits = new Bit[width];

		for(int i=0; i < width; i++) {
			bits[i] = new Bit(0);
		}
	}
	
	public void set(String s) {
		// binary
		char[] array =  s.toCharArray();
		for(int i=0; i <bits.length; i++) {
			bits[i] = new Bit(0);
		}
		for(int i=0; i < array.length; i++) {
			bits[array.length - 1- i].setValue(array[i]);
		}
	}
	public void set(int v) {
		String s = Integer.toBinaryString((int) (Math.abs(v% Math.pow(2, bits.length)))) ;
		set(s);
	}
	
	public void set(boolean b) {
		set(b?"1" : "0");
	}
	
	public void set(Bus b) {
		set(b.toInt());
	}
	public boolean getBitValue(int index) {
		return bits[index].getBoolValue();
	}
	
	public String toString() {
		String temp = "";
		for(Bit b : bits) {
			temp = b.getValue() +temp;
		}
		return temp;
	}
	
	public int toInt() {
		return Integer.parseInt(toString(), 2);
	}
	
	
	public String toHex() {
		return Integer.toHexString(toInt());
	}
	

	
	public Bus or(Bus b) {		
		Bus newBus = new Bus(this.bits.length);
		for(int i=0; i< Math.min(b.getWidth(), this.getWidth()); i++) {
			newBus.bits[i] = b.bits[i].or(this.bits[i]);
		}

		return newBus;
	}

	public Bus and(Bus b) {
		
		Bus newBus = new Bus(this.bits.length);
		for(int i=0; i< Math.min(b.getWidth(), this.getWidth());i++) {
			newBus.bits[i] = b.bits[i].and(this.bits[i]);
		}

		return newBus;
	}
	
	public Bus xor(Bus b) {
		
		Bus newBus = new Bus(this.bits.length);
		for(int i=0; i< Math.min(b.getWidth(), this.getWidth()); i++) {
			newBus.bits[i] = b.bits[i].xor(this.bits[i]);
		}

		return newBus;
	}
	
	public Bus leftShift(Bus by) {
		Bus copy = new Bus(this);
		int val = copy.toInt();
		val = val << by.toInt();
		copy.set(val);
		return copy;
	}
	
	public Bus rightShift(Bus by) {
		Bus copy = new Bus(this);
		int val = copy.toInt();
		val = val >> by.toInt();
		copy.set(val);
		return copy;
	}
	
	
	public Bus not() {		
		Bus newBus = new Bus(this.bits.length);
		for(int i=0; i< this.bits.length; i++) {
			newBus.bits[i] = this.bits[i].not();
		}

		return newBus;
	}
	public Bus nand(Bus b) {
		return this.and(b).not();
	}
	
	public Bus twosCompliment() {
		return new Bus( this.bits.length, this.not().toInt() +1);
	}

	public int getWidth() {
		// TODO Auto-generated method stub
		return bits.length;
	}

	@Override
	public void set(Variable v) {
		if(v instanceof Bus)
			set(((Bus) v));
	}
}