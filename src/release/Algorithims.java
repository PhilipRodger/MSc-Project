package release;

import java.util.HashMap;

public enum Algorithims {
	Original, Modified, Diagonal;
	
	public static Algorithims stringToAlgorithim(String name) {
		if (name.equalsIgnoreCase("BPCS_Original") || name.equalsIgnoreCase("Original")) {
			return Algorithims.Original;
		} else if (name.equalsIgnoreCase("BPCS_Modified") || name.equalsIgnoreCase("Modified")) {
			return Algorithims.Modified;
		}  else if (name.equalsIgnoreCase("BPCS_Diagonal") || name.equalsIgnoreCase("Diagonal")) {
			return Algorithims.Diagonal;
		} else {
			return null;
		}
	}
	
	public static BPCS createAlgorithim(Algorithims algorithim, HashMap<String, String> params) {
		switch (algorithim) {
		case Original:
 			return new BPCS_Original(params);
		case Modified:
			return new BPCS_Modified(params);
		case Diagonal:
			return new BPCS_Diagonal(params);
		default:
			throw new IllegalArgumentException("Must specify what algorithim to use e.g.'algorithim=BPCS_Original'");
		}
	}
}
