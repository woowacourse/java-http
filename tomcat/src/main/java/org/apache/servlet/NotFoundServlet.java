package org.apache.servlet;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.StatusCode;

public class NotFoundServlet extends HttpServlet {

    @Override
    public boolean isSupported(final String path) {
        return false;
    }

    @Override
    public void service(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        httpResponse.addStatusCode(StatusCode.NOT_FOUND);
        httpResponse.addView("404.html");
    }

    @Override
    protected void doGet(final HttpRequest httpRequest, final HttpResponse httpResponse) {
    }

    @Override
    protected void doPost(final HttpRequest httpRequest, final HttpResponse httpResponse) {
    }
}
