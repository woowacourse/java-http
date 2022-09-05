package org.apache.coyote.http11.message.request;

import lombok.Getter;
import lombok.ToString;
import org.apache.coyote.http11.message.common.HttpMethod;
import org.apache.coyote.http11.message.common.HttpVersion;

@ToString
@Getter
public class RequestLine {

    private static final String START_LINE_DELIMITER = " ";

    private static final int METHOD_INDEX = 0;
    private static final int REQUEST_URI_INDEX = 1;
    private static final int VERSION_INDEX = 2;

    private final HttpMethod method;
    private final RequestUri uri;
    private final HttpVersion version;

    private RequestLine(final HttpMethod method, final RequestUri uri, final HttpVersion version) {
        this.method = method;
        this.uri = uri;
        this.version = version;
    }

    public static RequestLine parse(final String rawRequestLine) {
        String[] splitRequestLine = rawRequestLine.split(START_LINE_DELIMITER);

        HttpMethod method = HttpMethod.from(splitRequestLine[METHOD_INDEX]);
        RequestUri uri = new RequestUri(splitRequestLine[REQUEST_URI_INDEX]);
        HttpVersion version = HttpVersion.from(splitRequestLine[VERSION_INDEX]);

        return new RequestLine(method, uri, version);
    }

    public boolean hasQuery() {
        return uri.hasQuery();
    }
}
