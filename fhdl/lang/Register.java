package fhdl.lang;

public class Register extends Variable {
	Bit[] bits; // [width] downto [0]

	public Register(int width, int val) {
		setWidth(width);
		set(val);
	}

	public Register(int width) {
		this(width, 0);
	}

	public Register(String value) {
		this(value.length(), 0);
		set(value);
	}

	public Register(Register b) {
		this(b.getWidth(), 0);
		set(b);
	}

	public Register subBus(int high, int low) {
		Register b = new Register(high - low + 1, 0);
		for (int i = low; i <= high; i++) {
			b.bits[i - low] = this.bits[i];
		}
		return b;
	}

	public void setWidth(int width) {
		bits = new Bit[width];

		for (int i = 0; i < width; i++) {
			bits[i] = new Bit(0);
		}
	}

	public void set(String s) {
		// binary
		//System.out.println(s.length() + " > " + bits.length);
		char[] array = s.toCharArray();
		for (int i = 0; i < bits.length; i++) {
			bits[i] = new Bit(0);
		}
		for (int i = 0; i < array.length; i++) {
			int index = array.length - 1 - i;
			if (index >= 0 && index < bits.length) {
				bits[array.length - 1 - i].setValue(array[i]);
			}
		}
	}

	public void set(int v) {
		set(Integer.toBinaryString((int) (Math.abs(v % Math.pow(2, bits.length)))));
	}

	public void set(boolean b) {
		set(b ? "1" : "0");
	}

	public void set(Register b) {
		set(b.toInt());
	}

	public boolean getBitValue(int index) {
		return bits[index].getBoolValue();
	}

	public String toString() {
		String temp = "";
		for (Bit b : bits) {
			temp = b.getValue() + temp;
		}
		return temp;
	}

	public int toInt() {
		return Integer.parseInt(toString(), 2);
	}

	public String toHex() {
		return Integer.toHexString(toInt());
	}

	public Register or(Register b) {
		Register newBus = new Register(this.bits.length);
		for (int i = 0; i < Math.min(b.getWidth(), this.getWidth()); i++) {
			newBus.bits[i] = b.bits[i].or(this.bits[i]);
		}

		return newBus;
	}

	public Register and(Register b) {

		Register newBus = new Register(this.bits.length);
		for (int i = 0; i < Math.min(b.getWidth(), this.getWidth()); i++) {
			newBus.bits[i] = b.bits[i].and(this.bits[i]);
		}

		return newBus;
	}

	public Register xor(Register b) {

		Register newBus = new Register(this.bits.length);
		for (int i = 0; i < Math.min(b.getWidth(), this.getWidth()); i++) {
			newBus.bits[i] = b.bits[i].xor(this.bits[i]);
		}

		return newBus;
	}

	public Register leftShift(Register by) {
		Register copy = new Register(this);
		int val = copy.toInt();
		val = val << by.toInt();
		copy.set(val);
		return copy;
	}

	public Register rightShift(Register by) {
		Register copy = new Register(this);
		int val = copy.toInt();
		val = val >> by.toInt();
		copy.set(val);
		return copy;
	}

	public Register not() {
		Register newBus = new Register(this.bits.length);
		for (int i = 0; i < this.bits.length; i++) {
			newBus.bits[i] = this.bits[i].not();
		}

		return newBus;
	}

	public Register nand(Register b) {
		return this.and(b).not();
	}

	public Register twosCompliment() {
		return new Register(this.bits.length, this.not().toInt() + 1);
	}

	public int getWidth() {
		// TODO Auto-generated method stub
		return bits.length;
	}

	@Override
	public void set(Variable v) {
		if (v instanceof Register)
			set(((Register) v));
	}

	@Override
	public Object get() {

		return toString();
	}

	public Object get(int index) {
		return getBitValue(index) ? new Register(1, 1) : new Register(1, 0);
	}
}