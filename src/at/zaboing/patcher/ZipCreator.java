package at.zaboing.patcher;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipOutputStream;

import at.zaboing.patcher.manifest.PatchManifest;

public class ZipCreator
{

	public static void createFromManifest(PatchManifest manifest, String rootDir)
	{
		for (PatchElement element : manifest.elements)
		{
			try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(PatchElement.escapeString(element.getName()) + ".zip")))
			{
				try (ZipOutputStream zipStream = new ZipOutputStream(bos))
				{
					element.zip(zipStream, rootDir);
				}
			} catch (FileNotFoundException e)
			{
				e.printStackTrace();
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
}
