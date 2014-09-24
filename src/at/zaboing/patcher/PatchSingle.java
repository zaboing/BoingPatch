package at.zaboing.patcher;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class PatchSingle extends PatchElement {

	private String path;

	public PatchSingle(String path) {
		this.path = path;
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

	public String getHash(String rootDir) {
		byte[] hash;

		try {
			byte[] content = Files.readAllBytes(Paths.get(rootDir, path));

			hash = HashUtils.hash(content);
		} catch (IOException e) {
			e.printStackTrace();
			return "ERROR";
		}

		return HashUtils.toHexString(hash);
	}

	public void zip(ZipOutputStream zipStream, String rootDir) throws IOException {
		ZipEntry entry = new ZipEntry(path.toString());
		zipStream.putNextEntry(entry);
		zipStream.write(Files.readAllBytes(Paths.get(rootDir, path)));
		zipStream.closeEntry();
	}
}
