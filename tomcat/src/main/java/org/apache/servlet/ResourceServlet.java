package org.apache.servlet;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public class ResourceServlet extends HttpServlet {

    @Override
    public boolean isSupported(final String path) {
        return isResource(path);
    }

    private boolean isResource(final String path) {
        return path.contains(".");
    }

    @Override
    protected void doGet(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        httpResponse.addView(httpRequest.getPath());
    }

    @Override
    protected void doPost(final HttpRequest httpRequest, final HttpResponse httpResponse) {
    }
}
