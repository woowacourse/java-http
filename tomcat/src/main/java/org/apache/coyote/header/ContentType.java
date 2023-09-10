package org.apache.coyote.header;

public class ContentType {

    public static final String TEXT_HTML = "text/html";
    public static final String TEXT_CSS = "text/css";
    public static final String CHARSET_UTF_8 = "charset=utf-8";
    public static final String TEXT_HTML_CHARSET_UTF_8 = TEXT_HTML + ";" + CHARSET_UTF_8;

    public static String negotiate(String requestUrl) {
        if (requestUrl.contains(".css")) {
            return TEXT_CSS;
        }
        return TEXT_HTML_CHARSET_UTF_8;
    }
}
