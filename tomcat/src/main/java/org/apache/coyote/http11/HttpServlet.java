package org.apache.coyote.http11;

import java.io.IOException;
import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public abstract class HttpServlet {

    protected void service(final HttpRequest req, final HttpResponse res) throws IOException {
        if (req.isSameMethod(HttpMethod.GET)) {
            doGet(req, res);
        }
        if (req.isSameMethod(HttpMethod.POST)) {
            doPost(req, res);
        }
    }

    public void doGet(final HttpRequest req, final HttpResponse resp) throws IOException {
        throw new UnsupportedOperationException();
    }

    public void doPost(final HttpRequest req, final HttpResponse resp) {
        throw new UnsupportedOperationException();
    }
}
