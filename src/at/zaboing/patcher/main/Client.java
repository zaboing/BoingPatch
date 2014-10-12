package at.zaboing.patcher.main;

import java.util.Set;

import at.zaboing.patcher.HashRegistry;
import at.zaboing.patcher.URLUtils;
import at.zaboing.patcher.ZipExtractor;
import at.zaboing.patcher.manifest.ManifestReader;
import at.zaboing.patcher.manifest.PatchManifest;

public class Client implements Runnable
{
	private String directory;
	private String remote;

	private boolean threaded;
	private int threadAmount;

	public String currentAction;
	public float maxProgress = Float.POSITIVE_INFINITY;
	public int progress;
	public Runnable progressUpdateCallback;
	public Runnable successCallback;

	public static void main(String... args)
	{
		if (args.length < 2)
		{
			System.err.println("Usage: java -jar BoingPatch.jar <dir> <remote> [threaded] [num_threads]");
			return;
		}
		String dir = args[0];
		String remote = args[1];
		boolean threaded = false;
		int threadAmount = 10;
		if (args.length >= 3)
		{
			try
			{
				threaded = args[2].equalsIgnoreCase("threaded");
			} catch (NumberFormatException e)
			{
				System.err.println("Invalid number format: " + args[2]);
				System.exit(1);
				return;
			}
		}
		if (args.length >= 4)
		{
			threadAmount = Integer.parseInt(args[3]);
		}
		Client client = new Client(dir, remote, threaded, threadAmount);
		client.run();
	}

	public Client(String directory, String remote, boolean threaded, int threadAmount)
	{
		this.directory = directory;
		this.remote = remote;
		this.threaded = threaded;
		this.threadAmount = threadAmount;
	}

	@Override
	public void run()
	{
		maxProgress = Float.POSITIVE_INFINITY;
		currentAction = "Fetching patch manifest";
		PatchManifest manifest = new ManifestReader(URLUtils.getStream(remote, "manifest.pmf")).read();
		progress();
		currentAction = "Fetching patch hashes";
		progress();
		HashRegistry remoteRegistry = HashRegistry.createFromStream(URLUtils.getStream(remote, "hashes"));
		currentAction = "Calculating local hashes";
		progress();
		HashRegistry localRegistry = HashRegistry.createFromManifest(manifest, directory);
		currentAction = "Calculating differences";
		Set<String> differences = remoteRegistry.getDifferences(localRegistry);
		progress();
		maxProgress = differences.size() + 4;
		currentAction = "Extracting zips...";
		ZipExtractor extractor = new ZipExtractor(differences, directory, remote);
		extractor.manifest = manifest;
		extractor.progressUpdateCallback = this::progress;
		extractor.threaded = threaded;
		extractor.threads = threadAmount;
		extractor.successCallback = successCallback;
		extractor.run();
	}

	private void progress()
	{
		progress++;
		if (progressUpdateCallback != null)
		{
			progressUpdateCallback.run();
		}
	}
}
