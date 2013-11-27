package tw.com.lightnovel.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import tw.com.lightnovel.classes.NovelIndex;
import tw.com.lightnovel.classes.TSONovelURLs;
import tw.com.lightnovel.core.ApplicationContext;
import tw.com.lightnovel.image.LocalImageManager;
import tw.com.lightnovel.loader.NovelLoader;
import tw.com.lightnovel.logging.ILogger;

public class LocalNovelIndexActivity extends BaseNovelIndexActivity {
	private ILogger mLogger = ApplicationContext.Logger
			.forType(LocalNovelIndexActivity.class);

	@Override
	protected void onResume() {
		super.onResume();
		NovelLoader loader = new NovelLoader(mUrl);
		NovelIndex index = loader.loadNovelIndex();
		setNovelIndex(index);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mImageManager = new LocalImageManager();
		mBtnKeepNovel.setVisibility(View.GONE);
	}

	@Override
	protected void readVolume(String uri) {
		super.readVolume(uri);

		Intent intent = new Intent(LocalNovelIndexActivity.this,
				LocalVolumeActivity.class);
		intent.putExtra(TSONovelURLs.VOLUME_URL_TAG, uri);
		startActivity(intent);
	}
}
