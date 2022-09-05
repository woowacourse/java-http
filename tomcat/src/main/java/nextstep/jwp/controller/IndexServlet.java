package nextstep.jwp.controller;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.StatusCode;
import org.apache.servlet.HttpServlet;

public class IndexServlet extends HttpServlet {

    private static final String PATH = "/";

    @Override
    public boolean isSupported(final String path) {
        return PATH.equals(path);
    }

    @Override
    protected void doGet(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        httpResponse.addStatusCode(StatusCode.OK);
        httpResponse.addBody("Hello world!");
    }

    @Override
    protected void doPost(final HttpRequest httpRequest, final HttpResponse httpResponse) {
    }
}
