package tw.com.lightnovel.activities;

import tw.com.lightnovel.classes.TSONovelURLs;
import tw.com.lightnovel.classes.Volume;
import tw.com.lightnovel.core.ApplicationContext;
import tw.com.lightnovel.database.BookmarkDatabase;
import tw.com.lightnovel.image.LocalImageManager;
import tw.com.lightnovel.loader.VolumeLoader;
import tw.com.lightnovel.logging.ILogger;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;

public class LocalVolumeActivity extends BaseVolumeActivity {
	private ILogger mLogger = ApplicationContext.Logger
			.forType(LocalVolumeActivity.class);

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mImageManager = new LocalImageManager();

		loadVolume();
	}

	@Override
	protected void onChapterButtonClicked(String uri, String volumeUri) {
		Intent intent = new Intent(LocalVolumeActivity.this,
				LocalChapterContentActivity.class);

		intent.putExtra(TSONovelURLs.URL_TAG, uri);
		intent.putExtra(TSONovelURLs.VOLUME_URL_TAG, volumeUri);

		startActivity(intent);
	}

	private void loadVolume() {
		try {
			VolumeLoader loader = new VolumeLoader(mVolumeUrl);
			Volume volume = loader.load();
			notifyVolumeLoaded(volume);
			ApplicationContext.getInstance().cacheVolume(mVolumeUrl, volume);
			showView();
		} catch (Exception e) {
			mLogger.error(e, "fail to load volume from local");
		}
	}
	
	@Override
	protected void onPause() {	
		super.onPause();
		
		BookmarkDatabase database = new BookmarkDatabase(this);
		database.addBookmark(mVolume.getBookmark());
	}

	@Override
	protected void onNovelButtonClicked(String uri) {		
		
	}
}
