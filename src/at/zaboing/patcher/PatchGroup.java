package at.zaboing.patcher;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipOutputStream;

public class PatchGroup extends PatchElement {

	private String path;
	private final List<String> exceptions = new ArrayList<String>();

	public PatchGroup(String path) {
		this.path = path;
	}

	public String getName() {
		StringBuilder stringBuilder = new StringBuilder(path.toString());
		exceptions.forEach(path -> {
			stringBuilder.append(" -");
			stringBuilder.append(path.toString());
		});
		return stringBuilder.toString();
	}

	public String getHash(String rootDir) {
		HashingVisitor visitor = new HashingVisitor(this, rootDir);
		try {
			Files.walkFileTree(Paths.get(rootDir, path), visitor);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return HashUtils.toHexString(visitor.getHash());
	}

	public PatchGroup except(String exception) {
		exceptions.add(exception);
		return this;
	}

	public boolean equals(Object o) {
		if (o instanceof PatchGroup) {
			return equals((PatchGroup) o);
		}
		return super.equals(o);
	}
	
	public boolean equals(PatchGroup group) {
		if (super.equals(group)) {
			return true;
		}
		if (!group.path.equals(path)) {
			return false;
		}
		if (group.exceptions.size() != this.exceptions.size()) {
			return false;
		}
		for (String exception : exceptions) {
			boolean match = false;
			for (String exception2 : group.exceptions) {
				if (exception.equalsIgnoreCase(exception2)) {
					match = true;
					break;
				}
			}
			if (!match) {
				return false;
			}
		}
		return true;
	}
	
	public boolean shouldIgnore(String path) {
		return exceptions.contains(path);
	}

	public void zip(ZipOutputStream zipStream, String rootDir) throws IOException {
		ZippingVisitor visitor = new ZippingVisitor(this, zipStream, rootDir);
		Files.walkFileTree(Paths.get(rootDir, path), visitor);
	}
}
