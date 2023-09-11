package org.apache.coyote.http11.servlet;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import org.apache.coyote.http11.common.ContentType;
import org.apache.coyote.http11.common.HttpMethod;
import org.apache.coyote.http11.common.request.HttpRequest;
import org.apache.coyote.http11.common.response.HttpResponse;
import org.apache.coyote.http11.common.response.StatusCode;
import org.apache.coyote.http11.util.StaticFileLoader;

public class NotFoundServlet extends Servlet {

    private static final List<HttpMethod> METHODS = Collections.emptyList();

    public NotFoundServlet() {
        super(METHODS);
    }

    @Override
    public void service(final HttpRequest request, final HttpResponse response) throws IOException {
        String content = StaticFileLoader.load(Page.NOT_FOUND.getUri());

        response.setStatusCode(StatusCode.NOT_FOUND);
        response.setContentType(ContentType.TEXT_HTML);
        response.setContentLength(content.getBytes().length);
        response.setBody(content);
    }
}
