package cc.lison.pojo;

import java.nio.charset.Charset;

public class Constants {

	public static final String ENCODING = "UTF-8";

	public static Charset getCharset() {
		return Charset.forName(ENCODING);
	}
}
