package tw.com.lightnovel.loader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.json.JSONException;

import tw.com.lightnovel.classes.NovelIndex;
import tw.com.lightnovel.core.ApplicationContext;
import tw.com.lightnovel.logging.ILogger;

public class NovelLoader {
	private final String mNovelIndexId;
	private final File mBaseFolder;
	private final ILogger mLogger = ApplicationContext.Logger
			.forType(NovelLoader.class);

	public NovelLoader(String novelId) {
		mNovelIndexId = novelId;
		mBaseFolder = new File(
				ApplicationContext.getInstance().getBaseFolder(), mNovelIndexId);		
	}

	public NovelIndex loadNovelIndex() {
		File infoFile = new File(mBaseFolder, "info.data");
		BufferedReader reader = null;
		NovelIndex novelIndex = null;

		try {
			reader = new BufferedReader(new InputStreamReader(
					new FileInputStream(infoFile)));

			String json = read(reader);
			novelIndex = NovelIndex.fromJson(json);
		} catch (IOException e) {
			mLogger.error(e, "fail to load [" + infoFile.getAbsolutePath()
					+ "]");
		} catch (JSONException e) {
			mLogger.error(e, "fail to recover description json object");
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {

				}
			}
		}
		
		return novelIndex;
	}

	private String read(BufferedReader reader) throws IOException {
		char[] buffer = new char[512];
		StringBuilder sb = new StringBuilder();

		int len = reader.read(buffer);

		while (len > 0) {
			sb.append(buffer, 0, len);
			len = reader.read(buffer);
		}

		mLogger.log("info.data: " + sb.toString());

		return sb.toString();
	}
}
