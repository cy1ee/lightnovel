package tw.com.lightnovel.activities;

import java.util.List;

import tw.com.lightnovel.adapters.ChapterAdapter;
import tw.com.lightnovel.apis.ITranslator;
import tw.com.lightnovel.classes.TSONovelURLs;
import tw.com.lightnovel.classes.Volume;
import tw.com.lightnovel.core.ApplicationContext;
import tw.com.lightnovel.image.ImageManager;
import tw.com.thinkso.novelreaderapp.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;

public abstract class BaseChapterContentActivity extends Activity {
	protected ITranslator mTranslator;
	protected String mUri;
	protected ImageManager mImageManager;
	private Volume mVolume;

	private ProgressBar mPbChapterLoading;
	private ListView mLvLines;
	private LinearLayout mLvLoadMsg;

	private List<String> mLines;

	protected abstract void loadChapterContent(String uri);
	
	protected void notifyChapterContentLoaded(List<String> lines) {
		if (lines.size() == 0) {
			mPbChapterLoading.setVisibility(View.INVISIBLE);
			mLvLoadMsg.setVisibility(View.VISIBLE);
		} else {
			mLines = lines;
			setList(mLines);
			mPbChapterLoading.setVisibility(View.INVISIBLE);
		}
	}

	private void setList(List<String> lines) {
		mLvLines.setAdapter(new ChapterAdapter(BaseChapterContentActivity.this,
				lines, mImageManager));

		if (mVolume != null)
			mLvLines.setSelectionFromTop(mVolume.getReadingPosition(), 0);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chapter_content);

		getActionBar().hide();

		Intent intent = getIntent();
		mUri = intent.getStringExtra(TSONovelURLs.URL_TAG);

		ApplicationContext app = ApplicationContext.getInstance();
		mTranslator = app.getTranslator();
		mVolume = app.getVolume(intent
				.getStringExtra(TSONovelURLs.VOLUME_URL_TAG));
		
		mPbChapterLoading = (ProgressBar) findViewById(R.id.progress_chapter_loading);

		mLvLines = (ListView) findViewById(R.id.line_list);
		mLvLines.setFastScrollEnabled(true);
		mLvLines.setDivider(null);
		mLvLines.setDividerHeight(0);
		mLvLines.setSelector(android.R.color.transparent);

		mLvLoadMsg = (LinearLayout) findViewById(R.id.view_reload);
	}

	public void reload(View v) {
		mPbChapterLoading.setVisibility(View.VISIBLE);
		mLvLoadMsg.setVisibility(View.INVISIBLE);
		
		loadChapterContent(mUri);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();

		if (mVolume != null)
			mVolume.setReadingPosition(mLvLines.getFirstVisiblePosition());
	}
}
