package at.zaboing.patcher;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class URLUtils {
	public static InputStream getStream(URL url) {
		try {
			return url.openStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static InputStream getStream(String remote, String file) {

		URL url;
		try {
			url = new URL(remote + "\\"
					+ file.replace("%", "%25").replace(" ", "%20"));
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		}

		return getStream(url);
	}
}
