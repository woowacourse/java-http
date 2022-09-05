package nextstep.jwp.http;

import org.apache.http.HttpHeader;
import org.apache.http.HttpMime;

import java.util.LinkedHashMap;
import java.util.Map;

public class Headers {

    private static final String DEFAULT_CONTENT_LENGTH = "0";

    private final Map<HttpHeader, String> mapping = new LinkedHashMap<>();

    public Headers() {
        mapping.put(HttpHeader.CONTENT_TYPE, HttpMime.DEFAULT.getValue());
        mapping.put(HttpHeader.CONTENT_LENGTH, DEFAULT_CONTENT_LENGTH);
    }

    public void put(final HttpHeader key, final String value) {
        mapping.put(key, value);
    }

    public String parse() {
        final StringBuilder builder = new StringBuilder();
        mapping.forEach((key, value) ->
                builder.append(String.format("%s: %s \r%n", key.getValue(), value)));
        return builder.toString();
    }
}
