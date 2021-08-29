package nextstep.jwp.framework.http;

import static nextstep.jwp.framework.http.HttpRequest.LINE_DELIMITER;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class HttpHeaders {

    public static final String HEADER_DELIMITER = ":";
    public static final String SPACE = " ";

    private final Map<String, String> headers;

    public HttpHeaders(final String lines) {
        this.headers = convert(lines);
    }

    public HttpHeaders(final Map<String, String> headers) {
        this.headers = headers;
    }

    private Map<String, String> convert(final String lines) {
        final Map<String, String> result = new HashMap<>();
        final String[] headers = lines.split(LINE_DELIMITER);

        for (final String header : headers) {
            final String[] split = header.split(HEADER_DELIMITER);
            result.put(split[0].trim(), split[1].trim());
        }

        return result;
    }

    public int contentLength() {
        return Integer.parseInt(headers.get("Content-Length"));
    }

    public Map<String, String> getHeaders() {
        return Collections.unmodifiableMap(headers);
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();

        for (final String key : headers.keySet()) {
            builder.append(key)
                .append(HEADER_DELIMITER + SPACE)
                .append(headers.get(key))
                .append(LINE_DELIMITER);
        }

        return builder.toString();
    }
}
