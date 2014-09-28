package at.zaboing.patcher.main;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;

import at.zaboing.patcher.ZipCreator;
import at.zaboing.patcher.manifest.ManifestReader;
import at.zaboing.patcher.manifest.PatchManifest;

public class Server
{
	public static void main(String... args)
	{
		if (args.length < 2)
		{
			System.err.println("Usage: java -jar BoingPatch.jar <manifest> <dir> [hash] [zip]");
			return;
		}
		String manifestPath = args[0];
		String dir = args[1];
		try
		{
			PatchManifest manifest = new ManifestReader(manifestPath).read();
			for (int i = 2; i < args.length; i++)
			{
				if (args[i].equals("hash"))
				{
					Files.write(Paths.get("hashes"), manifest.elements.stream().parallel().map(element -> element.getName() + " - " + element.getHash(dir)).collect(Collectors.toList()));
				}
				if (args[i].equals("zip"))
				{
					ZipCreator.createFromManifest(manifest, dir);
				}
			}
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
