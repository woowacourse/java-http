package org.apache.coyote.request;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.coyote.exception.http.InvalidHeaderException;

public class HttpRequestHeaders {

    private static final String DELIMITER = ": ";
    private static final int ELEMENT_COUNT = 2;

    private final Map<String, String> headers;

    private HttpRequestHeaders(final Map<String, String> headers) {
        this.headers = headers;
    }

    public static HttpRequestHeaders parse(final List<String> lines) {
        final Map<String, String> headers = lines.stream()
                .map(line -> {
                    final String[] splitHeaders = line.split(DELIMITER);
                    if (splitHeaders.length != ELEMENT_COUNT) {
                        throw new InvalidHeaderException("Header Element Count Not Match");
                    }
                    return splitHeaders;
                })
                .collect(Collectors.toMap(
                        header -> header[0],
                        header -> header[1],
                        (exist, replace) -> replace,
                        LinkedHashMap::new
                ));
        return new HttpRequestHeaders(headers);
    }

    public String getHeader(final String name) {
        return headers.get(name);
    }

    public HttpCookie getCookie() {
        return HttpCookie.from(headers.get("Cookie"));
    }
}
