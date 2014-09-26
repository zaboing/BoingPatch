package at.zaboing.patcher;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;

import com.google.common.hash.Hasher;

public class HashingVisitor implements FileVisitor<Path> {

	private final Path rootDir;
	
	private final Hasher hasher = HashUtils.hasher();

	private final PatchGroup group;

	public HashingVisitor(PatchGroup group, String rootDir) {
		this.group = group;
		this.rootDir = Paths.get(rootDir);
	}

	@Override
	public FileVisitResult postVisitDirectory(Path arg0, IOException arg1)
			throws IOException {
		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult preVisitDirectory(Path arg0, BasicFileAttributes arg1)
			throws IOException {
		String path = rootDir.relativize(arg0).normalize().toString();
		if (group.shouldIgnore(path)) {
			return FileVisitResult.SKIP_SUBTREE;
		}
		hasher.putBytes(path.getBytes("UTF-8"));

		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult visitFile(Path arg0, BasicFileAttributes arg1)
			throws IOException {
		String path = rootDir.relativize(arg0).normalize().toString();
		if (group.shouldIgnore(path)) {
			return FileVisitResult.CONTINUE;
		}

		hasher.putBytes(path.getBytes("UTF-8"));
		hasher.putBytes(Files.readAllBytes(arg0));

		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult visitFileFailed(Path arg0, IOException arg1)
			throws IOException {
		return FileVisitResult.CONTINUE;
	}

	public byte[] getHash() {
		return hasher.hash().asBytes();
	}
}
