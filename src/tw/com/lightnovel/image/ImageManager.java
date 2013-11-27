package tw.com.lightnovel.image;

import java.util.HashMap;

import android.graphics.Bitmap;
import android.os.Handler;

public abstract class ImageManager {	
	protected static HashMap<String, byte[]> mImageCache = new HashMap<String, byte[]>();		
	
	public abstract boolean IsLoadFine(String u);

	public abstract boolean IsLoading(String u);

	public abstract void LoadPicWithoutCache(final String u, final Handler h);

	public abstract void LoadPic(final String u, final Handler h);

	public ImageManager() {	}	

	public Bitmap getImg(String u) {
		return ImageUtil.imageFromBytes(mImageCache.get(u));
	}

	public boolean IsCache(String u) {
		return mImageCache.containsKey(u);
	}	

	public class ImageUrlPair {
		final String mUrl;
		final byte[] mImageBytes;

		public ImageUrlPair(String url, byte[] imageBytes) {
			mUrl = url;
			mImageBytes = imageBytes;
		}

		public String getUrl() {
			return mUrl;
		}

		public byte[] getImage() {
			return mImageBytes;
		}
	}
}