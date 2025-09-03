package org.apache.catalina.servlet;

import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;

public class IndexServlet extends HttpServlet{

    @Override
    public boolean canHandle(final HttpRequest httpRequest) {
        return false;
    }

    @Override
    public HttpResponse doGet(final HttpRequest httpRequest) {
        return null;
    }

    @Override
    public HttpResponse doPost(final HttpRequest httpRequest) {
        throw new IllegalArgumentException("[ERROR] 해당 요청을 찾지 못했습니다.");
    }
}
