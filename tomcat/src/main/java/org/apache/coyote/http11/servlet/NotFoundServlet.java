package org.apache.coyote.http11.servlet;

import java.io.IOException;
import org.apache.coyote.http11.common.ContentType;
import org.apache.coyote.http11.common.HttpHeaderName;
import org.apache.coyote.http11.common.HttpHeaders;
import org.apache.coyote.http11.common.request.HttpRequest;
import org.apache.coyote.http11.common.response.HttpResponse;
import org.apache.coyote.http11.common.response.StatusCode;
import org.apache.coyote.http11.util.StaticFileLoader;

public class NotFoundServlet implements Servlet {

    @Override
    public HttpResponse handle(final HttpRequest request) throws IOException {
        String content = StaticFileLoader.load(Page.NOT_FOUND.getUri());

        HttpHeaders headers = new HttpHeaders();
        headers.addHeader(HttpHeaderName.CONTENT_TYPE, ContentType.TEXT_HTML.getDetail());
        headers.addHeader(HttpHeaderName.CONTENT_LENGTH, String.valueOf(content.getBytes().length));

        return HttpResponse.create(StatusCode.NOT_FOUND, headers, content);
    }
}
