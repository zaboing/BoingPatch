package at.zaboing.patcher;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import at.zaboing.patcher.manifest.PatchManifest;

public class ZipExtractor implements Runnable
{

	private final Set<String> zips;
	private final String dir;
	private final String remote;

	public boolean threaded;
	public int threads;

	public int progress;

	public PatchManifest manifest;

	public Runnable progressUpdateCallback;
	public Runnable successCallback;
	public Runnable errorCallback;

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
			long time = System.currentTimeMillis();
			ExecutorService executor = Executors.newFixedThreadPool(threads);
			for (String zip : zips)
			{
				ExtractTask task = new ExtractTask(zip);
				task.successCallback = this::updateProgress;
				executor.submit(task);
			}
			try
			{
				executor.shutdown();
				boolean success = executor.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
				if (success)
				{
					System.out.println("Threaded: " + (System.currentTimeMillis() - time) + "ms");
					if (successCallback != null)
					{
						successCallback.run();
					}
				}
				else
				{
					if (errorCallback != null)
					{
						errorCallback.run();
					}
				}
			} catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
		else
		{
			long time = System.currentTimeMillis();
			for (String zip : zips)
			{
				applyZip(zip);
				updateProgress();
			}
			System.out.println("No threads: " + (System.currentTimeMillis() - time) + "ms");
			if (successCallback != null)
			{
				successCallback.run();
			}
		}
	}

	private void updateProgress()
	{
		progress++;
		if (progressUpdateCallback != null)
		{
			progressUpdateCallback.run();
		}
	}

	class ExtractTask implements Runnable
	{
		private final String zip;

		public Runnable successCallback;

		public ExtractTask(String zip)
		{
			this.zip = zip;
		}

		@Override
		public void run()
		{
			applyZip(zip);
			if (successCallback != null)
			{
				successCallback.run();
			}
		}
	}

	private void applyZip(String zip)
	{
		String escaped = PatchElement.escapeString(zip);
		InputStream stream = URLUtils.getStream(remote, escaped + ".zip");
		// manifest.elements.forEach(e -> System.out.println(e.getName()));
		PatchElement element = manifest.elements.stream().filter(e -> e.getName().equals(zip)).findFirst().get();
		File root = new File(dir);
		Set<String> present = element.getFiles(dir);
		present = present.stream().map(s -> Paths.get(s).normalize().toString()).collect(Collectors.toSet());
		Set<String> downloaded = new HashSet<>();
		try (ZipInputStream zipStream = new ZipInputStream(new BufferedInputStream(stream)))
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
					if (!entry.toString().equals("/"))
					{
						downloaded.add(Paths.get(entry.toString()).normalize().toString());
					}
					else
					{
						downloaded.add("");
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
		for (String file : present)
		{
			if (!downloaded.contains(file))
			{
				try
				{
					Files.deleteIfExists(new File(root, file).toPath());
				} catch (IOException e1)
				{
					e1.printStackTrace();
				}
			}
		}
	}
}
