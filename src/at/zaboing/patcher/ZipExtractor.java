package at.zaboing.patcher;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipExtractor implements Runnable
{

	private final Set<String> zips;
	private final String dir;
	private final String remote;

	public boolean threaded;
	public int threads;

	public ZipExtractor(Set<String> zips, String dir, String remote)
	{
		this.zips = zips;
		this.dir = dir;
		this.remote = remote;
	}

	@Override
	public void run()
	{
		if (threaded)
		{
			ExecutorService executor = Executors.newFixedThreadPool(threads);
			for (String zip : zips)
			{
				String escaped = PatchElement.escapeString(zip);
				InputStream stream = URLUtils.getStream(remote, escaped + ".zip");
				ExtractTask task = new ExtractTask(stream);
				executor.submit(task);
			}
			try
			{
				executor.shutdown();
				boolean success = executor.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
				if (success)
				{
					System.out.println("Successfully installed patch");
				}
				else
				{
					System.out.println("Extracting took too long");
				}
			} catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
		else
		{
			for (String zip : zips)
			{
				applyZip(URLUtils.getStream(remote, PatchElement.escapeString(zip) + ".zip"));
			}
		}
	}

	class ExtractTask implements Runnable
	{
		private final InputStream inputStream;

		public ExtractTask(InputStream inputStream)
		{
			this.inputStream = inputStream;
		}

		@Override
		public void run()
		{
			applyZip(inputStream);
		}
	}

	private void applyZip(InputStream inputStream)
	{
		File root = new File(dir);
		try (ZipInputStream zipStream = new ZipInputStream(new BufferedInputStream(inputStream)))
		{
			byte[] buffer = new byte[2048];
			ZipEntry entry;
			try
			{
				while (Objects.nonNull(entry = zipStream.getNextEntry()))
				{
					if (entry.isDirectory())
					{
						new File(root, entry.getName()).mkdirs();
					}
					else
					{
						File file = new File(root, entry.getName());
						OutputStream outputStream = Files.newOutputStream(file.toPath());
						file.getParentFile().mkdirs();
						int len;
						while ((len = zipStream.read(buffer)) > 0)
						{
							outputStream.write(buffer, 0, len);
						}
						outputStream.close();
					}
				}
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
