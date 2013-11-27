package tw.com.lightnovel.loader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import android.R.integer;

import tw.com.lightnovel.core.ApplicationContext;
import tw.com.lightnovel.logging.ILogger;

public class ChapterContentLoader {
	private ILogger mLogger = ApplicationContext.Logger
			.forType(ChapterContentLoader.class);
	private final String mUri;

	public ChapterContentLoader(String uri) {
		mUri = new File(uri, "content.data").getAbsolutePath();
	}

	public List<String> load() {
		String content = read();
		List<String> lines = new ArrayList<String>();
		Collections.addAll(lines, content.split("\n"));
		return lines;		
	}

	private String read() {
		BufferedReader reader = null;
		StringBuffer sb = new StringBuffer();

		try {
			reader = new BufferedReader(new InputStreamReader(
					new FileInputStream(mUri)));
			char[] buffer = new char[512];
			int len = reader.read(buffer);

			while (len > 0) {
				sb.append(buffer, 0, len);
				len = reader.read(buffer);
			}
		} catch (FileNotFoundException e) {
			mLogger.error(e, "FileNotFoundException");
		} catch (IOException e) {
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
