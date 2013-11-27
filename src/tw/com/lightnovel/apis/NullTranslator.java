package tw.com.lightnovel.apis;

public class NullTranslator implements ITranslator {

	public String translate(String content) {
		return content;
	}

}
