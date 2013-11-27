package tw.com.lightnovel.parsers;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import tw.com.lightnovel.apis.Utility;
import tw.com.lightnovel.classes.ListRowData;
import tw.com.lightnovel.classes.TSONovelURLs;
import tw.com.lightnovel.classes.Volume;
import tw.com.lightnovel.classes.VolumeDescription;
import tw.com.lightnovel.core.ApplicationContext;
import tw.com.lightnovel.logging.ILogger;
import tw.com.lightnovel.network.IHttpRequest;
import tw.com.lightnovel.network.NullRequest;

public class VolumeParser extends BaseParser {
	private ILogger mLogger = ApplicationContext.Logger.forType(VolumeParser.class);

	public VolumeParser(IHttpRequest request) {
		super(request);
	}

	public VolumeParser() {
		super(new NullRequest());
	}

	@Override
	public Object parse() {
		return getVolume();
	}

	public Volume getVolume() {
		String content = mRequest.getContent();
		Document doc = Jsoup.parse(content);

		String novelUrl = getMainNovelUrl(doc);	
		String imageUrl = getImageUri(doc);		
		VolumeDescription description = getDescription(doc);
		List<ListRowData> chapters = getChapter(doc);

		return new Volume(description, novelUrl, imageUrl, chapters);
	}

	private VolumeDescription getDescription(Document doc) {
		Element detailElem = doc.getElementsByClass("lk-book-detail").first();
		Elements tdElems = detailElem.getElementsByTag("td");

		String title = translate(tdElems.get(1).text());
		String author = translate(tdElems.get(3).text());
		String iconograph = translate(tdElems.get(5).text());
		String publish = translate(tdElems.get(7).text());
		String viewCount = translate(tdElems.get(9).text());
		String updateDateTime = translate(tdElems.get(11).text());
		String description = translate(doc.getElementsByTag("p").get(1).text());

		return new VolumeDescription(title, author, iconograph, publish,
				viewCount, updateDateTime, description);
	}

	private String getImageUri(Document doc) {
		Element imgUriElem = doc.getElementsByClass("lk-book-cover").first()
				.getElementsByTag("img").first();

		return TSONovelURLs.BASE_URL + imgUriElem.attr("src");
	}

	private List<ListRowData> getChapter(Document doc) {		
		List<ListRowData> chapters = new ArrayList<ListRowData>();
		Element chapterListElem = doc.getElementsByClass("lk-chapter-list").first();
		Elements liElems = chapterListElem.getElementsByTag("li");
		
		for (Element liElem : liElems) {
			Element aElem = liElem.getElementsByTag("a").first();
			
			String title = Utility.getFixLenghString(translate(aElem.text()));
			String novelUri = TSONovelURLs.BASE_URL + aElem.attr("href");
			
			chapters.add(new ListRowData(title, "", novelUri));
		}		

		return chapters;
	}

	private String getMainNovelUrl(Document doc) {
		Element mainUriElem = doc.getElementsByClass("breadcrumb").first().getElementsByTag("a").get(1);

		return TSONovelURLs.BASE_URL + mainUriElem.attr("href");
	}
}
