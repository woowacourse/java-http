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

    private final String httpContentType;
    private final String extension;

    private static final Map<String, ContentType> contentTypes =
            Collections.unmodifiableMap(Stream.of(values())
                    .collect(Collectors.toMap(ContentType::getExtension, Function.identity()))
            );

    ContentType(String httpContentType, String extension) {
        this.httpContentType = httpContentType;
        this.extension = extension;
    }

    public static ContentType findMatchingType(String extension) {
        return contentTypes.getOrDefault(extension, HTML);
    }

    public String getHttpContentType() {
        return httpContentType;
    }

    public String getExtension() {
        return extension;
    }
}
