package at.zaboing.patcher;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PatchSingle extends PatchElement {

	private Path path;

	public PatchSingle(String path) {
		this.path = Paths.get(path);
	}
	
	public boolean equals(Object o) {
		if (o instanceof PatchSingle) {
			return equals((PatchSingle) o);
		}
		return super.equals(o);
	}
	
	public boolean equals(PatchSingle single) {
		if (single == null) {
			return false;
		}
		if (super.equals(single)) {
			return true;
		}
		return single.path.equals(path);
	}

	public String getName() {
		return path.toString();
	}

	public String getHash() {
		byte[] hash;

		try {
			byte[] content = Files.readAllBytes(path);

			hash = HashUtils.hash(content);
		} catch (IOException e) {
			e.printStackTrace();
			return "ERROR";
		}

		return HashUtils.toHexString(hash);
	}

}
