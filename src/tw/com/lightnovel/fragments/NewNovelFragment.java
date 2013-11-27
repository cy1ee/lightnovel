package tw.com.lightnovel.fragments;

import java.util.List;

import tw.com.lightnovel.activities.OnlineVolumeActivity;
import tw.com.lightnovel.adapters.MainListDataAdapter;
import tw.com.lightnovel.apis.ITranslator;
import tw.com.lightnovel.classes.ListRowData;
import tw.com.lightnovel.classes.TSONovelURLs;
import tw.com.lightnovel.core.ApplicationContext;
import tw.com.lightnovel.core.HttpRequestTask;
import tw.com.lightnovel.image.HttpImageManager;
import tw.com.lightnovel.image.ImageManager;
import tw.com.lightnovel.network.TSOHttpPost;
import tw.com.lightnovel.parsers.BaseParser;
import tw.com.lightnovel.parsers.NewNovelParser;
import tw.com.thinkso.novelreaderapp.R;
import android.content.Intent;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;

public class NewNovelFragment extends Fragment {
	private ITranslator mTranslator;
	private ImageManager mImgManager;

	private ListView mNewNovelList;
	private ProgressBar mPbNovelLoading;
	private List<ListRowData> mNovels;
	private HttpRequestTask<List<ListRowData>> mTask;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_new_novel, container,
				false);
		return view;
	};

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewCreated(view, savedInstanceState);

		mNewNovelList = (ListView) view.findViewById(R.id.new_novel_list);
		mNewNovelList.setOnItemClickListener(mListItemClickListener);

		mPbNovelLoading = (ProgressBar) view
				.findViewById(R.id.progress_new_novel_loading);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		ApplicationContext app = ApplicationContext.getInstance();
		mTranslator = app.getTranslator();
		mImgManager = new HttpImageManager();
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		startTask();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();

		if (mTask != null && mTask.getStatus() == Status.RUNNING) {
			mTask.cancel(true);

		}
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	private void startTask() {
		TSOHttpPost post = new TSOHttpPost(TSONovelURLs.BASE_URL, null);
		BaseParser parser = new NewNovelParser(post);

		parser.setTranslator(mTranslator);
		mTask = new HttpRequestTask<List<ListRowData>>();
		mTask.setOnRequestPostExecuteListener(new HttpRequestTask.OnRequestPostExecuteListener<List<ListRowData>>() {

			public void onRequestPostExecuted(List<ListRowData> result) {
				mNovels = result;
				setList(mNovels);
				mPbNovelLoading.setVisibility(View.INVISIBLE);
				mNewNovelList.setVisibility(View.VISIBLE);
			}
		});

		mTask.execute(parser);
	}

	private OnItemClickListener mListItemClickListener = new OnItemClickListener() {

		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {

			if (mNovels == null)
				return;

			ListRowData novel = mNovels.get(arg2);

			Intent intent = new Intent(getActivity(), OnlineVolumeActivity.class);			
			intent.putExtra(TSONovelURLs.VOLUME_URL_TAG, novel.getUri());
			startActivity(intent);
		}
	};

	private void setList(List<ListRowData> novels) {
		mNewNovelList.setAdapter(new MainListDataAdapter(getActivity(),
				novels, mImgManager));
	}
}
