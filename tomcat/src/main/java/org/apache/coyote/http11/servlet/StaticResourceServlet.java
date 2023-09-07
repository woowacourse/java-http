package org.apache.coyote.http11.servlet;

import java.io.IOException;
import java.util.List;
import org.apache.coyote.http11.common.ContentType;
import org.apache.coyote.http11.common.HttpHeaderName;
import org.apache.coyote.http11.common.HttpHeaders;
import org.apache.coyote.http11.common.request.HttpMethod;
import org.apache.coyote.http11.common.request.HttpRequest;
import org.apache.coyote.http11.common.response.HttpResponse;
import org.apache.coyote.http11.common.response.StatusCode;
import org.apache.coyote.http11.util.StaticFileLoader;

public class StaticResourceServlet implements Servlet {

    @Override
    public HttpResponse handle(final HttpRequest request) throws IOException {
        if (request.getMethod() == HttpMethod.GET) {
            return doGet(request);
        }
        return HttpResponse.createMethodNotAllowed(List.of(HttpMethod.GET));
    }

    private static HttpResponse doGet(final HttpRequest request) throws IOException {
        String detail = request.getUri().getValue();
        String content = StaticFileLoader.load(detail);
        String extension = request.getUri().getExtension();

        if (content.isEmpty()) {
            content = StaticFileLoader.load(Page.NOT_FOUND.getUri());
            extension = ContentType.TEXT_HTML.getExtension();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.addHeader(HttpHeaderName.CONTENT_TYPE, ContentType.getDetailfromExtension(extension));
        headers.addHeader(HttpHeaderName.CONTENT_LENGTH, String.valueOf(content.getBytes().length));

        return HttpResponse.create(StatusCode.OK, headers, content);
    }
}
