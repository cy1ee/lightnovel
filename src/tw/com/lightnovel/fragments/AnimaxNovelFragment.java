package tw.com.lightnovel.fragments;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;

import tw.com.lightnovel.activities.OnlineNovelIndexActivity;
import tw.com.lightnovel.adapters.MainListDataAdapter;
import tw.com.lightnovel.apis.ITranslator;
import tw.com.lightnovel.classes.ListRowData;
import tw.com.lightnovel.classes.TSONovelURLs;
import tw.com.lightnovel.core.ApplicationContext;
import tw.com.lightnovel.core.HttpRequestTask;
import tw.com.lightnovel.image.HttpImageManager;
import tw.com.lightnovel.image.ImageManager;
import tw.com.lightnovel.network.TSOHttpPost;
import tw.com.lightnovel.parsers.AnimaxNovelParser;
import tw.com.lightnovel.parsers.BaseParser;
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

public class AnimaxNovelFragment extends Fragment {
	private ITranslator mTranslator;
	private ImageManager mImgManager;

	private ListView mMainList;
	private ProgressBar mPbNovelLoading;
	private List<ListRowData> mNovels;
	private HttpRequestTask<List<ListRowData>> mTask;

	private static final List<NameValuePair> PARAMS = new ArrayList<NameValuePair>();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_animax_novel, container,
				false);

		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewCreated(view, savedInstanceState);

		mMainList = (ListView) view.findViewById(R.id.main_list);
		mMainList.setOnItemClickListener(mListItemClickListener);

		mPbNovelLoading = (ProgressBar) view
				.findViewById(R.id.progress_animax_novel_loading);
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
	public void onDestroyView() {
		super.onDestroyView();

		if (mTask != null && mTask.getStatus() == Status.RUNNING) {
			mTask.cancel(true);
		}
	}

	private void startTask() {
		TSOHttpPost post = new TSOHttpPost(TSONovelURLs.BASE_URL, PARAMS);
		BaseParser parser = new AnimaxNovelParser(post);
		parser.setTranslator(mTranslator);

		mTask = new HttpRequestTask<List<ListRowData>>();
		mTask.setOnRequestPostExecuteListener(new HttpRequestTask.OnRequestPostExecuteListener<List<ListRowData>>() {

			public void onRequestPostExecuted(List<ListRowData> result) {
				mNovels = result;
				setList(mNovels);
				mPbNovelLoading.setVisibility(View.INVISIBLE);
				mMainList.setVisibility(View.VISIBLE);
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

			Intent intent = new Intent(getActivity(), OnlineNovelIndexActivity.class);
			intent.putExtra(TSONovelURLs.NOVEL_URL_TAG, novel.getUri());
			startActivity(intent);
		}
	};

	private void setList(List<ListRowData> novels) {
		mMainList.setAdapter(new MainListDataAdapter(getActivity(), novels,
				mImgManager));
	}
}
