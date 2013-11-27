package tw.com.lightnovel.adapters;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.TokenIterator;

import tw.com.lightnovel.activities.BaseNovelIndexActivity;
import tw.com.lightnovel.apis.NovelDownloader;
import tw.com.lightnovel.classes.ListRowData;
import tw.com.lightnovel.classes.TSONovelURLs;
import tw.com.lightnovel.core.ApplicationContext;
import tw.com.lightnovel.image.HttpImageManager;
import tw.com.lightnovel.image.ImageManager;
import tw.com.lightnovel.logging.ILogger;
import tw.com.thinkso.novelreaderapp.R;
import android.R.integer;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class VolumListAdapter extends BaseAdapter {
	public class VolumeListRow extends ListRowData {
		private boolean mIsChecked;

		public VolumeListRow(ListRowData rowContent) {
			super(rowContent.getTitle(), rowContent.getImageUri(), rowContent
					.getUri());
			mIsChecked = false;
		}

		public void setChecked(boolean checked) {
			mIsChecked = checked;
		}

		public boolean getChecked() {
			return mIsChecked;
		}
	}

	public enum Mode {
		Edit, Normal
	};

	private LayoutInflater mInflater;
	private final List<VolumeListRow> mListRow;
	private final ILogger mLogger = ApplicationContext.Logger
			.forType(VolumListAdapter.class);
	private ImageManager mImageManager;
	private Mode mCurrentMode = Mode.Normal;
	private BaseNovelIndexActivity mParent;

	public VolumListAdapter(BaseNovelIndexActivity activity,
			List<ListRowData> rowContent, ImageManager imageManager) {
		mInflater = LayoutInflater.from(activity);
		mListRow = createListRow(rowContent);
		mImageManager = imageManager;
		mParent = activity;
	}

	private List<VolumeListRow> createListRow(List<ListRowData> rowContent) {
		List<VolumeListRow> listRows = new ArrayList<VolumListAdapter.VolumeListRow>();

		for (ListRowData item : rowContent) {
			VolumeListRow row = new VolumeListRow(item);
			listRows.add(row);
		}

		return listRows;
	}

	public int getCount() {
		return mListRow.size();
	}

	public Object getItem(int position) {
		return mListRow.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public void setMode(Mode mode) {
		switch (mode) {
		case Edit:
			mCurrentMode = Mode.Edit;
			break;
		case Normal:
			mCurrentMode = Mode.Normal;
			break;
		}

		notifyDataSetChanged();
	}

	public void checkAllRow(boolean checked) {
		for (VolumeListRow item : mListRow) {
			item.setChecked(checked);
		}

		notifyDataSetChanged();
	}

	public List<ListRowData> getSelectedRowContent() {
		List<ListRowData> list = new ArrayList<ListRowData>();

		for (int i = 0; i < mListRow.size(); i++) {
			if (mListRow.get(i).getChecked()) {
				list.add(new ListRowData(mListRow.get(i)));
			}
		}

		return list;
	}

	public Mode getMode() {
		return mCurrentMode;
	}

	public View getView(final int position, View view, ViewGroup parent) {
		final ViewHolder holder;

		if (view == null) {
			view = mInflater.inflate(R.layout.novel_index_content, null);
			holder = createHolder(view);

			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}

		holder.chkDownload.setTag(position);
		holder.chkDownload.setChecked(mListRow.get(position).getChecked());

		String title = mListRow.get(position).getTitle();

		String[] token = title.split("\\s+");

		holder.volNumber.setText(token[0]);

		if (token.length > 1) {
			holder.volTitle.setText(token[1]);
		}

		holder.pic.setVisibility(View.INVISIBLE);

		switch (mCurrentMode) {
		case Edit:
			holder.chkDownload.setVisibility(View.VISIBLE);
			break;
		case Normal:
			holder.chkDownload.setVisibility(View.INVISIBLE);
			break;
		}

		String imageUrl = mListRow.get(position).getImageUri();
		if (!imageUrl.contains("http://") && !imageUrl.contains("mnt") && !imageUrl.contains("storage")) {
			imageUrl = TSONovelURLs.BASE_URL + imageUrl;
		}

		if (!mImageManager.IsCache(imageUrl)
				&& !mImageManager.IsLoading(imageUrl)) {
			mImageManager.LoadPic(imageUrl, mHandler);
		} else if (mImageManager.IsLoadFine(imageUrl) == true) {
			holder.pic.setImageBitmap(mImageManager.getImg(imageUrl));
			holder.pic.setVisibility(View.VISIBLE);
		} else {

		}
		return view;
	}

	private boolean containsChecked() {
		for (VolumeListRow item : mListRow) {
			if (item.getChecked())
				return true;
		}

		return false;
	}

	private ViewHolder createHolder(View view) {
		final ViewHolder holder;
		holder = new ViewHolder();
		holder.volNumber = (TextView) view.findViewById(R.id.vol_no);
		holder.volTitle = (TextView) view.findViewById(R.id.vol_title);
		holder.pic = (ImageView) view.findViewById(R.id.vol_image);
		holder.chkDownload = (CheckBox) view.findViewById(R.id.check_download);
		holder.chkDownload.setOnClickListener(new OnClickListener() {

			public void onClick(View view) {
				CheckBox chkDownload = (CheckBox) view;
				int index = (Integer) (view.getTag());
				mListRow.get(index).setChecked(chkDownload.isChecked());
				mParent.showContextualActionBar(containsChecked());

			}
		});
		return holder;
	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			mLogger.log("notifyDataSetChanged");
			notifyDataSetChanged();
			super.handleMessage(msg);
		}
	};

	private class ViewHolder {
		TextView volNumber;
		TextView volTitle;
		ImageView pic;
		CheckBox chkDownload;
	}
}
