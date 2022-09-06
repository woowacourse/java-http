package org.apache.coyote.http11;

import java.text.MessageFormat;
import org.apache.coyote.exception.MethodNotAllowedException;
import org.apache.coyote.http11.request.model.HttpMethod;
import org.apache.coyote.http11.request.model.HttpRequest;
import org.apache.coyote.support.Controller;

public class WebClient {

    private static final String INDEX_URI = "/";

    public HttpResponse request(final HttpRequest httpRequest) {
        if (httpRequest.isEqualToMethod(HttpMethod.GET)) {
            return doGet(httpRequest);
        }
        if (httpRequest.isEqualToMethod(HttpMethod.POST)) {
            return doPost(httpRequest);
        }
        throw new MethodNotAllowedException(MessageFormat.format("not allowed : {0}", httpRequest.getMethod()));
    }

    private HttpResponse doGet(final HttpRequest httpRequest) {
        Controller controller = new Controller(httpRequest);

        if (httpRequest.isEqualToUri(INDEX_URI)) {
            return controller.get();
        }

        if (httpRequest.isQueryString()) {
            return controller.get();
        }

        return controller.get();
    }

    private HttpResponse doPost(final HttpRequest httpRequest) {
        Controller controller = new Controller(httpRequest);
        return controller.post();
    }
}
