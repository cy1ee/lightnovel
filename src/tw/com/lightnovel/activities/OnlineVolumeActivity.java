package tw.com.lightnovel.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.AsyncTask.Status;
import tw.com.lightnovel.classes.TSONovelURLs;
import tw.com.lightnovel.classes.Volume;
import tw.com.lightnovel.core.ApplicationContext;
import tw.com.lightnovel.core.HttpRequestTask;
import tw.com.lightnovel.database.VolumeDatabase;
import tw.com.lightnovel.image.HttpImageManager;
import tw.com.lightnovel.logging.ILogger;
import tw.com.lightnovel.network.TSOHttpPost;
import tw.com.lightnovel.parsers.BaseParser;
import tw.com.lightnovel.parsers.VolumeParser;

public class OnlineVolumeActivity extends BaseVolumeActivity {
	private ILogger mLogger = ApplicationContext.Logger
			.forType(OnlineNovelIndexActivity.class);

	private VolumeDatabase mDatabase;
	private HttpRequestTask<Volume> mTask;

	@Override
	protected void onPause() {
		super.onPause();
		mDatabase.save(mVolume);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		if (mTask != null && mTask.getStatus() == Status.RUNNING) {
			mTask.cancel(true);
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mDatabase = mApp.getVolumeDatabase();
		mImageManager = new HttpImageManager();
		
		loadVolume();
	}
	
	@Override
	protected void onNovelButtonClicked(String uri) {			
		Intent intent = new Intent(OnlineVolumeActivity.this,
				OnlineNovelIndexActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra(TSONovelURLs.NOVEL_URL_TAG, uri);
		startActivity(intent);
		finish();
	}
	
	@Override
	protected void onChapterButtonClicked(String uri, String volumeUri) {		
		Intent intent = new Intent(OnlineVolumeActivity.this,
				OnlineChapterContentActivity.class);

		intent.putExtra(TSONovelURLs.URL_TAG, uri);
		intent.putExtra(TSONovelURLs.VOLUME_URL_TAG, volumeUri);

		startActivity(intent);
	}

	private void startTask() {
		TSOHttpPost post = new TSOHttpPost(mVolumeUrl, null);
		BaseParser parser = new VolumeParser(post);

		parser.setTranslator(mTranslator);
		mTask = new HttpRequestTask<Volume>();
		mTask.setOnRequestPostExecuteListener(new HttpRequestTask.OnRequestPostExecuteListener<Volume>() {

			public void onRequestPostExecuted(Volume result) {
				mVolume = result;
				mVolume.setUri(mVolumeUrl);
				mApp.addVolume(mVolumeUrl, mVolume);
				notifyVolumeLoaded(mVolume);
				showView();
			}
		});

		mTask.execute(parser);
	}

	private void loadVolume() {
		if (mApp.containsVolume(mVolumeUrl)) {
			mLogger.log("load volume from cache");

			mVolume = mApp.getVolume(mVolumeUrl);
			notifyVolumeLoaded(mVolume);
			showView();
		} else {
			mLogger.log("try to load volume from database");

			Volume volume = mDatabase.fetchVolume(mVolumeUrl);

			if (volume != null) {
				mLogger.log("volume is loaded from database");

				mVolume = volume;
				notifyVolumeLoaded(mVolume);
				showView();
				mApp.cacheVolume(mVolumeUrl, mVolume);
			} else {
				mLogger.log("load volume from website");
				startTask();
			}
		}
	}
}
