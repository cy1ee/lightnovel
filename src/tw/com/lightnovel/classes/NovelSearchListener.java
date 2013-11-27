package tw.com.lightnovel.classes;

import tw.com.lightnovel.activities.SearchResultActivity;
import android.content.Context;
import android.content.Intent;
import android.widget.SearchView.OnQueryTextListener;

public class NovelSearchListener implements OnQueryTextListener {
	public static final String SEARCH_CONTENT_TAG = "search_content";
	
	private final Context mContext;
	
	public NovelSearchListener(Context context) {
		mContext = context;
	}

	public boolean onQueryTextChange(String arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean onQueryTextSubmit(String searchContent) {
		Intent intent = new Intent(mContext, SearchResultActivity.class);
		intent.putExtra(SEARCH_CONTENT_TAG, searchContent);
		mContext.startActivity(intent);
		return false;
	}

}
