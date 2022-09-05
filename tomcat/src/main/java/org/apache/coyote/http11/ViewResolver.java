package org.apache.coyote.http11;

public class ViewResolver {

    private static final String HTML_EXTENSION = ".html";

    public static String convert(String url) {
        if (!url.contains(".")) {
            return url + HTML_EXTENSION;
        }
        return url;
    }
}
