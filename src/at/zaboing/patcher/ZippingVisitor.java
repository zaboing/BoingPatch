package at.zaboing.patcher;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZippingVisitor implements FileVisitor<Path>
{

	private final Path rootDir;
	private final PatchGroup group;
	private final ZipOutputStream zipStream;

	public ZippingVisitor(PatchGroup group, ZipOutputStream zipStream, String rootDir)
	{
		this.group = group;
		this.zipStream = zipStream;
		this.rootDir = Paths.get(rootDir);
	}

	public FileVisitResult postVisitDirectory(Path arg0, IOException arg1) throws IOException
	{
		return FileVisitResult.CONTINUE;
	}

	public FileVisitResult preVisitDirectory(Path arg0, BasicFileAttributes arg1) throws IOException
	{
		String path = rootDir.relativize(arg0).normalize().toString();
		if (group.shouldIgnore(path))
		{
			return FileVisitResult.SKIP_SUBTREE;
		}

		if (!path.endsWith("/"))
		{
			path += "/";
		}

		ZipEntry entry = new ZipEntry(path);
		zipStream.putNextEntry(entry);
		zipStream.closeEntry();

		return FileVisitResult.CONTINUE;
	}

	public FileVisitResult visitFile(Path arg0, BasicFileAttributes arg1) throws IOException
	{
		String path = rootDir.relativize(arg0).normalize().toString();
		if (group.shouldIgnore(path))
		{
			return FileVisitResult.CONTINUE;
		}

		ZipEntry entry = new ZipEntry(path);
		zipStream.putNextEntry(entry);
		zipStream.write(Files.readAllBytes(arg0));
		zipStream.closeEntry();

		return FileVisitResult.CONTINUE;
	}

	public FileVisitResult visitFileFailed(Path arg0, IOException arg1) throws IOException
	{
		return FileVisitResult.CONTINUE;
	}

}
