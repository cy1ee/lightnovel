package tw.com.lightnovel.image;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import tw.com.lightnovel.apis.Utility;
import tw.com.lightnovel.core.ApplicationContext;
import tw.com.lightnovel.logging.ILogger;
import android.os.Handler;

public class HttpImageManager extends ImageManager {
	private List<String> mDownloadList = new ArrayList<String>();
	private ILogger mLogger = ApplicationContext.Logger
			.forType(HttpImageManager.class);
	private final File mCache;
	private final int timeInterval = 1000;
	private final int retryCount = 3;
	private final int timeMutiplier = 2;

	public HttpImageManager() {
		mCache = new File(ApplicationContext.getInstance().getBaseFolder(),
				"image_cache");
		mCache.mkdirs();
	}

	public boolean IsLoadFine(String u) {
		return (!mDownloadList.contains(u) && IsCache(u));
	}

	public boolean IsLoading(String u) {
		return (mDownloadList.contains(u));
	}

	private void cacheImage(String name, byte[] imageBytes) {
		if (imageBytes == null)
			return;

		File f = new File(mCache, name + ".jpg");

		if (f.exists())
			f.delete();

		FileOutputStream stream = null;

		try {
			stream = new FileOutputStream(f);
			stream.write(imageBytes);
		} catch (FileNotFoundException e) {

		} catch (IOException e) {
			if (f.exists())
				f.delete();
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e) {
				}
			}
		}
	}

	private byte[] tryLoadFromLocalCache(String name) {
		File f = new File(mCache, name + ".jpg");

		if (!f.exists())
			return null;

		FileInputStream stream = null;
		byte[] imageBytes = new byte[(int) f.length()];

		try {
			stream = new FileInputStream(f);
			stream.read(imageBytes);
		} catch (IOException e) {
			mLogger.error(e, "fail to load image from [" + f.getAbsolutePath()
					+ "]");
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return imageBytes;
	}

	public void LoadPicWithoutCache(final String u, final Handler h) {
		mDownloadList.add(u);

		new Thread(new Runnable() {
			public void run() {
				byte[] imageBytes = tryLoadFromLocalCache(Utility.sha1Hash(u));

				if (imageBytes == null) {
					imageBytes = reliableLoadImage(u);
					cacheImage(Utility.sha1Hash(u), imageBytes);
				}

				ImageUrlPair pair = new ImageUrlPair(u, imageBytes);
				h.sendMessage(h.obtainMessage(ImageHandlerCode.DOWNLOAD_FINISH,
						pair));
				mImageCache.remove(u);
				mDownloadList.remove(u);
			}
		}).start();
	}

	public void LoadPic(final String u, final Handler h) {
		mDownloadList.add(u);

		new Thread(new Runnable() {
			public void run() {
				byte[] imageBytes = tryLoadFromLocalCache(Utility.sha1Hash(u));

				if (imageBytes == null) {
					imageBytes = reliableLoadImage(u);
					cacheImage(Utility.sha1Hash(u), imageBytes);
				}

				mImageCache.put(u, imageBytes);
				h.sendMessage(h.obtainMessage(ImageHandlerCode.DOWNLOAD_FINISH,
						imageBytes));
				mDownloadList.remove(u);
			}
		}).start();
	}

	public byte[] reliableLoadImage(String url) {
		int waitTime = timeInterval;
		
		for (int i = 0; i < retryCount; i++) {
			byte[] imageBytes = LoadUrlPic(url);

			if (imageBytes != null)
				return imageBytes;
			
			try {
				Thread.sleep(waitTime);
			} catch (InterruptedException e) {
				
			}
			
			waitTime *= timeMutiplier;
		}

		return null;
	}

	private synchronized byte[] LoadUrlPic(String url) {
		URL imgUrl;

		byte[] imageBytes;

		try {
			imgUrl = new URL(url);
		} catch (MalformedURLException e) {
			mLogger.error(e, "MalformedURLException");
			return null;
		}

		try {
			HttpURLConnection httpURLConnection = (HttpURLConnection) imgUrl
					.openConnection();
			httpURLConnection.setDoInput(true);
			httpURLConnection.setDoOutput(false);
			httpURLConnection.setUseCaches(false);
			httpURLConnection.connect();

			InputStream inputStream = httpURLConnection.getInputStream();
			int length = (int) httpURLConnection.getContentLength();
			int tmpLength = 512;
			int readLen = 0, desPos = 0;
			imageBytes = new byte[length];
			byte[] tmp = new byte[tmpLength];

			if (length != -1) {
				while ((readLen = inputStream.read(tmp)) > 0) {
					System.arraycopy(tmp, 0, imageBytes, desPos, readLen);
					desPos += readLen;
				}
			}

			httpURLConnection.disconnect();
		} catch (IOException e) {
			mLogger.error(e, "IOException");
			return null;
		} catch (Exception e) {
			mLogger.error(e, "image url: " + url);
			return null;
		}

		return imageBytes;
	}
}
