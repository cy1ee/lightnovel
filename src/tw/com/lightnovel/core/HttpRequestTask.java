package tw.com.lightnovel.core;

import tw.com.lightnovel.parsers.NewNovelParser;
import tw.com.lightnovel.parsers.BaseParser;
import tw.com.lightnovel.parsers.AnimaxNovelParser;
import android.os.AsyncTask;

public class HttpRequestTask <T> extends AsyncTask<BaseParser, Integer, T>{
	private OnRequestPostExecuteListener<T> mPostExecuted;

	@Override
	protected T doInBackground(BaseParser... parsers) {
		BaseParser parser = parsers[0];		
		return (T)parser.parse();
	}
	
	@Override
	protected void onPostExecute(T result) {
		// TODO Auto-generated method stub
		if (mPostExecuted != null) {
			mPostExecuted.onRequestPostExecuted(result);
		}
	}
	
	public void setOnRequestPostExecuteListener(OnRequestPostExecuteListener<T> postEcecutedListener){
		mPostExecuted = postEcecutedListener;
	}
	
	public interface OnRequestPostExecuteListener <T> {
		void onRequestPostExecuted(T result);
	}
}
