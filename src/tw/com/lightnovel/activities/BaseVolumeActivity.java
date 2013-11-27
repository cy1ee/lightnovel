package tw.com.lightnovel.activities;

import java.util.List;

import tw.com.lightnovel.apis.ITranslator;
import tw.com.lightnovel.classes.ListRowData;
import tw.com.lightnovel.classes.TSONovelURLs;
import tw.com.lightnovel.classes.Volume;
import tw.com.lightnovel.classes.VolumeDescription;
import tw.com.lightnovel.core.ApplicationContext;
import tw.com.lightnovel.image.ImageHandlerCode;
import tw.com.lightnovel.image.ImageManager;
import tw.com.lightnovel.image.ImageUtil;
import tw.com.lightnovel.logging.ILogger;
import tw.com.thinkso.novelreaderapp.R;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public abstract class BaseVolumeActivity extends Activity {
	protected ApplicationContext mApp;
	protected String mVolumeUrl;
	protected ITranslator mTranslator;
	protected ImageManager mImageManager;

	private TextView mNovelName;
	private TextView mAuthor;
	private TextView mUpdateDateTime;
	private TextView mViewCount;
	private TextView mTvDescription;
	private ImageView mVolumeImage;
	private ImageView mIvCollapseIndicator;
	private Button mBtnNovelIndex;
	private LinearLayout mLlVolumeContent;
	private ProgressBar mPbVolumeLoading;
	private Button[] mChapterButtons;

	private int mTvDescriptionHeight;

	private ILogger mLogger = ApplicationContext.Logger
			.forType(BaseVolumeActivity.class);

	protected Volume mVolume;

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {

			switch (msg.what) {

			case ImageHandlerCode.DOWNLOAD_FINISH:
				mVolumeImage.setImageBitmap(ImageUtil
						.imageFromBytes((byte[]) msg.obj));
				break;
			}
		};
	};

	protected abstract void onNovelButtonClicked(String uri);

	protected abstract void onChapterButtonClicked(String uri, String volumeUri);

	protected void notifyVolumeLoaded(Volume volume) {
		mVolume = volume;

		mBtnNovelIndex.setOnClickListener(new NovelButtonClickListener(volume
				.getNovelUri()));

		setVolumeDescription(volume);
		setChapterButtons(volume.getChapters());
	}

	protected void showView() {
		mPbVolumeLoading.setVisibility(View.INVISIBLE);
		mLlVolumeContent.setVisibility(View.VISIBLE);
	}

	private void setVolumeDescription(Volume volume) {
		VolumeDescription description = volume.getDescription();
		mLogger.log(description.toString());
		if (mImageManager.IsCache(volume.getImageUri())) {
			Bitmap bitmap = mImageManager.getImg(volume.getImageUri());
			mVolumeImage.setImageBitmap(bitmap);
		} else {
			mImageManager.LoadPic(volume.getImageUri(), mHandler);
		}

		mNovelName.setText(description.getName());
		mAuthor.setText(description.getAuthor());
		mUpdateDateTime.setText(description.getUpdateDateTime());
		mViewCount.setText(description.getViewCount());

		ViewTreeObserver observer = mTvDescription.getViewTreeObserver();
		observer.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			public void onGlobalLayout() {
				mTvDescriptionHeight = mTvDescription.getHeight();
				mTvDescription.getViewTreeObserver()
						.removeGlobalOnLayoutListener(this);
			}
		});

		mTvDescription.setText(description.getDescription());
		// mTvDescription.callOnClick();
	}

	private void setChapterButtons(List<ListRowData> chapters) {
		TableLayout layout = (TableLayout) findViewById(R.id.chapters);

		TableRow row = null;

		Display display = getWindowManager().getDefaultDisplay();
		DisplayMetrics outMetrics = new DisplayMetrics();
		display.getMetrics(outMetrics);

		float density = getResources().getDisplayMetrics().density;
		float dpScreenWidth = outMetrics.widthPixels / density;
		float dpMargin = 10;
		float dpButtonWidth = (dpScreenWidth - 3 * dpMargin) / 2;

		Resources r = getResources();
		float pxWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				dpButtonWidth, r.getDisplayMetrics());
		float pxHeigh = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
				36, r.getDisplayMetrics());
		float pxMargin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				dpMargin, r.getDisplayMetrics());

		mChapterButtons = new Button[chapters.size()];

		for (int i = 0; i < chapters.size(); i++) {
			if (i % 2 == 0) {
				row = new TableRow(this);
				layout.addView(row);
			}

			Button btnChapter = new Button(this);

			TableRow.LayoutParams params = new TableRow.LayoutParams(
					(int) pxWidth, (int) pxHeigh);
			params.setMargins((int) pxMargin, (int) pxMargin / 2, 0,
					(int) pxMargin / 2);

			btnChapter.setLayoutParams(params);
			btnChapter.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
			btnChapter.setText(chapters.get(i).getTitle());
			btnChapter.setOnClickListener(new ChapterClickListener(chapters
					.get(i).getUri()));
			btnChapter.setFocusable(true);

			if (mVolume.isReading(chapters.get(i).getUri())) {
				btnChapter
						.setBackgroundResource(R.drawable.selected_button_style);
				btnChapter.setTextColor(Color.WHITE);
			} else {
				btnChapter.setBackgroundResource(R.drawable.button_style);
			}

			row.addView(btnChapter);
			mChapterButtons[i] = btnChapter;
		}
	}

	private class NovelButtonClickListener implements OnClickListener {
		private final String mNovelUri;

		public NovelButtonClickListener(String novelUri) {
			mNovelUri = novelUri;
		}

		public void onClick(View v) {
			onNovelButtonClicked(mNovelUri);
		}
	}

	private class ChapterClickListener implements OnClickListener {
		private final String mUri;

		public ChapterClickListener(String uri) {
			mUri = uri;
		}

		public void onClick(View v) {
			resetButtons();
			Button button = (Button) v;
			button.setBackgroundResource(R.drawable.selected_button_style);
			button.setTextColor(Color.WHITE);

			if (!mVolume.isReading(mUri)) {
				mVolume.setReading(mUri);
				mVolume.setReadingPosition(0);
			}

			onChapterButtonClicked(mUri, mVolumeUrl);
		}
	}

	private void resetButtons() {
		for (int i = 0; i < mChapterButtons.length; i++) {
			mChapterButtons[i].setBackgroundResource(R.drawable.button_style);
			mChapterButtons[i].setTextColor(Color.BLACK);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_volume);
		initializeView();

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setDisplayShowTitleEnabled(false);

		Intent intent = getIntent();
		mVolumeUrl = intent.getStringExtra(TSONovelURLs.VOLUME_URL_TAG);

		mApp = ApplicationContext.getInstance();
		mTranslator = mApp.getTranslator();
		mBtnNovelIndex = (Button) findViewById(R.id.btn_novel_index);

		mLlVolumeContent = (LinearLayout) findViewById(R.id.content_volume);
		mPbVolumeLoading = (ProgressBar) findViewById(R.id.progress_volume_loading);

		mIvCollapseIndicator = (ImageView) findViewById(R.id.image_collapse_indocator);
	}

	private void initializeView() {
		mNovelName = (TextView) findViewById(R.id.volume_name);
		mAuthor = (TextView) findViewById(R.id.author);
		mUpdateDateTime = (TextView) findViewById(R.id.update_date_time);
		mViewCount = (TextView) findViewById(R.id.view_count);
		mTvDescription = (TextView) findViewById(R.id.volume_description);
		mTvDescription.setOnClickListener(new OnClickListener() {
			private boolean mCollapsed = false;
			private float mCollapsedHeight = spToPx(30);

			public void onClick(View arg0) {
				if (mCollapsed) {
					mTvDescription.setHeight(mTvDescriptionHeight);
					mIvCollapseIndicator
							.setImageResource(R.drawable.navigation_collapse);
					mCollapsed = false;
				} else {
					mTvDescription.setHeight((int) mCollapsedHeight);
					mIvCollapseIndicator
							.setImageResource(R.drawable.navigation_expand);
					mCollapsed = true;
				}
			}
		});

		mVolumeImage = (ImageView) findViewById(R.id.volume_image);
	}

	private float spToPx(float sp) {
		Display display = getWindowManager().getDefaultDisplay();
		DisplayMetrics outMetrics = new DisplayMetrics();
		display.getMetrics(outMetrics);

		Resources r = getResources();
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp,
				r.getDisplayMetrics());
	}
}
