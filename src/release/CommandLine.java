package release;

import java.util.ArrayList;
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
			showUsage();

			throw new IllegalArgumentException("Must specify what algorithim to use e.g.'BPCS_Original'");
		}
		Algorithims algorithim = Algorithims.stringToAlgorithim(settings.get("algorithim"));
		if (algorithim == null) {
			showUsage();
			throw new IllegalArgumentException(
					"Algorithim name not recognised enter valid name e.g.'BPCS_Original'");
		}
		runFromParams(algorithim, settings);
	}
	
	public static void runFromParams(Algorithims algorithimType, HashMap<String, String> params) {		
		//Defaults
		String stegKey = BPCS.defaultStegKey; 
		
		if (params.containsKey("key")) {
			stegKey = params.get("key");
		}
		
		BPCS bpcs = Algorithims.createAlgorithim(algorithimType, params);
		
		
		if (!params.containsKey("mode")) {
			showUsage();
			throw new IllegalArgumentException("Must specify the mode of use e.g. 'mode=embed', 'mode=extract', or 'mode=roundtrip'");
		}
		String mode = params.get("mode");
		switch (mode) {
		case "embed":
			if (!params.containsKey("payload")) {
				showUsage();
				throw new IllegalArgumentException("Must specify the payload path you wish to embed using 'payload=[path]'");
			}
			String stegoPath = bpcs.embedFile(params.get("payload"), stegKey);
			System.out.println("Succesfully embedded, output: " + stegoPath);
			break;
		case "extract":
			if (!params.containsKey("payload")) {
				showUsage();
				throw new IllegalArgumentException("Must specify the payload path you wish to extract using 'payload=[path]'");
			}
			bpcs.extractFile(params.get("payload"), stegKey);
			System.out.println("Succesfully Extracted payload, output: " + params.get("payload"));
			break;
		case "roundtrip":
			if (!params.containsKey("payload")) {
				showUsage();
				throw new IllegalArgumentException("Must specify the payload path you wish to embed using 'payload=[path]'");
			}
			
			// Embed Payload Step
			stegoPath = bpcs.embedFile(params.get("payload"), stegKey);
			params.put("vessel", stegoPath);
			System.out.println("Succesfully Embedded, output: " + stegoPath);
			
			// Clear so it can't cheat
			bpcs = null;

			// Extract Payload Step
			bpcs = Algorithims.createAlgorithim(algorithimType, params);
			String extractedPath = "extracted" + params.get("payload");
			bpcs.extractFile(extractedPath, stegKey);
			System.out.println("Succesfully Extracted, output: " + extractedPath);
			break;
			
		default:
			showUsage();
			throw new IllegalArgumentException("Must specify a valid mode of use e.g.embed, extract, or roundtrip");
		}
	}
	public static void showUsage() {
		System.out.println("Usage:\r\n" + 
				"	java -jar BPCS.jar <algorithim> mode=<mode> vessel=<vessel_path> payload=<payload_path>\r\n" + 
				"<algorithim> = “original” / “modified” / “diagonal”\r\n" + 
				"<mode> = “embed” / “extract” / “roundtrip”\r\n" + 
				"<payload_path> = payload to embed or extracted payload will be saved.\r\n" +
				"\n"+
				"Optionals:\r\n" + 
				"	threshold=<double> segmentwidth=<pixels> segmentheight=<pixels> key=<password>\r\n" + 
				"");
	}
}
