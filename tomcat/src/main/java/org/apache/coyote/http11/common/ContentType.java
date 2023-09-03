package org.apache.coyote.http11.common;

import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum ContentType {

    HTML("text/html;charset=utf-8", "html"),
    CSS("text/css", "css"),
    JS("text/javascript", "js");

    private final String contentType;
    private final String matchingType;

    ContentType(String contentType, String matchingType) {
        this.contentType = contentType;
        this.matchingType = matchingType;
    }

    private static final Map<String, ContentType> contentTypes =
            Collections.unmodifiableMap(Stream.of(values())
                    .collect(Collectors.toMap(ContentType::getMatchingType, Function.identity()))
            );

    public static ContentType findMatchingType(String uri) {
        int fileTypeStartIndex = uri.indexOf('.');
        String uriType = uri.substring(fileTypeStartIndex + 1);

        if (contentTypes.containsKey(uriType)) {
            return contentTypes.get(uriType);
        }
        return contentTypes.get(HTML.matchingType);
    }

    public String getContentType() {
        return contentType;
    }

    public String getMatchingType() {
        return matchingType;
    }
}
