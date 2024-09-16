package org.apache.coyote.http;

import static org.apache.coyote.http.HttpProtocol.HTTP_11;
import static org.apache.coyote.http.request.line.Method.GET;

import org.apache.coyote.http.request.line.RequestLine;
import org.apache.coyote.http.request.line.Uri;

public class HttpFixture {

    public static final RequestLine INDEX_REQUEST_LINE = new RequestLine(GET, new Uri("/index.html"), HTTP_11);
    public static final RequestLine NO_RESOURCE_LINE = new RequestLine(GET, new Uri("/no.html"), HTTP_11);
    public static final RequestLine HOME_REQUEST_LINE = new RequestLine(GET, new Uri("/"), HTTP_11);
    public static final HttpHeaders EMPTY_HEADER = new HttpHeaders();
    public static final HttpMessageBody EMPTY_BODY = HttpMessageBody.createEmptyBody();
}
