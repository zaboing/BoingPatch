package at.zaboing.patcher.manifest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import at.zaboing.patcher.PatchGroup;
import at.zaboing.patcher.PatchSingle;

public class ManifestReader {

	private BufferedReader reader;

	public ManifestReader(File file) throws FileNotFoundException {
		this(new FileInputStream(file));
	}

	public ManifestReader(String path) throws FileNotFoundException {
		this(new File(path));
	}

	public ManifestReader(InputStream inputStream) {
		this(new InputStreamReader(inputStream));
	}

	public ManifestReader(Reader reader) {
		this.reader = new BufferedReader(reader);
	}

	public PatchManifest read() {
		PatchManifest manifest = new PatchManifest();

		int lineNumber = 0;
		String line;
		try {
			PatchGroup lastGroup = null;
			while ((line = reader.readLine()) != null) {
				line = line.trim();
				lineNumber++;
				if (!line.contains(" ")) {
					System.err.println("Ignoring line " + lineNumber);
					continue;
				}
				String command = line.substring(0, line.indexOf(' '));
				String param = "test_area/"
						+ line.substring(line.indexOf(' ') + 1);

				switch (command.toLowerCase()) {
				case "group":
					PatchGroup group = new PatchGroup(param);
					manifest.elements.add(group);
					lastGroup = group;
					break;
				case "except":
					if (lastGroup != null) {
						lastGroup.except(param);
					} else {
						System.err.println("[" + lineNumber
								+ "] : Create group first");
					}
					break;
				case "single":
					lastGroup = null;
					PatchSingle single = new PatchSingle(param);
					manifest.elements.add(single);
					break;
				default:
					System.err.println(command.toLowerCase().trim());
					System.err.println("Ignoring line " + lineNumber);
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return manifest;
	}

	public static void main(String[] args) {
		try {
			ManifestReader reader = new ManifestReader("manifest.pmf");
			PatchManifest manifest = reader.read();
			manifest.elements.forEach(element -> System.out.println(element.toString()));
			System.out.println(manifest.equals(manifest));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}