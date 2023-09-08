package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.types.HttpMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

import static org.apache.coyote.http11.ViewResolver.resolveView;
import static org.apache.coyote.http11.types.HttpProtocol.HTTP_1_1;

public abstract class AbstractController implements Controller {

    protected static final Logger log = LoggerFactory.getLogger(AbstractController.class);

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        if (request == null || response == null) {
            throw new IllegalArgumentException();
        }

        response.setHttpProtocol(HTTP_1_1);
        if (Objects.requireNonNull(request.getMethod()) == HttpMethod.GET) {
            this.doGet(request, response);
            return;
        }

        if (request.getMethod() == HttpMethod.POST) {
            this.doPost(request, response);
            return;
        }

        throw new UnsupportedOperationException();
    }

    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        /* NOOP */
    }

    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        resolveView(request, response);
    }
}
