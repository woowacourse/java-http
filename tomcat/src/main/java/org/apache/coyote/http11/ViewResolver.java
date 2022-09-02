package org.apache.coyote.http11;

public class ViewResolver {

    public static String convert(String url) {
        if (url.contains("?")) {
            url = url.substring(0, url.indexOf("?"));
        }
        if (!url.contains(".")) {
            return url + ".html";
        }
        return url;
    }
}
