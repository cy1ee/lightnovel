package tw.com.lightnovel.activities;

import java.util.List;

import tw.com.lightnovel.adapters.VolumListAdapter;
import tw.com.lightnovel.apis.ITranslator;
import tw.com.lightnovel.apis.NovelDownloader;
import tw.com.lightnovel.classes.ListRowData;
import tw.com.lightnovel.classes.NovelDescription;
import tw.com.lightnovel.classes.NovelIndex;
import tw.com.lightnovel.classes.TSONovelURLs;
import tw.com.lightnovel.core.ApplicationContext;
import tw.com.lightnovel.fragments.MyCabinetFragment.ListDialogFragment;
import tw.com.lightnovel.image.HttpImageManager;
import tw.com.lightnovel.image.ImageHandlerCode;
import tw.com.lightnovel.image.ImageManager;
import tw.com.lightnovel.image.ImageUtil;
import tw.com.lightnovel.logging.ILogger;
import tw.com.thinkso.novelreaderapp.R;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.ActionMode;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class BaseNovelIndexActivity extends Activity {	
	protected String mUrl;
	protected ITranslator mTranslator;
	protected VolumListAdapter mListAdapter;
	protected ImageManager mImageManager;

	private ILogger mLogger = ApplicationContext.Logger
			.forType(BaseNovelIndexActivity.class);

	private TextView mNovelName;
	private TextView mAuthor;
	private TextView mUpdateDateTime;
	private TextView mViewCount;
	private TextView mTvDescription;
	private ImageView mNovelImage;
	private ImageView mIvCollapseIndicator;
	private ListView mVolumesList;
	private View mPbNovelLoading;
	private View mLlNovelIndexContent;

	protected Button mBtnKeepNovel;

	private List<ListRowData> mVolumes;
	private NovelIndex mNovelIndex;
	private ApplicationContext mApp;

	private ActionMode mActionMode;
	private int mShortAnimationDuration;
	private int mTvDescriptionHeight;
	private Activity mActivity;
	
	public class NovelIndexHandler extends Handler {
		private static final int SHOW_ALERT_ID = 10;
		
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {

			case ImageHandlerCode.DOWNLOAD_FINISH:
				mNovelImage.setImageBitmap(ImageUtil
						.imageFromBytes((byte[]) msg.obj));
				break;
				
			case SHOW_ALERT_ID:
				AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
				builder.setMessage((String)msg.obj).create().show();
				break;
			}			
		};
		
		public void showAlert(String s) {
			mLogger.log(s);
			Message msg = obtainMessage(SHOW_ALERT_ID, s);
			sendMessage(msg);
		}
	}

	private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

		// Called when the action mode is created; startActionMode() was called
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			// Inflate a menu resource providing context menu items
			MenuInflater inflater = mode.getMenuInflater();
			inflater.inflate(R.menu.novel_index_download_mode_action, menu);
			return true;
		}

		// Called each time the action mode is shown. Always called after
		// onCreateActionMode, but
		// may be called multiple times if the mode is invalidated.
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			return false; // Return false if nothing is done
		}

		// Called when the user selects a contextual menu item
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			switch (item.getItemId()) {
			case R.id.novel_action_save:
				NovelDownloader downloader = new NovelDownloader(mActivity,
						mUrl, mListAdapter.getSelectedRowContent(), mHandler);
				// return true;

				// mode.finish(); // Action picked, so close the CAB
				return true;
			default:
				return false;
			}
		}

		// Called when the user exits the action mode
		public void onDestroyActionMode(ActionMode mode) {
			mActionMode = null;
			if (mListAdapter != null) {
				mListAdapter.checkAllRow(false);
			}
		}
	};

	private NovelIndexHandler mHandler = new NovelIndexHandler();

	private void setList(List<ListRowData> novels) {
		mListAdapter = new VolumListAdapter(BaseNovelIndexActivity.this,
				novels, mImageManager);
		mVolumesList.setAdapter(mListAdapter);
	}

	private void setIndexDescription(NovelIndex novel) {
		NovelDescription description = novel.getDescription();

		String imageUrl = novel.getImageUri();

		if (mImageManager.IsCache(imageUrl)) {
			Bitmap bitmap = mImageManager.getImg(imageUrl);
			mNovelImage.setImageBitmap(bitmap);
		} else {
			mImageManager.LoadPic(imageUrl, mHandler);
		}

		mNovelName.setText(novel.getName());
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
	}

	public void showContextualActionBar(boolean isShow) {
		if (isShow) {
			if (mActionMode == null) {
				mActionMode = startActionMode(mActionModeCallback);
			}
		} else {
			if (mActionMode != null) {
				mActionMode.finish();
			}
		}
	}

	private OnItemClickListener mListItemClickListener = new OnItemClickListener() {

		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {

			if (mVolumes == null)
				return;

			ListRowData volume = mVolumes.get(arg2);

			readVolume(volume.getUri());
		}
	};

	protected void readVolume(String uri) {

	}

	private OnClickListener mOnAddToCabinetClickListener = new OnClickListener() {

		public void onClick(View v) {
			if (mNovelIndex != null) {
				ListRowData novelRecord = new ListRowData(
						mNovelIndex.getName(), mNovelIndex.getImageUri(), mUrl);
				mApp.addNovelRecord(novelRecord);

				Toast.makeText(getApplicationContext(),
						getString(R.string.toast_add_to_mycabinet),
						Toast.LENGTH_SHORT).show();
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_novel_index);
		initializeView();

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setDisplayShowTitleEnabled(false);

		Intent intent = getIntent();
		mUrl = intent.getStringExtra(TSONovelURLs.NOVEL_URL_TAG);

		mActivity = this;
		mApp = ApplicationContext.getInstance();
		mTranslator = mApp.getTranslator();
		mImageManager = new HttpImageManager();

		mBtnKeepNovel = (Button) findViewById(R.id.btn_keep_novel);
		mBtnKeepNovel.setOnClickListener(mOnAddToCabinetClickListener);

		mPbNovelLoading = findViewById(R.id.progress_novel_loading);
		mLlNovelIndexContent = findViewById(R.id.content_novel_index);
		mShortAnimationDuration = getResources().getInteger(
				android.R.integer.config_shortAnimTime);

		mIvCollapseIndicator = (ImageView) findViewById(R.id.image_collapse_indocator);
	}

	private void crossfade() {
		mLlNovelIndexContent.setAlpha(0f);
		mLlNovelIndexContent.setVisibility(View.VISIBLE);

		mLlNovelIndexContent.animate().alpha(1f)
				.setDuration(mShortAnimationDuration).setListener(null);

		mPbNovelLoading.animate().alpha(0f)
				.setDuration(mShortAnimationDuration)
				.setListener(new AnimatorListenerAdapter() {
					@Override
					public void onAnimationEnd(Animator animation) {
						mPbNovelLoading.setVisibility(View.GONE);
					}
				});
	}

	protected void setNovelIndex(NovelIndex index) {
		setIndexDescription(index);

		mNovelIndex = index;
		mVolumes = index.getVolumes();
		crossfade();
		setList(mVolumes);
	}

	private void initializeView() {
		mNovelName = (TextView) findViewById(R.id.novel_name);
		mAuthor = (TextView) findViewById(R.id.author);
		mUpdateDateTime = (TextView) findViewById(R.id.update_date_time);
		mViewCount = (TextView) findViewById(R.id.view_count);

		mTvDescription = (TextView) findViewById(R.id.novel_description);
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

		mNovelImage = (ImageView) findViewById(R.id.index_image);
		mVolumesList = (ListView) findViewById(R.id.vol_list);
		mVolumesList.setOnItemClickListener(mListItemClickListener);
	}

	private float spToPx(float sp) {
		Display display = getWindowManager().getDefaultDisplay();
		DisplayMetrics outMetrics = new DisplayMetrics();
		display.getMetrics(outMetrics);

		Resources r = getResources();
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp,
				r.getDisplayMetrics());
	}

	public static class AlertDialogFragment extends DialogFragment {

		public static ListDialogFragment newInstance(String msg) {
			ListDialogFragment fragment = new ListDialogFragment();
			Bundle args = new Bundle();
			args.putString("msg", msg);
			fragment.setArguments(args);

			return fragment;
		}

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			final String msg = getArguments().getString("msg");

			return new AlertDialog.Builder(getActivity()).setTitle(msg)
					.create();
		}
	}
}
