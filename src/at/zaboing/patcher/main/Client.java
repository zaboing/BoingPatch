package at.zaboing.patcher.main;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Set;

import at.zaboing.patcher.HashRegistry;
import at.zaboing.patcher.PatchApplier;
import at.zaboing.patcher.PatchElement;
import at.zaboing.patcher.manifest.ManifestReader;
import at.zaboing.patcher.manifest.PatchManifest;

public class Client {
	public static void main(String... args) {
		if (args.length < 2) {
			System.err
					.println("Usage: java -jar BoingPatch.jar <dir> <remote>");
			return;
		}
		String dir = args[0];
		String remote = args[1];
		PatchManifest manifest = new ManifestReader(fetch(remote,
				"manifest.pmf")).read();
		HashRegistry remoteRegistry = HashRegistry.createFromStream(fetch(
				remote, "hashes"));
		HashRegistry localRegistry = HashRegistry.createFromManifest(manifest, dir);
		Set<String> differences = remoteRegistry.getDifferences(localRegistry);
		for (String difference : differences) {
			PatchApplier.applyZip(
					fetch(remote, PatchElement.escapeString(difference)
							+ ".zip"), dir);
		}
	}

	private static InputStream fetch(String remote, String file) {
		try {
			URL url = new URL(remote + "\\"
					+ file.replace("%", "%25").replace(" ", "%20"));
			return url.openStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
