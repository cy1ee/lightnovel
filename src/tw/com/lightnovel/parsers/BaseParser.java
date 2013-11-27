package tw.com.lightnovel.parsers;

import tw.com.lightnovel.apis.ITranslator;
import tw.com.lightnovel.apis.NullTranslator;
import tw.com.lightnovel.network.IHttpRequest;

public abstract class BaseParser {
	private ITranslator mTranslator = new NullTranslator();
	protected IHttpRequest mRequest;

	public void setTranslator(ITranslator translator) {
		mTranslator = translator;
	}

	protected BaseParser(IHttpRequest request) {
		mRequest = request;
	}
	
	protected String translate(String content) {
		return mTranslator.translate(content);
	}
	
	public void setRequest(IHttpRequest request) {
		mRequest = request;
	}
	
	public Object parse() {
		return null;
	}

	protected SearchResult search(String content, int index, String beginTag,
			String endTag) {
		int beginIndex = index;
		int endIndex = 0;

		beginIndex = content.indexOf(beginTag, beginIndex);

		SearchResult result = null;

		if (beginIndex != -1) {
			beginIndex += beginTag.length();
			endIndex = content.indexOf(endTag, beginIndex);
			result = new SearchResult(endIndex + endTag.length(), content
					.substring(beginIndex, endIndex).trim());
		} else {
			result = new SearchResult(-1, "");
		}

		return result;
	}

	protected SearchResult getDivBlock(String content, int index,
			String targetTag) {
		final String beginTag = "<div";
		final String endTag = "</div>";
		int depth = 1;
		int beginIndex = content.indexOf(targetTag, index);

		if (beginIndex == -1)
			return new SearchResult(-1, "");

		beginIndex += targetTag.length();
		int endIndex = beginIndex;

		while (depth > 0) {
			int i = content.indexOf(beginTag, endIndex);
			int j = content.indexOf(endTag, endIndex);

			if (i == -1 || i > j) {
				endIndex = j + endTag.length();
				depth--;
			}

			if (i < j) {
				endIndex = i + beginTag.length();
				depth++;
			}
		}

		endIndex -= endTag.length();

		return new SearchResult(endIndex + endTag.length(), content.substring(
				beginIndex, endIndex).trim());
	}

	protected class SearchResult {
		private final int mIndex;
		private final String mContent;

		public SearchResult(int index, String content) {
			mIndex = index;
			mContent = content;
		}

		public int getIndex() {
			return mIndex;
		}

		public String getContent() {
			return mContent;
		}
	}
}
