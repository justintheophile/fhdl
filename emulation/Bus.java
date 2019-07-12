package emulation;

public class Bus{
	Bit[] bits; //[width] downto [0]
	public Bus(int width, int val) {
		bits = new Bit[width];
		for(int i=0; i < width; i++) {
			bits[i] = new Bit(0);
		}
		set(val);
	}
	
	public Bus(int width) {
		this(width, 0);
	}
	
	public Bus(String value) {
		this(value.length(), 0);
		set(value);
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
		if(b.bits.length != this.bits.length) return null; // maybe throw something
		
		Bus newBus = new Bus(this.bits.length);
		for(int i=0; i< b.bits.length; i++) {
			newBus.bits[i] = b.bits[i].or(this.bits[i]);
		}

		return newBus;
	}

	public Bus and(Bus b) {
		if(b.bits.length != this.bits.length) return null; // maybe throw something
		
		Bus newBus = new Bus(this.bits.length);
		for(int i=0; i< b.bits.length; i++) {
			newBus.bits[i] = b.bits[i].and(this.bits[i]);
		}

		return newBus;
	}
	
	public Bus xor(Bus b) {
		if(b.bits.length != this.bits.length) return null; // maybe throw something
		
		Bus newBus = new Bus(this.bits.length);
		for(int i=0; i< b.bits.length; i++) {
			newBus.bits[i] = b.bits[i].xor(this.bits[i]);
		}

		return newBus;
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
}