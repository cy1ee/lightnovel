package tw.com.lightnovel.parsers;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import tw.com.lightnovel.classes.TSONovelURLs;
import tw.com.lightnovel.core.ApplicationContext;
import tw.com.lightnovel.logging.ILogger;
import tw.com.lightnovel.network.IHttpRequest;
import tw.com.lightnovel.network.NullRequest;

public class ChapterParser extends BaseParser {
	private ILogger mLogger = ApplicationContext.Logger.forType(ChapterParser.class);
	
	public ChapterParser(IHttpRequest request) {
		super(request);
	}

	public ChapterParser() {
		super(new NullRequest());
	}

	@Override
	public Object parse() {
		return getLines();
	}

	public List<String> getLines() {
		String content = mRequest.getContent();
		List<String> lines = new ArrayList<String>();
		Document doc = Jsoup.parse(content);
		Elements lineElems = doc.getElementsByClass("lk-view-line");

		for (Element lineElem : lineElems) {
			Elements imgElems = lineElem
					.getElementsByClass("J_lazy");

			if (imgElems.size() == 0) {
				lines.add(getText(lineElem));
			} else {
				lines.add(getImageUri(imgElems));
			}
		}
		
		return lines;
	}

	public String getText(Element elem) {
		String text = elem.text();
		text = text.replace("<br />", "");
		text = translate(text);

		return text;
	}

	public String getImageUri(Elements elems) {
		Element imgElem = elems.first();

		String url = imgElem.attr("data-cover");

		if (url.indexOf("http://") == -1) {
			url = TSONovelURLs.BASE_URL + url;
		}

		mLogger.log(url);
		return url;
	}

	/*
	 * public List<String> getLines() { String content = mRequest.getContent();
	 * List<String> lines = new ArrayList<String>();
	 * 
	 * if (content.equalsIgnoreCase("")) return lines;
	 * 
	 * SearchResult result = search(content, 0, BEGIN_TITLE_TAG, END_TITLE_TAG);
	 * lines.add(translate(result.getContent()));
	 * 
	 * while (true) { result = search(content, result.getIndex(),
	 * BEGIN_LINE_TAG, END_LINE_TAG);
	 * 
	 * if (result.getIndex() == -1) { break; }
	 * 
	 * String line = result.getContent();
	 * 
	 * if (line.indexOf(BEGIN_ILLUSTRATION_TAG) == -1) { line =
	 * result.getContent().replace("<br />", ""); line = translate(line); } else
	 * { line = getImageUrlFromLine(result.getContent()); }
	 * 
	 * lines.add(translate(line)); }
	 * 
	 * return lines; }
	 */
}
