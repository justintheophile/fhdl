package emulation.fhdl;

public class Bit {
	private boolean value;

	public Bit(int val) {
		setValue(val);
	}

	public Bit(boolean b) {
		setValue(b ? 1 : 0);
	}

	public boolean getBoolValue() {
		return value;
	}

	public int getValue() {
		return value ? 1 : 0;
	}

	public void setValue(boolean b) {
		value = b;
	}

	public void setValue(int v) {
		setValue(v != 0);
	}

	public void setValue(char b) {
		setValue(b != '0' ? 1 : 0);
	}

	public Bit or(Bit b) {
		return new Bit(b.getBoolValue() | this.getBoolValue());
	}

	public Bit and(Bit b) {
		return new Bit(b.getBoolValue() & this.getBoolValue());
	}

	public Bit xor(Bit b) {
		return new Bit(b.getBoolValue() ^ this.getBoolValue());
	}
	
	public Bit nand(Bit b) {
		return and(b).not();
	}
	
	public Bit not() {
		return new Bit(!this.getBoolValue());
	}

	
	
	
}