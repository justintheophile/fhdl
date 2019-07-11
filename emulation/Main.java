package emulation;

public class Main {

	public static void main(String[] args) {
		Bus b = new Bus(8);
		b.set("10000101");
		Logger.log(0, b.toInt());
	}

	
}
