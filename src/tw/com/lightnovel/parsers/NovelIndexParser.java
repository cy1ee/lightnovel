package tw.com.lightnovel.parsers;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import tw.com.lightnovel.apis.Utility;
import tw.com.lightnovel.classes.ListRowData;
import tw.com.lightnovel.classes.NovelDescription;
import tw.com.lightnovel.classes.NovelIndex;
import tw.com.lightnovel.classes.TSONovelURLs;
import tw.com.lightnovel.network.IHttpRequest;
import tw.com.lightnovel.network.NullRequest;

public class NovelIndexParser extends BaseParser {
	public NovelIndexParser(IHttpRequest request) {
		super(request);
	}

	public NovelIndexParser() {
		super(new NullRequest());
	}

	@Override
	public Object parse() {
		return getNovelIndex();
	}

	public NovelIndex getNovelIndex() {
		String content = mRequest.getContent();
		Document doc = Jsoup.parse(content);

		NovelDescription description = parseDescription(doc);

		return new NovelIndex(getNovelName(doc), getImageUrl(doc), description,
				getVolumes(doc));
	}

	/*
	 * public NovelIndex getNovelIndex() { String content =
	 * mRequest.getContent(); SearchResult result = new SearchResult(0, "");
	 * result = getDivBlock(content, result.getIndex(), DISCRIPTION_TAG);
	 * NovelDescription description = parseDescription(translate(result
	 * .getContent()));
	 * 
	 * List<ListRowData> volumes = new ArrayList<ListRowData>();
	 * 
	 * while (true) { result = getDivBlock(content, result.getIndex(),
	 * CHAPTER_TAG);
	 * 
	 * if (result.getIndex() == -1) break;
	 * 
	 * String title = getVolumeTitle(translate(result.getContent())); String
	 * urls[] = getVolume(result.getContent());
	 * 
	 * ListRowData volume = new ListRowData(title, urls[1], urls[0]);
	 * 
	 * volumes.add(volume); }
	 * 
	 * result = new SearchResult(0, ""); result = getDivBlock(content,
	 * result.getIndex(), TITLE_TAG); String titleHtmlBlock =
	 * translate(result.getContent());
	 * 
	 * return new NovelIndex(getNovelName(titleHtmlBlock),
	 * getImageUrl(titleHtmlBlock), description, volumes); }
	 */

	private List<ListRowData> getVolumes(Document doc) {
		List<ListRowData> rows = new ArrayList<ListRowData>();
		Elements ddElems = doc.getElementsByTag("dd");

		for (Element dd : ddElems) {
			Element imgElem = dd.getElementsByTag("img").first();
			Element titleElem = dd.getElementsByTag("a").get(1);

			String title = Utility.formatTitle(translate(titleElem.text()));
			String imgUri = imgElem.attr("src");
			String novelUri = TSONovelURLs.BASE_URL + titleElem.attr("href");
			ListRowData row = new ListRowData(title, imgUri, novelUri);

			rows.add(row);
		}

		return rows;
	}

	private String getNovelName(Document doc) {
		Element titleElem = doc.getElementsByClass("pd-30").first()
				.getElementsByTag("strong").first();

		return translate(titleElem.text());
	}

	private String getImageUrl(Document doc) {
		Element imgElem = doc.getElementsByClass("pd-30").first()
				.getElementsByTag("img").first();

		return imgElem.attr("data-cover");
	}

	private NovelDescription parseDescription(Document doc) {
		Element detailElem = doc.getElementsByClass("lk-book-detail").first();
		Elements tdElements = detailElem.getElementsByTag("td");

		String author = translate(tdElements.get(1).text());
		String iconograph = translate(tdElements.get(3).text());
		String publish = translate(tdElements.get(5).text());
		String view = translate(tdElements.get(7).text());
		String update = translate(tdElements.get(9).text());
		String description = translate(doc.getElementsByTag("p").get(1).text());
		return new NovelDescription(author, iconograph, publish, view, update,
				description);
	}
}
