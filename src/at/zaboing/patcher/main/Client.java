package at.zaboing.patcher.main;

import java.util.Set;

import at.zaboing.patcher.HashRegistry;
import at.zaboing.patcher.URLUtils;
import at.zaboing.patcher.ZipExtractor;
import at.zaboing.patcher.manifest.ManifestReader;
import at.zaboing.patcher.manifest.PatchManifest;

public class Client {
	public static void main(String... args) {
		if (args.length < 2) {
			System.err.println("Usage: java -jar BoingPatch.jar <dir> <remote> [threaded] [num_threads]");
			return;
		}
		String dir = args[0];
		String remote = args[1];
		boolean threaded = false;
		int num_threads = 10;
		if (args.length >= 3) {
			threaded = args[2].equalsIgnoreCase("threaded");
		}
		if (args.length >= 4) {
			num_threads = Integer.parseInt(args[3]);
		}
		PatchManifest manifest = new ManifestReader(URLUtils.getStream(remote,
				"manifest.pmf")).read();
		HashRegistry remoteRegistry = HashRegistry.createFromStream(URLUtils
				.getStream(remote, "hashes"));
		HashRegistry localRegistry = HashRegistry.createFromManifest(manifest,
				dir);
		Set<String> differences = remoteRegistry.getDifferences(localRegistry);
		ZipExtractor extractor = new ZipExtractor(differences, dir, remote);
		extractor.threaded = threaded;
		extractor.threads = num_threads;
		extractor.run();
	}
}
