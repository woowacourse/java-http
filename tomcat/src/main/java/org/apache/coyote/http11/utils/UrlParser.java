package org.apache.coyote.http11.utils;

import static org.apache.coyote.http11.response.ContentType.TEXT_HTML;

import org.apache.coyote.http11.request.HttpMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UrlParser {
    private static final Logger log = LoggerFactory.getLogger(UrlParser.class);

    public static HttpMethod extractMethod(final String httpRequest) {
        String method = httpRequest.split(" ")[0];
        return HttpMethod.valueOf(method);
    }

    public static String extractUri(final String httpRequest) {
        return httpRequest.split(" ")[1];
    }

    public static String convertEmptyToHtml(String url) {
        log.info("url : {}", url);
        String resource = url;
        int index = url.indexOf(".");
        if (index == -1) {
            resource = url + "." + TEXT_HTML.getExtension();
        }
        return resource;
    }
}
