package tw.com.lightnovel.activities;

import java.util.List;

import tw.com.lightnovel.image.LocalImageManager;
import tw.com.lightnovel.loader.ChapterContentLoader;
import android.os.Bundle;

public class LocalChapterContentActivity extends BaseChapterContentActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mImageManager = new LocalImageManager();

		loadChapterContent(mUri);
	}

	@Override
	protected void loadChapterContent(String uri) {
		ChapterContentLoader loader = new ChapterContentLoader(uri);
		List<String> lines = loader.load();
		notifyChapterContentLoaded(lines);

	}
}
