package tw.com.lightnovel.fragments;

import java.util.List;

import tw.com.lightnovel.activities.LocalNovelIndexActivity;
import tw.com.lightnovel.adapters.MainListDataAdapter;
import tw.com.lightnovel.classes.ListRowData;
import tw.com.lightnovel.classes.TSONovelURLs;
import tw.com.lightnovel.core.ApplicationContext;
import tw.com.lightnovel.image.ImageManager;
import tw.com.lightnovel.image.LocalImageManager;
import tw.com.lightnovel.loader.LocalListLoader;
import tw.com.lightnovel.logging.ILogger;
import tw.com.thinkso.novelreaderapp.R;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class LocalNovelFragment extends Fragment {
	private List<ListRowData> mVolumeList = null;
	private ListView mLvVolumeList;
	private MainListDataAdapter mAdapter;
	private ImageManager mImageManager;
	private final ILogger mLogger = ApplicationContext.Logger
			.forType(LocalNovelFragment.class);

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater
				.inflate(R.layout.fragment_local_novel, container, false);

	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		mLvVolumeList = (ListView) view.findViewById(R.id.local_novel_list);
		mLvVolumeList.setOnItemClickListener(mListItemClickListener);
	}

	@Override
	public void onResume() {
		super.onResume();

		if (mVolumeList == null) {
			LocalListLoader loader = new LocalListLoader();
			mVolumeList = loader.load();
		}

		setList(mVolumeList);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mImageManager = new LocalImageManager();
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	private void setList(List<ListRowData> records) {
		mAdapter = new MainListDataAdapter(getActivity(), records,
				mImageManager);
		mLvVolumeList.setAdapter(mAdapter);
	}

	private OnItemClickListener mListItemClickListener = new OnItemClickListener() {

		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {

			if (mVolumeList == null)
				return;

			ListRowData novel = mVolumeList.get(arg2);
			Intent intent = new Intent(getActivity(),
					LocalNovelIndexActivity.class);
			intent.putExtra(TSONovelURLs.NOVEL_URL_TAG, novel.getUri());			
			startActivity(intent);
			getActivity().overridePendingTransition(R.anim.slide_in_right,
					android.R.anim.fade_out);
		}
	};
}
