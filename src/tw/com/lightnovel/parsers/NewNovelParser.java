package tw.com.lightnovel.parsers;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import tw.com.lightnovel.classes.ListRowData;
import tw.com.lightnovel.classes.TSONovelURLs;
import tw.com.lightnovel.network.IHttpRequest;

public class NewNovelParser extends BaseParser {	
	public NewNovelParser(IHttpRequest request) {
		super(request);
	}

	@Override
	public Object parse() {
		List<ListRowData> novels = null;

		try {
			novels = getNovels();
		} catch (Exception e) {
			novels = new ArrayList<ListRowData>();
		}

		return novels;
	}

	public List<ListRowData> getNovels() {
		ArrayList<ListRowData> novels = new ArrayList<ListRowData>();
		String content = mRequest.getContent();
		Document doc = Jsoup.parse(content);
		Elements animeBlock = doc.getElementsByClass("span8").first()
				.getElementsByTag("ul");

		int count = 0;
		for (Element element : animeBlock) {
			count++;
			
			if (count == 1) continue;
			
			Elements novelsBlock = element.getElementsByClass("lk-block");

			for (Element novelElem : novelsBlock) {
				Element titleNodeElem = novelElem.getElementsByClass(
						"lk-ellipsis").first();
				Element coverImageElem = novelElem.getElementsByClass(
						"J_scoll_load").first();

				ListRowData row = new ListRowData(
						formatTitle(translate(titleNodeElem.text())),
						coverImageElem.attr("data-cover"),
						TSONovelURLs.BASE_URL + titleNodeElem.attr("href"));

				novels.add(row);
			}			
		}
		return novels;
	}

	private String formatTitle(String title) {
		return translate(title.trim().replace("&nbsp;", " "));
	}
}
