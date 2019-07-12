package emulation;

import java.util.HashMap;

public class EComponent {
	HashMap<String, Bus> buses = new HashMap<String, Bus>();
	public Bus getBus(String key) {
		return buses.get(key);
	}
}
