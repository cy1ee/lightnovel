package tw.com.lightnovel.adapters;

import java.util.HashMap;
import java.util.List;

import tw.com.lightnovel.activities.BaseChapterContentActivity;
import tw.com.lightnovel.image.HttpImageManager;
import tw.com.lightnovel.image.ImageHandlerCode;
import tw.com.lightnovel.image.ImageManager;
import tw.com.lightnovel.image.ImageUtil;
import tw.com.lightnovel.image.ImageManager.ImageUrlPair;
import tw.com.thinkso.novelreaderapp.R;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ChapterAdapter extends BaseAdapter {
	private final List<String> mLines;
	private final ImageManager mImageManager;
	private final Context mContext;
	private final Point mDisplaySize;
	private LayoutInflater mInflater;
	private HashMap<String, byte[]> mImageCache = new HashMap<String, byte[]>();

	public ChapterAdapter(Activity activity, List<String> lines,
			ImageManager manager) {

		Display display = activity.getWindowManager().getDefaultDisplay();
		mDisplaySize = new Point();
		display.getSize(mDisplaySize);

		mInflater = LayoutInflater.from(activity);
		mContext = activity;
		mLines = lines;
		mImageManager = manager;
	}

	public int getCount() {
		return mLines.size();
	}

	public Object getItem(int position) {
		return mLines.get(position);
	}

	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	public View getView(int index, View view, ViewGroup arg2) {
		final ViewHolder holder;

		if (view == null) {
			view = mInflater.inflate(R.layout.line_item, null);
			holder = new ViewHolder();
			holder.line = (TextView) view.findViewById(R.id.line_text);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}

		final String line = mLines.get(index);

		if (line.indexOf(".jpg") == -1 && line.indexOf(".jpeg") == -1) {
			holder.line.setText("\n" + mLines.get(index) + "\n");
		} else {
			if (mImageCache.containsKey(line)) {
				Bitmap bitmap = ImageUtil.imageFromBytes(mImageCache.get(line),
						mDisplaySize);
				bitmap = scaleImage(bitmap);

				ImageSpan is = new ImageSpan(mContext, bitmap);
				SpannableString text = new SpannableString("\n \n");
				text.setSpan(is, 1, 2, 0);
				holder.line.setText(text);
			} else {
				holder.line.setText("");

				if (mImageManager.IsLoading(line)) {

				} else {
					mImageManager.LoadPicWithoutCache(line, mHandler);
				}
			}
		}

		return view;
	}

	private Bitmap scaleImage(Bitmap originalImage) {
		float width = originalImage.getWidth();
		float height = originalImage.getHeight();		

		if (width > mDisplaySize.x - 70) {
			float ratio = (mDisplaySize.x - 70) / width;

			width *= ratio * 4 / 5;
			height *= ratio * 4 / 5;
		}

		return Bitmap.createScaledBitmap(originalImage, (int) width,
				(int) height, true);		
	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			Log.d("m", "notifyDataSetChanged");

			switch (msg.what) {
			case ImageHandlerCode.DOWNLOAD_FINISH:
				ImageUrlPair pair = (ImageUrlPair) msg.obj;
				mImageCache.put(pair.getUrl(), pair.getImage());
				notifyDataSetChanged();
				break;
			}

			super.handleMessage(msg);
		}
	};

	private class ViewHolder {
		TextView line;
	}
}
