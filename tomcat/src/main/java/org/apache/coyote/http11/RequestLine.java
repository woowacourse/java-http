package org.apache.coyote.http11;

import java.util.List;
import org.apache.coyote.http11.exception.RequestException;

public class RequestLine {

    private static final int REQUEST_METHOD_INDEX = 0;
    private static final int REQUEST_URL_INDEX = 1;
    private static final int REQUEST_VERSION_INDEX = 2;
    private static final String REQUEST_LINE_DELIMITER = " ";

    private final String method;
    private final String url;
    private final String version;

    public RequestLine(String rawRequestLine) {
        List<String> requestPart = List.of(rawRequestLine.split(REQUEST_LINE_DELIMITER));
        validateRequestPartLength(requestPart);
        this.method = requestPart.get(REQUEST_METHOD_INDEX);
        this.url = requestPart.get(REQUEST_URL_INDEX);
        this.version = requestPart.get(REQUEST_VERSION_INDEX);
    }

    private void validateRequestPartLength(List<String> requestPart) {
        if (requestPart.size() != 3) {
            throw new RequestException(HttpStatusCode.BAD_REQUEST, "/500.html");
        }
    }

    public String getMethod() {
        return method;
    }

    public String getUrl() {
        return url;
    }
}
