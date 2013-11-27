package tw.com.lightnovel.parsers;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import tw.com.lightnovel.classes.ListRowData;
import tw.com.lightnovel.network.IHttpRequest;

public class TSOSearchResultParser extends BaseParser {
	private final String BEGIN_DIV_TAG = "<div class=\"well\">";
	private final String END_DIV_TAG = "</div>";
	private final String BEGIN_HREF_TAG = "href=\"";
	private final String END_HREF_TAG = "\"";
	private final String BEGIN_IMAGE_URL_TAG = "url(";
	private final String END_IMAGE_URL_TAG = ");\"";
	private final String BEGIN_TITLE_TAG = "-title\">";
	private final String END_TITLE_TAG = "</span>";

	public TSOSearchResultParser(IHttpRequest request) {
		super(request);
	}

	public List<ListRowData> getLines() {
		String content = mRequest.getContent();
		ArrayList<ListRowData> captions = new ArrayList<ListRowData>();

		Document doc = Jsoup.parse(content);

		Elements elems = doc.getElementsByClass("lk-book-cover");

		for (Element coverDiv : elems) {
			ListRowData caption = new ListRowData(
					translate(getTitle(coverDiv)), getImageUri(coverDiv),
					getUri(coverDiv));
			
			captions.add(caption);
		}

		/*
		 * SearchResult result = new SearchResult(0, ""); while (true) { result
		 * = search(content, result.getIndex(), BEGIN_HREF_TAG, END_HREF_TAG);
		 * 
		 * if (result.getIndex() == -1) break;
		 * 
		 * String novelUrl = result.getContent(); result = search(content,
		 * result.getIndex(), BEGIN_IMAGE_URL_TAG, END_IMAGE_URL_TAG); String
		 * imageUrl = result.getContent(); result = search(content,
		 * result.getIndex(), BEGIN_TITLE_TAG, END_TITLE_TAG); String title =
		 * result.getContent().replace("&nbsp;", " ");
		 * 
		 * ListRowData caption = new ListRowData(translate(title), imageUrl,
		 * novelUrl); captions.add(caption); }
		 */
		return captions;
	}

	private String getImageUri(Element elem) {
		Element img = elem.getElementsByTag("img").first();

		return img.attr("data-cover");
	}

	private String getTitle(Element elem) {
		Element img = elem.getElementsByTag("img").first();

		return img.attr("alt");
	}

	private String getUri(Element elem) {
		Element a = elem.getElementsByTag("a").first();

		return a.attr("href");
	}

	private String getNovelListContent(String content) {
		int beginIndex = content.indexOf(BEGIN_DIV_TAG)
				+ BEGIN_DIV_TAG.length();
		int endIndex = content.indexOf(END_DIV_TAG, beginIndex);

		return content.substring(beginIndex, endIndex).trim();
	}
}
