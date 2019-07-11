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
	
	
	public void set(String s) {
		char[] array =  s.toCharArray();
		for(int i=0; i < array.length; i++) {
			bits[array.length - 1- i].setValue(array[i]);
		}
	}
	public void set(int v) {
		String s = Integer.toBinaryString(v);
		set(s);
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
	
}