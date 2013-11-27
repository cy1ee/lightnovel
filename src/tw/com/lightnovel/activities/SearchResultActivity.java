package tw.com.lightnovel.activities;

import java.util.List;

import tw.com.lightnovel.adapters.MainListDataAdapter;
import tw.com.lightnovel.apis.ITranslator;
import tw.com.lightnovel.apis.Zhcoder;
import tw.com.lightnovel.classes.ListRowData;
import tw.com.lightnovel.classes.NovelSearchListener;
import tw.com.lightnovel.classes.TSONovelURLs;
import tw.com.lightnovel.core.ApplicationContext;
import tw.com.lightnovel.image.HttpImageManager;
import tw.com.lightnovel.image.ImageManager;
import tw.com.lightnovel.network.TSOHttpPost;
import tw.com.lightnovel.parsers.TSOSearchResultParser;
import tw.com.thinkso.novelreaderapp.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;

public class SearchResultActivity extends Activity {
	private final int SHOW_SEARCH_RESULT = 1;

	private ApplicationContext mApp;
	private ITranslator mTranslator;
	private Zhcoder mZhcoder;
	private ImageManager mImgManager;
	private List<ListRowData> mResultList;

	private ListView mLvResult;
	private ProgressBar mPbSearch;

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {

			case SHOW_SEARCH_RESULT:
				mResultList = (List<ListRowData>) msg.obj;
				setList(mResultList);
				mPbSearch.setVisibility(View.INVISIBLE);
				mLvResult.setVisibility(View.VISIBLE);

				break;

			default:
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_result);

		final String searchContent = getIntent().getStringExtra(
				NovelSearchListener.SEARCH_CONTENT_TAG);

		mApp = ApplicationContext.getInstance();
		mZhcoder = mApp.getZhCoder();

		getActionBar().setTitle(
				getString(R.string.search_title) + " " + searchContent);

		mTranslator = mApp.getTranslator();
		mImgManager = new HttpImageManager();

		mLvResult = (ListView) findViewById(R.id.result_list);
		mLvResult.setOnItemClickListener(mListItemClickListener);

		mPbSearch = (ProgressBar) findViewById(R.id.progress_search);

		Thread t = new Thread() {
			@Override
			public void run() {
				String url = getUrl(searchContent);

				TSOHttpPost post = new TSOHttpPost(url, null);
				TSOSearchResultParser parser = new TSOSearchResultParser(post);

				parser.setTranslator(mTranslator);

				Message msg = mHandler.obtainMessage(SHOW_SEARCH_RESULT);
				msg.obj = parser.getLines();
				mHandler.sendMessage(msg);
			}
		};

		t.start();
	}

	private String getUrl(String searchContent) {
		String url = TSONovelURLs.SEARCH_URL + searchContent.trim() + ".html";
		url = mZhcoder.convertString(url, Zhcoder.BIG5, Zhcoder.GB2312);
		url = url.replace(" ", "%20");

		return url;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_search_result, menu);
		return true;
	}

	private OnItemClickListener mListItemClickListener = new OnItemClickListener() {

		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {

			if (mResultList == null)
				return;

			ListRowData novel = mResultList.get(arg2);

			Intent intent = new Intent(getApplicationContext(),
					OnlineVolumeActivity.class);
			intent.putExtra(TSONovelURLs.VOLUME_URL_TAG, novel.getUri());
			startActivity(intent);
		}
	};

	private void setList(List<ListRowData> searchResult) {
		mLvResult.setAdapter(new MainListDataAdapter(this, searchResult,
				mImgManager));
	}

}
