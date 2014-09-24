package at.zaboing.patcher;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


public class PatchApplier {
	public static void applyZip(InputStream inputStream, String dir) {
		File root = new File(dir);
		try (ZipInputStream zipStream = new ZipInputStream(
				new BufferedInputStream(inputStream))) {

			byte[] buffer = new byte[2048];
			ZipEntry entry;
			try {
				while (Objects.nonNull(entry = zipStream.getNextEntry())) {
					if (entry.isDirectory()) {
						new File(root, entry.getName()).mkdirs();
					} else {
						File file = new File(root, entry.getName());
						OutputStream outputStream = Files.newOutputStream(file.toPath());
						file.getParentFile().mkdirs();
						int len;
						while ((len = zipStream.read(buffer)) > 0) {
							outputStream.write(buffer, 0, len);
						}
						outputStream.close();
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
