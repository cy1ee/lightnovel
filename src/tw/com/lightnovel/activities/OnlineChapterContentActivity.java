package tw.com.lightnovel.activities;

import java.util.List;

import tw.com.lightnovel.core.HttpRequestTask;
import tw.com.lightnovel.image.HttpImageManager;
import tw.com.lightnovel.network.TSOHttpPost;
import tw.com.lightnovel.parsers.BaseParser;
import tw.com.lightnovel.parsers.ChapterParser;
import android.os.AsyncTask.Status;
import android.os.Bundle;

public class OnlineChapterContentActivity extends BaseChapterContentActivity {
	private HttpRequestTask<List<String>> mTask;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mImageManager = new HttpImageManager();

		loadChapterContent(mUri);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		if (mTask != null && mTask.getStatus() == Status.RUNNING) {
			mTask.cancel(true);
		}
	}

	@Override
	protected void loadChapterContent(String uri) {
		TSOHttpPost post = new TSOHttpPost(uri, null);
		BaseParser parser = new ChapterParser(post);

		parser.setTranslator(mTranslator);
		mTask = new HttpRequestTask<List<String>>();
		mTask.setOnRequestPostExecuteListener(new HttpRequestTask.OnRequestPostExecuteListener<List<String>>() {

			public void onRequestPostExecuted(List<String> result) {
				notifyChapterContentLoaded(result);
			}
		});

		mTask.execute(parser);
	}
}
