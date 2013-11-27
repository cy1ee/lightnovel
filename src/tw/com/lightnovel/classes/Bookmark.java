package tw.com.lightnovel.classes;


public class Bookmark {
	private String mVolume;
	private String mChapter;
	private int mLineAt;

	public Bookmark() {
		this("null volume", "null chapter");
	}
	
	public Bookmark(String volume, String chapter) {
		mVolume = volume;
		mChapter = chapter;
		mLineAt = 0;
	}
	
	public Bookmark(String volume, String chapter, int lineAt) {
		mVolume = volume;
		mChapter = chapter;
		mLineAt = lineAt;
	}
	
	public void setVolume(String volume) {
		mVolume = volume;
	}
	
	public String getVolume() {
		return mVolume;
	}
	
	public void setChapter(String chapter) {
		mChapter = chapter;
	}
	
	public String getChapter() {
		return mChapter;
	}
	
	public void setLine(int lineAt) {
		mLineAt = lineAt;
	}
	
	public int getLine() {
		return mLineAt;
	}
}
