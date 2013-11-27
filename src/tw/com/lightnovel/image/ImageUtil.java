package tw.com.lightnovel.image;

import tw.com.lightnovel.core.ApplicationContext;
import tw.com.lightnovel.core.SubApplication;
import tw.com.lightnovel.logging.ILogger;
import tw.com.thinkso.novelreaderapp.R;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Handler;

public class ImageUtil {	
	private static ILogger mLogger = ApplicationContext.Logger
			.forType(ImageUtil.class);

	private static Bitmap nullImage;

	private static Bitmap getNullImage() {
		if (nullImage == null)
			return nullImage = BitmapFactory.decodeResource(SubApplication
					.getContext().getResources(), R.drawable.icon);
		
		return nullImage;
	}

	public static Bitmap imageFromBytes(byte[] imageBytes) {
		if (imageBytes == null)
			return getNullImage();

		return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
	}

	public static Bitmap imageFromBytes(byte[] imageBytes, Point size) {
		if (imageBytes == null)
			return getNullImage();

		BitmapFactory.Options option = new BitmapFactory.Options();
		option.inJustDecodeBounds = true;

		BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length, option);

		mLogger.log(size.x + " " + option.outWidth);
		
		int width = option.outWidth;		
		int scale = 1;
		while (width > size.x) {
			width /= 2;			
			scale++;
		}

		mLogger.log("Image Scale: " + scale);

		option = new BitmapFactory.Options();
		option.inSampleSize = scale;

		return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length,
				option);
	}
}
