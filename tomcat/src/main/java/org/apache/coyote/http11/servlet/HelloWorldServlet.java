package org.apache.coyote.http11.servlet;

import org.apache.coyote.http11.common.ContentType;
import org.apache.coyote.http11.common.HttpHeaderName;
import org.apache.coyote.http11.common.HttpHeaders;
import org.apache.coyote.http11.common.request.HttpRequest;
import org.apache.coyote.http11.common.response.HttpResponse;
import org.apache.coyote.http11.common.response.StatusCode;

public class HelloWorldServlet implements Servlet {

    public static final String HELLO_WORLD = "Hello world!";

    @Override
    public HttpResponse handle(final HttpRequest request) {
        String body = HELLO_WORLD;

        HttpHeaders headers = new HttpHeaders();
        headers.addHeader(HttpHeaderName.CONTENT_TYPE, ContentType.TEXT_PLAIN.getDetail());
        headers.addHeader(HttpHeaderName.CONTENT_LENGTH, String.valueOf(body.getBytes().length));

        return HttpResponse.create(StatusCode.OK, headers, body);
    }
}
