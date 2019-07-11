package emulation;

public class Bit{
	private boolean value;
	public Bit(int val) {
		setValue(val);
	}
	
	public int getValue() {
		return value ? 1 : 0;
	}
	public void setValue(int v) {
		value = v != 0;
	}
	
	public void setValue(char b) {
		setValue(b != '0' ? 1 : 0);
	}
}