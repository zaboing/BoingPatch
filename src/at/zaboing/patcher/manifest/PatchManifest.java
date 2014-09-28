package at.zaboing.patcher.manifest;

import java.util.ArrayList;
import java.util.List;

import at.zaboing.patcher.PatchElement;

public class PatchManifest
{
	public final List<PatchElement> elements = new ArrayList<PatchElement>();

	public boolean equals(Object o)
	{
		if (o == null || !(o instanceof PatchManifest))
		{
			return false;
		}
		// if (super.equals(o)) {
		// return true;
		// }
		PatchManifest manifest = (PatchManifest) o;
		if (elements.size() != manifest.elements.size())
		{
			return false;
		}
		for (PatchElement element : elements)
		{
			boolean match = false;

			for (PatchElement element2 : manifest.elements)
			{
				if (element.equals(element2))
				{
					match = true;
				}
			}

			if (!match)
			{
				return false;
			}
		}
		return true;
	}
}
