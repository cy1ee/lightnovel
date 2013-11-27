package tw.com.lightnovel.network;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.util.Log;

public class TSOHttpPost implements IHttpRequest {
	private final String mUrl;
	private final List<NameValuePair> mParams;

	public String getContent() {
		return post();
	}

	public TSOHttpPost(String url, List<NameValuePair> param) {
		mUrl = url;

		if (param != null)
			mParams = param;
		else {
			mParams = new ArrayList<NameValuePair>();
		}
	}

	private String post() {
		StringBuilder sb = new StringBuilder();

		try {
			HttpPost request = new HttpPost(mUrl);
			UrlEncodedFormEntity form = new UrlEncodedFormEntity(mParams,
					"UTF-8");
			request.setEntity(form);

			HttpParams httpParameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters, 6000);

			HttpClient client = new DefaultHttpClient(httpParameters);
			HttpResponse response = client.execute(request);

			InputStream resStream = response.getEntity().getContent();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					resStream, "UTF-8"));

			char[] buffer = new char[4096];
			int len = 0;

			while ((len = reader.read(buffer, 0, 4096)) != -1) {
				sb.append(buffer, 0, len);
			}
		} catch (Exception e) {
			Log.d("Exception", e.getMessage());
		}

		return sb.toString();
	}
}
