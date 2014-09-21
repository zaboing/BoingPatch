package at.zaboing.patcher;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;

import com.google.common.hash.Hasher;

public class HashingVisitor implements FileVisitor<Path> {

	private final Hasher hasher = HashUtils.hasher();

	private final List<Path> ignoreList;

	public HashingVisitor(List<Path> ignoreList) {
		this.ignoreList = ignoreList;
	}

	@Override
	public FileVisitResult postVisitDirectory(Path arg0, IOException arg1)
			throws IOException {
		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult preVisitDirectory(Path arg0, BasicFileAttributes arg1)
			throws IOException {
		for (Path path : ignoreList) {
			if (arg0.equals(path)) {
				return FileVisitResult.SKIP_SUBTREE;
			}
		}
		hasher.putBytes(arg0.toString().getBytes("UTF-8"));

		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult visitFile(Path arg0, BasicFileAttributes arg1)
			throws IOException {
		for (Path path : ignoreList) {
			if (arg0.equals(path)) {
				return FileVisitResult.CONTINUE;
			}
		}

		hasher.putBytes(arg0.toString().getBytes("UTF-8"));
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
