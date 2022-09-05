package org.apache.coyote.http11.message.request;

import lombok.Getter;
import lombok.ToString;
import org.apache.coyote.http11.message.common.HttpMethod;

@ToString
@Getter
public class RequestLine {

    private static final String START_LINE_DELIMITER = " ";

    private static final int METHOD_INDEX = 0;
    private static final int REQUEST_URI_INDEX = 1;
    private static final int VERSION_INDEX = 2;

    private final HttpMethod method;
    private final RequestUri requestUri;
    private final String version;

    public RequestLine(final String requestLine) {
        String[] splitRequestLine = requestLine.split(START_LINE_DELIMITER);
        this.method = HttpMethod.from(splitRequestLine[METHOD_INDEX]);
        this.requestUri = new RequestUri(splitRequestLine[REQUEST_URI_INDEX]);
        this.version = splitRequestLine[VERSION_INDEX];
    }

    public boolean hasQuery() {
        return requestUri.hasQuery();
    }
}
