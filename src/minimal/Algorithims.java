package minimal;

import java.util.HashMap;

public enum Algorithims {
	Original;
	
	public static Algorithims stringToAlgorithim(String name) {
		if (name.equalsIgnoreCase("BPCS_Original") || name.equalsIgnoreCase("Original")) {
			return Algorithims.Original;
		} else {
			return null;
		}
	}
	
	public static void sendArgsToAlgorithim(Algorithims algorithim, HashMap<String, String> params) {
		switch (algorithim) {
		case Original:
			BPCS_Original.runFromParams(params);
			break;

		default:
			throw new IllegalArgumentException("Must specify what algorithim to use e.g.'algorithim=BPCS_Original'");
		}
	}
}
