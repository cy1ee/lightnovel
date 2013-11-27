package tw.com.lightnovel.image;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import tw.com.lightnovel.core.ApplicationContext;
import tw.com.lightnovel.logging.ILogger;
import android.os.Handler;

public class LocalImageManager extends ImageManager {
	private List<String> mLoadList = new ArrayList<String>();
	private ILogger mLogger = ApplicationContext.Logger
			.forType(LocalImageManager.class);

	@Override
	public boolean IsLoadFine(String u) {
		return (!mLoadList.contains(u) && IsCache(u));
	}

	@Override
	public boolean IsLoading(String u) {
		return mLoadList.contains(u);
	}

	@Override
	public void LoadPicWithoutCache(final String u, final Handler h) {
		mLoadList.add(u);
		new Thread(new Runnable() {			
			public void run() {
				byte[] imagBytes = loadFromDisk(u);

				ImageUrlPair pair = new ImageUrlPair(u, imagBytes);
				h.sendMessage(h.obtainMessage(ImageHandlerCode.DOWNLOAD_FINISH, pair));
				mLoadList.remove(u);
			}
		}).start();
	}

	@Override
	public void LoadPic(final String u, final Handler h) {		
		mLoadList.add(u);
		new Thread(new Runnable() {
			public void run() {
				byte[] imagBytes = loadFromDisk(u);

				mImageCache.put(u, imagBytes);
				h.sendMessage(h.obtainMessage(ImageHandlerCode.DOWNLOAD_FINISH, imagBytes));
				mLoadList.remove(u);
			}
		}).start();
	}

	private synchronized byte[] loadFromDisk(String uri) {
		File file = new File(uri);
		FileInputStream stream = null;
		byte[] imageBytes = null;

		try {
			stream = new FileInputStream(file);
			imageBytes = new byte[(int) file.length()];
			byte[] buffer = new byte[512];
			int offset = 0;
			int len = stream.read(buffer);

			while (len > 0) {
				System.arraycopy(buffer, 0, imageBytes, offset, len);
				offset += len;
				len = stream.read(buffer);
			}
		} catch (FileNotFoundException e) {
			mLogger.error(e, "FileNotFoundException");
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

		return imageBytes;

	}

}
