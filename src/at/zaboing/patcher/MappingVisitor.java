package at.zaboing.patcher;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Set;

public class MappingVisitor implements FileVisitor<Path>
{
	public final Path rootDir;
	public final Set<String> files;
	public final PatchGroup group;

	public MappingVisitor(PatchGroup group, Set<String> files, String rootDir)
	{
		this.rootDir = Paths.get(rootDir).normalize();
		this.files = files;
		this.group = group;
	}

	@Override
	public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException
	{
		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException
	{
		String path = rootDir.relativize(dir).normalize().toString();
		if (group.shouldIgnore(path))
		{
			return FileVisitResult.SKIP_SUBTREE;
		}
		files.add(path);
		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException
	{
		String path = rootDir.relativize(file).normalize().toString();
		if (group.shouldIgnore(path))
		{
			return FileVisitResult.CONTINUE;
		}
		files.add(path);
		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException
	{
		return FileVisitResult.CONTINUE;
	}
}
