package tw.com.lightnovel.adapters;

import java.util.List;

import tw.com.lightnovel.classes.ListRowData;
import tw.com.lightnovel.classes.TSONovelURLs;
import tw.com.lightnovel.image.ImageManager;
import tw.com.thinkso.novelreaderapp.R;
import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MainListDataAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	private final List<ListRowData> mRowData;	
	private ImageManager mImageManager;

	public MainListDataAdapter(Activity activity,
			List<ListRowData> rowData, ImageManager imageManager) {
		mInflater = LayoutInflater.from(activity);
		mRowData = rowData;
		mImageManager = imageManager;
	}
	
	public int getCount() {
		return mRowData.size();
	}

	public Object getItem(int position) {
		return mRowData.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(final int position, View view, ViewGroup parent) {
		final ViewHolder holder;

		if (view == null) {
			view = mInflater.inflate(R.layout.list_item, null);
			holder = new ViewHolder();
			holder.text = (TextView) view.findViewById(R.id.main_content_text);
			holder.pic = (ImageView) view.findViewById(R.id.main_content_pic);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		
		holder.text.setText(mRowData.get(position).getTitle());
		holder.pic.setVisibility(View.INVISIBLE);

		String imageUrl = mRowData.get(position).getImageUri();
		if (!imageUrl.contains("http://") && !imageUrl.contains("mnt") && !imageUrl.contains("storage")) {
			imageUrl = TSONovelURLs.BASE_URL + imageUrl;
		}		 
		
		if (!mImageManager.IsCache(imageUrl) && !mImageManager.IsLoading(imageUrl)) {			
			mImageManager.LoadPic(imageUrl,
					mHandler);
		} else if (mImageManager.IsLoadFine(imageUrl) == true) {			
			holder.pic.setImageBitmap(mImageManager.getImg(imageUrl));
			holder.pic.setVisibility(View.VISIBLE);
		} else {

		}
		return view;
	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {			
			notifyDataSetChanged();
			super.handleMessage(msg);
		}
	};

	private class ViewHolder {
		TextView text;
		ImageView pic;		
	}
}