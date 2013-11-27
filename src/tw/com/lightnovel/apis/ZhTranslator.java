package tw.com.lightnovel.apis;

import java.io.InputStream;

import android.R;
import android.app.Activity;

import tw.com.lightnovel.activities.MainActivity;

public class ZhTranslator implements ITranslator{
	private final Zhcoder mZhEncoder;
	
	public ZhTranslator(Zhcoder zhEncoder) {
		mZhEncoder = zhEncoder;
	}
	
	public String translate(String content) {
		return mZhEncoder.convertString(content, Zhcoder.GB2312, Zhcoder.BIG5);
	}
}
