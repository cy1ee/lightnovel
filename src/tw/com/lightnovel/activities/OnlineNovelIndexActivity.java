package tw.com.lightnovel.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.AsyncTask.Status;
import android.view.Menu;
import android.view.MenuItem;
import tw.com.lightnovel.adapters.VolumListAdapter.Mode;
import tw.com.lightnovel.classes.NovelIndex;
import tw.com.lightnovel.classes.TSONovelURLs;
import tw.com.lightnovel.core.HttpRequestTask;
import tw.com.lightnovel.image.HttpImageManager;
import tw.com.lightnovel.network.TSOHttpPost;
import tw.com.lightnovel.parsers.BaseParser;
import tw.com.lightnovel.parsers.NovelIndexParser;
import tw.com.thinkso.novelreaderapp.R;

public class OnlineNovelIndexActivity extends BaseNovelIndexActivity {
	private HttpRequestTask<NovelIndex> mTask;

	@Override
	protected void onResume() {
		super.onResume();
		startTask();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {	
		super.onCreate(savedInstanceState);
		
		 mImageManager = new HttpImageManager();
	}

	private void startTask() {
		TSOHttpPost post = new TSOHttpPost(mUrl, null);
		BaseParser parser = new NovelIndexParser(post);

		parser.setTranslator(mTranslator);
		mTask = new HttpRequestTask<NovelIndex>();
		mTask.setOnRequestPostExecuteListener(new HttpRequestTask.OnRequestPostExecuteListener<NovelIndex>() {

			public void onRequestPostExecuted(NovelIndex result) {
				setNovelIndex(result);
			}
		});

		mTask.execute(parser);
	}
	
	@Override
	protected void readVolume(String uri) {		
		super.readVolume(uri);
		
		Intent intent = new Intent(OnlineNovelIndexActivity.this,
				OnlineVolumeActivity.class);
		intent.putExtra(TSONovelURLs.VOLUME_URL_TAG, uri);
		startActivity(intent);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		if (mTask != null && mTask.getStatus() == Status.RUNNING) {
			mTask.cancel(true);
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.novel_index_actions, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;

		case R.id.novel_action_download_mode:
			if (mListAdapter != null) {
				switch (mListAdapter.getMode()) {
				case Normal:
					mListAdapter.setMode(Mode.Edit);
					item.setTitle("¨ú®ø");
					break;
				case Edit:
					mListAdapter.setMode(Mode.Normal);
					item.setTitle("¤U¸ü");
					break;
				}

				return true;
			}
		}
		return super.onOptionsItemSelected(item);
	}
}
