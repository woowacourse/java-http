package org.apache.coyote.http;

import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.request.line.Method;
import org.apache.coyote.http.request.line.RequestLine;
import org.apache.coyote.http.request.line.Uri;

public class RequestResponseFixture {

    private static final RequestLine INDEX_REQUEST_LINE = new RequestLine(Method.GET, new Uri("/index.html"),
            HttpProtocol.HTTP_11);
    private static final HttpHeaders EMPTY_HEADER = new HttpHeaders();
    private static final HttpMessageBody BODY = HttpMessageBody.createEmptyBody();
    public static final HttpRequest INDEX_REQUEST = new HttpRequest(INDEX_REQUEST_LINE, EMPTY_HEADER, BODY);
}
