package at.zaboing.patcher;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class PatchGroup extends PatchElement {

	private Path path;
	private final List<Path> exceptions = new ArrayList<Path>();

	public PatchGroup(String path) {
		this.path = Paths.get(path);
	}

	public String getName() {
		StringBuilder stringBuilder = new StringBuilder(path.toString());
		exceptions.forEach(path -> {
			stringBuilder.append(" -");
			stringBuilder.append(path.toString());
		});
		return stringBuilder.toString();
	}

	public String getHash() {
		HashingVisitor visitor = new HashingVisitor(exceptions);
		try {
			Files.walkFileTree(path, visitor);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return HashUtils.toHexString(visitor.getHash());
	}

	public PatchGroup except(String exception) {
		exceptions.add(Paths.get(exception));
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
		for (Path exception : exceptions) {
			boolean match = false;
			for (Path exception2 : group.exceptions) {
				if (exception.equals(exception2)) {
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
}
