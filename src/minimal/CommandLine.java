package minimal;

import java.util.HashMap;

public class CommandLine {
	public static void main(String[] args) {
		HashMap<String, String> settings = new HashMap<>();
		for (String string : args) {
			String[] keyValue = string.split("=", 2);
			if (keyValue.length == 2) {
				settings.put(keyValue[0].toLowerCase(), keyValue[1]);
			} else {
				// Check for some other meanings
				
				// Might be an algorithm or mode
				Algorithims possibleRef = Algorithims.stringToAlgorithim(keyValue[0]);
				if (possibleRef != null) {
					settings.put("algorithim", keyValue[0]);
					
				} else if (keyValue[0].equalsIgnoreCase("embed") || keyValue[0].equalsIgnoreCase("extract")
						|| keyValue[0].equalsIgnoreCase("roundtrip")) {
					settings.put("mode", keyValue[0]);
				}
			}
			
		}
		if (!settings.containsKey("algorithim")) {
			throw new IllegalArgumentException("Must specify what algorithim to use e.g.'BPCS_Original'");
		}
		Algorithims algorithim = Algorithims.stringToAlgorithim(settings.get("algorithim"));
		if (algorithim == null) {
			throw new IllegalArgumentException(
					"Algorithim name not recognised enter valid name e.g.'BPCS_Original'");
		}
		Algorithims.sendArgsToAlgorithim(algorithim, settings);
	}

	private static void printHelp() {
		System.out.println("Invalid input");
	}
}
