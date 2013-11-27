package tw.com.lightnovel.loader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import tw.com.lightnovel.apis.JsonHelper;
import tw.com.lightnovel.classes.ListRowData;
import tw.com.lightnovel.core.ApplicationContext;
import tw.com.lightnovel.logging.ILogger;

public class LocalListLoader {
	private final ILogger mLogger = ApplicationContext.Logger
			.forType(LocalListLoader.class);
	private final File mBaseFolder = ApplicationContext.getInstance()
			.getBaseFolder();

	public LocalListLoader() {
		
	}

	public List<ListRowData> load() {
		File file = new File(mBaseFolder, "list.data");
		BufferedReader reader = null;
		List<ListRowData> list = null;

		try {
			reader = new BufferedReader(new InputStreamReader(
					new FileInputStream(file)));
			String json = read(reader);

			list = JsonHelper.toList(json);
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

		return list;
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
