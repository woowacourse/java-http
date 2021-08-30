package nextstep.jwp.framework.http;

import static nextstep.jwp.framework.http.HttpRequest.LINE_DELIMITER;

import java.util.HashMap;
import java.util.Map;

public class HttpHeaders {

    public static final String HEADER_DELIMITER = ":";
    public static final String SPACE = " ";
    private static final int HEADER_KEY_INDEX = 0;
    private static final int VALUE_KEY_INDEX = 1;

    private final Map<String, String> headers;

    public HttpHeaders(final String lines) {
        this(convert(lines));
    }

    public HttpHeaders(final Map<String, String> headers) {
        this.headers = headers;
    }

    private static Map<String, String> convert(final String lines) {
        final Map<String, String> result = new HashMap<>();
        final String[] headers = lines.split(LINE_DELIMITER);

        for (final String header : headers) {
            final String[] split = header.split(HEADER_DELIMITER);
            result.put(split[HEADER_KEY_INDEX].trim(), split[VALUE_KEY_INDEX].trim());
        }

        return result;
    }

    public int contentLength() {
        return Integer.parseInt(headers.get("Content-Length"));
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
