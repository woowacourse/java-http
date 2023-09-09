package org.apache.coyote.http11.servlet;

import java.io.IOException;
import org.apache.coyote.http11.common.ContentType;
import org.apache.coyote.http11.common.request.HttpRequest;
import org.apache.coyote.http11.common.request.RequestUri;
import org.apache.coyote.http11.common.response.HttpResponse;
import org.apache.coyote.http11.common.response.StatusCode;
import org.apache.coyote.http11.util.StaticFileLoader;

public class StaticResourceServlet extends Servlet {

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws IOException {
        RequestUri uri = request.getUri();
        String detail = uri.getValue();
        String extension = uri.getExtension();

        String content = StaticFileLoader.load(detail);
        if (content.isEmpty()) {
            content = StaticFileLoader.load(Page.NOT_FOUND.getUri());

            response.setStatusCode(StatusCode.NOT_FOUND);
            response.setContentType(ContentType.TEXT_HTML);
        } else {
            response.setStatusCode(StatusCode.OK);
            response.setContentType(ContentType.getContentTypeFromExtension(extension));
        }
        response.setContentLength(content.getBytes().length);
        response.setBody(content);
    }
}
