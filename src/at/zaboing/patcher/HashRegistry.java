package at.zaboing.patcher;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import at.zaboing.patcher.manifest.PatchManifest;

public class HashRegistry {
	private final Map<String, String> hashes;

	private HashRegistry() {
		this.hashes = new HashMap<String, String>();
	}

	private HashRegistry(Map<String, String> hashes) {
		this.hashes = hashes;
	}

	public void clear() {
		hashes.clear();
	}

	public String getHash(String name) {
		return hashes.get(name);
	}

	public void register(String name, String hash) {
		hashes.put(name, hash);
	}

	public Set<String> getDifferences(HashRegistry registry) {
		Set<String> differences = new HashSet<String>();

		for (String name : hashes.keySet()) {
			String hash = String.valueOf(getHash(name));
			String hash2 = String.valueOf(registry.getHash(name));
			if (!hash.equals(hash2)) {
				differences.add(name);
			}
		}

		return differences;
	}

	public static HashRegistry createFromManifest(PatchManifest manifest, String rootDir) {
		HashRegistry registry = new HashRegistry();

		manifest.elements.forEach(element -> registry.register(
				element.getName(), element.getHash(rootDir)));

		return registry;
	}

	public static HashRegistry createFromStream(InputStream inputStream) {
		Reader reader = new InputStreamReader(inputStream);
		return createFromReader(reader);
	}

	public static HashRegistry createFromReader(Reader reader) {
		BufferedReader br = new BufferedReader(reader);
		String line;
		try {
			Map<String, String> registry = new HashMap<String, String>();
			while (Objects.nonNull(line = br.readLine())) {
				if (line.contains(" - ")) {
					String name = line.substring(0, line.indexOf(" - "));
					String hash = line.substring(line.indexOf(" - ") + 3);
					registry.put(name, hash);
				}
			}
			return new HashRegistry(registry);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static HashRegistry createFromFile(File file) {
		try (Reader reader = new BufferedReader(new FileReader(file))) {
			return createFromReader(reader);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static HashRegistry createFromFile(String filePath) {
		return createFromPath(Paths.get(filePath));
	}

	public static HashRegistry createFromPath(Path filePath) {
		try (Reader reader = Files.newBufferedReader(filePath)) {
			return createFromReader(reader);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static HashRegistry createFromString(String registry) {
		try (StringReader reader = new StringReader(registry)) {
			return createFromReader(reader);
		}
	}

	public static HashRegistry createFromURL(URL url) {
		try {
			URLConnection connection = url.openConnection();
			connection
					.setRequestProperty(
							"User-Agent",
							"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
			return createFromStream(connection.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}
}
