package nextstep.jwp.http.response;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import nextstep.jwp.http.ContentType;
import nextstep.jwp.http.HttpCookie;
import nextstep.jwp.http.Location;

public class HttpResponseHeaders {

    private static final String HTTP_RESPONSES_DELIMITER = "\r\n";
    private static final String HTTP_RESPONSE_KEY_VALUE_DELIMITER = ": ";

    private final Map<String, String> values;
    private final Location location;
    private final ContentType contentType;
    private final HttpCookie httpCookie;

    public HttpResponseHeaders(final Map<String, String> values, final Location location, final ContentType contentType,
                               final HttpCookie httpCookie) {
        this.values = values;
        this.location = location;
        this.contentType = contentType;
        this.httpCookie = httpCookie;
    }

    public HttpResponseHeaders(final Location location, final ContentType contentType, final HttpCookie httpCookie) {
        this(new ConcurrentHashMap<>(), location, contentType, httpCookie);
    }

    public void addHeader(final String key, final String value) {
        values.put(key, value);
    }

    public String toHeaderFormat() {
        String response = contentType.toHeaderFormat();
        response = joinValues(response);
        response = joinLocation(response);
        response = joinCookie(response);
        return response;
    }

    private String joinValues(final String response) {
        if (values.isEmpty()) {
            return response;
        }
        return joinResponseFormat(response, valuesHeaderFormat());
    }

    private String valuesHeaderFormat() {
        return values.entrySet()
                .stream()
                .map(value -> value.getKey() + HTTP_RESPONSE_KEY_VALUE_DELIMITER + value.getValue() + " ")
                .collect(Collectors.joining(HTTP_RESPONSES_DELIMITER));
    }

    private String joinLocation(final String response) {
        if (location.isEmpty()) {
            return response;
        }
        return joinResponseFormat(response, location.toHeaderFormat());
    }

    private String joinCookie(final String response) {
        if (httpCookie.isEmpty()) {
            return response;
        }
        return joinResponseFormat(response, httpCookie.toHeaderFormat());
    }

    private String joinResponseFormat(final String ... response) {
        return String.join(HTTP_RESPONSES_DELIMITER, response);
    }
}
