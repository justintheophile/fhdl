package emulation.fhdl;

public abstract class Variable {
	public abstract void set(Variable v);
	public abstract Object get();
	public abstract Object get(int index);
}
