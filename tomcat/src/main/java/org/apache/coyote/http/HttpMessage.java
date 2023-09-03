package org.apache.coyote.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

public class HttpMessage {

    private static final Logger log = LoggerFactory.getLogger(HttpMessage.class);

    private final ContentType contentType;
    private final String content;
    private final Map<String, String> parameters;

    private HttpMessage(ContentType contentType, String content, Map<String, String> parameters) {
        this.contentType = contentType;
        this.content = content;
        this.parameters = parameters;
    }

    public static HttpMessage of(byte[] source, String contentTypeString) {
        String content = new String(source);
        ContentType contentType = ContentType.from(contentTypeString);
        return HttpMessage.of(content, contentType);
    }

    public static HttpMessage of(String content, ContentType contentType) {
        log.info("body: {}", content);
        Map<String, String> parameters = handleFormData(content, contentType);
        return new HttpMessage(contentType, content, parameters);
    }

    private static Map<String, String> handleFormData(String content, ContentType contentType) {
        if (contentType != ContentType.APPLICATION_X_WWW_FORM_URL_ENCODED) {
            return Collections.emptyMap();
        }
        return Arrays.stream(content.split("&"))
                .map(queryParam -> queryParam.split("="))
                .filter(arr -> arr.length >= 2)
                .collect(toMap(keyAndValue -> keyAndValue[0], keyAndValue -> keyAndValue[1]));
    }

    public ContentType getContentType() {
        return contentType;
    }

    public String getContent() {
        return content;
    }

    public Map<String, String> getParameters() {
        return Map.copyOf(parameters);
    }

    public enum ContentType {

        TEXT_HTML("text/html", "html"),
        TEXT_CSS("text/css", "css"),
        TEXT_JAVASCRIPT("text/javascript", "js"),
        APPLICATION_X_WWW_FORM_URL_ENCODED("application/x-www-form-urlencoded", "application/x-www-form-urlencoded"),
        ;

        public final String value;
        public final String identifier;

        ContentType(String value, String identifier) {
            this.value = value;
            this.identifier = identifier;
        }

        public static ContentType from(String value) {
            return Arrays.stream(values())
                    .filter(contentType -> value.endsWith(contentType.identifier))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException());
        }
    }
}
