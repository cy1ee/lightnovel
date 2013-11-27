package tw.com.lightnovel.apis;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import tw.com.lightnovel.activities.BaseNovelIndexActivity.AlertDialogFragment;
import tw.com.lightnovel.activities.BaseNovelIndexActivity.NovelIndexHandler;
import tw.com.lightnovel.classes.ListRowData;
import tw.com.lightnovel.classes.NovelIndex;
import tw.com.lightnovel.classes.TSONovelURLs;
import tw.com.lightnovel.classes.Volume;
import tw.com.lightnovel.core.ApplicationContext;
import tw.com.lightnovel.core.ObjectFactory;
import tw.com.lightnovel.core.SubApplication;
import tw.com.lightnovel.image.HttpImageManager;
import tw.com.lightnovel.loader.NovelLoader;
import tw.com.lightnovel.logging.ILogger;
import tw.com.lightnovel.network.TSOHttpPost;
import tw.com.lightnovel.parsers.BaseParser;
import tw.com.lightnovel.parsers.ChapterParser;
import tw.com.lightnovel.parsers.NovelIndexParser;
import tw.com.lightnovel.parsers.VolumeParser;
import android.app.Activity;
import android.app.AlertDialog;

public class NovelDownloader {
	public static final String DESCRIPTION_TAG = "description";
	public static final String NOVEL_NAME_TAG = "name";
	public static final String VOLUME_TAG = "volume";

	private ILogger mLogger = ApplicationContext.Logger
			.forType(NovelDownloader.class);

	private File mNovelFolder;

	private final ITranslator mTranslator = ApplicationContext.getInstance()
			.getTranslator();

	private final String mNovelIndexUrl;

	private final List<ListRowData> mDownloadList;

	private final List<ListRowData> mLocalList;

	private final String mFolderName;

	private HttpImageManager mImageManager = new HttpImageManager();

	private NovelIndexHandler mHandler;

	public NovelDownloader(Activity activity, String url,
			List<ListRowData> downloadList, NovelIndexHandler handler) {
		mFolderName = getHtmlPageName(url);
		mNovelFolder = createDirectory(ApplicationContext.getInstance()
				.getBaseFolder(), mFolderName);
		mNovelIndexUrl = url;
		mHandler = handler;

		NovelIndex index = new NovelLoader(mFolderName).loadNovelIndex();
		if (index != null) {
			mLocalList = index.getVolumes();
		} else {
			mLocalList = new ArrayList<ListRowData>();
		}

		mDownloadList = filterDownloadList(downloadList);
		if (mDownloadList.size() == 0) {
			mLogger.log("all of the selected volumes have been downloaded");
			return;
		}

		Thread t = new Thread(new Runnable() {

			public void run() {
				task();
			}
		});
		t.start();
	}

	private List<ListRowData> filterDownloadList(List<ListRowData> downloadList) {
		List<String> titleList = toTitleList(mLocalList);
		List<ListRowData> filteredList = new ArrayList<ListRowData>();

		for (ListRowData item : downloadList) {
			if (!titleList.contains(item.getTitle())) {
				filteredList.add(item);
			}
		}

		return filteredList;
	}

	private List<String> toTitleList(List<ListRowData> list) {
		List<String> titleList = new ArrayList<String>();

		for (ListRowData item : list) {
			titleList.add(item.getTitle());
		}

		return titleList;
	}

	private final String getHtmlPageName(String uri) {
		return uri.substring(uri.lastIndexOf('/') + 1, uri.lastIndexOf('.'));
	}

	private void updateVolumeData(File folder, ListRowData fingerPrint) {
		String volumeUri = new File(folder, "info.data").getAbsolutePath();
		fingerPrint.setUri(volumeUri);
		String imageUri = new File(folder, folder.getName() + ".jpg")
				.getAbsolutePath();
		fingerPrint.setImageUri(imageUri);
	}

	private void taskForVolume(File folder, ListRowData volumeFingerPrint) {
		Volume volume = download(volumeFingerPrint.getUri(),
				VolumeParser.class);
		String folderName = getHtmlPageName(volumeFingerPrint.getUri());
		File volumeFolder = createDirectory(folder, folderName);
		volume.setImageUri(new File(volumeFolder, folderName + ".jpg")
				.getAbsolutePath());

		for (ListRowData chapterFingerPrint : volume.getChapters()) {
			taskForChapter(volumeFolder, chapterFingerPrint);
		}

		save(volumeFolder, "info.data", volume);
		saveImage(
				volumeFolder,
				downloadSingleImage(volumeFingerPrint.getImageUri(), folderName
						+ ".jpg"));

		updateVolumeData(volumeFolder, volumeFingerPrint);
	}

	private void taskForChapter(File folder, ListRowData chapterFingerPrint) {
		String folderName = getHtmlPageName(chapterFingerPrint.getUri());
		File chapterFolder = createDirectory(folder, folderName);

		List<String> contentLines = download(chapterFingerPrint.getUri(),
				ChapterParser.class);
		Map<String, byte[]> map = downloadImages(contentLines, new File(folder,
				folderName), folderName);

		chapterFingerPrint.setUri(chapterFolder.getAbsolutePath());
		saveChapter(chapterFolder, contentLines);
		saveImage(chapterFolder, map);
	}

	private void task() {
		NovelIndex novelIndex = download(mNovelIndexUrl, NovelIndexParser.class);
		saveImage(
				mNovelFolder,
				downloadSingleImage(novelIndex.getImageUri(), mFolderName
						+ ".jpg"));
		String imageUri = new File(mNovelFolder, mFolderName + ".jpg")
				.getAbsolutePath();
		novelIndex.setImageUri(imageUri);

		for (ListRowData volumeFingerPrint : mDownloadList) {
			taskForVolume(mNovelFolder, volumeFingerPrint);
		}

		addToNovelList(novelIndex);

		// combine download list with local volume list
		mDownloadList.addAll(mLocalList);
		novelIndex.setVolumes(mDownloadList);
		save(mNovelFolder, "info.data", novelIndex);
		mLogger.log("download done!");
		
		mHandler.showAlert("¤U¸ü§¹¦¨");
	}

	private void addToNovelList(NovelIndex novelIndex) {
		FileOutputStream stream = null;

		try {
			File file = new File(ApplicationContext.getInstance()
					.getBaseFolder(), "list.data");
			ListRowData novel = new ListRowData(novelIndex.getName(),
					novelIndex.getImageUri(), mFolderName);

			Map<String, ListRowData> map = loadNovelMap(file);
			if (!map.containsKey(mFolderName)) {
				map.put(mFolderName, novel);
				JSONObject jsonObject = new JSONObject(map);

				stream = new FileOutputStream(file);
				stream.write(jsonObject.toString().getBytes());
			}
		} catch (Exception e) {
			mLogger.error(e, "fail to add novel to list");
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e) {

				}
			}
		}
	}

	private Map<String, ListRowData> loadNovelMap(File baseFolder) {
		if (baseFolder == null) {
			throw new NullPointerException();
		}

		if (!baseFolder.exists()) {
			return new HashMap<String, ListRowData>();
		}

		Map<String, ListRowData> map = null;
		FileReader reader = null;

		try {
			reader = new FileReader(baseFolder);
			StringBuilder sb = new StringBuilder();

			char[] buffer = new char[512];
			int len = 0;
			while ((len = reader.read(buffer)) > 0) {
				sb.append(buffer, 0, len);
			}

			String json = sb.toString();
			map = JsonHelper.toMap(json);
		} catch (FileNotFoundException e) {
			mLogger.error(e, "File not found" + baseFolder.getAbsolutePath());
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

		if (map != null) {
			return map;
		}

		return new HashMap<String, ListRowData>();
	}

	private void saveChapter(File folder, List<String> lines) {
		FileOutputStream stream = null;
		File file = new File(folder, "content.data");

		try {
			stream = new FileOutputStream(file, true);

			for (String line : lines) {
				stream.write(line.getBytes());
				stream.write("\n".getBytes());
			}

		} catch (FileNotFoundException e) {
			mLogger.error(e, "file not found [" + file.getAbsolutePath() + "]");
		} catch (IOException e) {
			mLogger.error(e, "IOException");
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e) {

				}
			}
		}
	}

	private void saveImage(File folder, Map<String, byte[]> images) {
		for (String key : images.keySet()) {
			FileOutputStream stream = null;
			File file = new File(folder, key);
			try {
				if (file.exists()) {
					file.delete();
				}

				stream = new FileOutputStream(file, true);
				stream.write(images.get(key));
			} catch (FileNotFoundException e) {
				mLogger.error(e, "file not found [" + file.getAbsolutePath()
						+ "]");
			} catch (IOException e) {
				mLogger.error(e, "IOException");
			} finally {
				if (stream != null) {
					try {
						stream.close();
					} catch (IOException e) {

					}
				}
			}
		}
	}

	private void save(File folder, String fileName, IJsonSerilizable obj) {
		FileOutputStream stream = null;
		File file = new File(folder, fileName);

		try {
			stream = new FileOutputStream(file, false);
			stream.write(obj.toJson().toString().getBytes());
		} catch (FileNotFoundException e) {
			mLogger.error(e, "file not found [" + file.getAbsolutePath() + "]");
		} catch (IOException e) {
			mLogger.error(e, "IOException");
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e) {

				}
			}
		}
	}

	private <TResult, TParser extends BaseParser> TResult download(String url,
			Class<TParser> cls) {
		TSOHttpPost post = new TSOHttpPost(url, null);
		TParser parser = new ObjectFactory<TParser>(cls).newInstance();
		parser.setRequest(post);
		parser.setTranslator(mTranslator);
		return (TResult) parser.parse();
	}

	private Map<String, byte[]> downloadSingleImage(String url, String name) {
		if (!url.contains("http://")) {
			url = TSONovelURLs.BASE_URL + url;
		}

		byte[] coverImage = mImageManager.reliableLoadImage(url);

		Map<String, byte[]> map = new HashMap<String, byte[]>();
		map.put(name, coverImage);
		return map;
	}

	private Map<String, byte[]> downloadImages(List<String> lines, File folder,
			String prefix) {

		int imageCounter = 0;
		Map<String, byte[]> map = new HashMap<String, byte[]>();

		for (int i = 0; i < lines.size(); i++) {
			String line = lines.get(i);

			if (line.contains("http://")
					&& (line.contains(".jpg") || line.contains(".jpeg"))) {
				byte[] imageBytes = mImageManager.reliableLoadImage(line);
				String imageName = String.format("%s_%03d.jpg", prefix,
						imageCounter++);
				String path = new File(folder, imageName).getAbsolutePath();
				lines.set(i, path);
				map.put(imageName, imageBytes);
			}
		}

		return map;
	}

	private File createDirectory(File file, String folder) {
		File directory = new File(file, folder);
		directory.mkdirs();
		return directory;
	}
}
