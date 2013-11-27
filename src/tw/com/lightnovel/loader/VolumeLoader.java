package tw.com.lightnovel.loader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.json.JSONException;

import tw.com.lightnovel.classes.Volume;
import tw.com.lightnovel.core.ApplicationContext;
import tw.com.lightnovel.logging.ILogger;

public class VolumeLoader {
	private ILogger mLogger = ApplicationContext.Logger
			.forType(VolumeLoader.class);
	
	private final String mUri;
	
	public VolumeLoader(String uri) {
		mUri = uri;
	}

	public Volume load() throws JSONException {
		String json = read(mUri);
		return Volume.fromJson(json);
	}

	private String read(String uri) {
		File file = new File(uri);
		BufferedReader reader = null;
		StringBuffer sb = new StringBuffer();

		try {
			reader = new BufferedReader(new InputStreamReader(
					new FileInputStream(file)));
			char[] buffer = new char[512];
			int len = reader.read(buffer);

			while (len > 0) {
				sb.append(buffer, 0, len);
				len = reader.read(buffer);
			}

		} catch (Exception e) {
			mLogger.error(e, "IOException");
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {

				}
			}
		}

		return sb.toString();
	}
}
