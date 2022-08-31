package org.utils;

public class RequestLineParser {

    private static final int URL = 1;
    private static final String STATIC_RESOURCE_PATH = "static";

    public static String getStaticResourceUrl(final String requestLine) {
        return STATIC_RESOURCE_PATH + requestLine.split(" ")[URL];
    }
}
