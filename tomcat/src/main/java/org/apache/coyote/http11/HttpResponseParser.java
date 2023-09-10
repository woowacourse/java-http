package org.apache.coyote.http11;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestLine;
import org.apache.coyote.http11.response.HttpResponse;

public class HttpResponseParser {
    public static HttpResponse extract(HttpRequest httpRequest) {
        RequestLine requestLine = httpRequest.getRequestLine();

        return new HttpResponse(requestLine.getHttpVersion());
    }

}
