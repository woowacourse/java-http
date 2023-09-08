package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.common.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.ResponseEntity;

public abstract class AbstractController<T> implements Controller {

    protected final T controller;

    protected AbstractController(T controller) {
        this.controller = controller;
    }

    @Override
    public ResponseEntity service(HttpRequest httpRequest) {
        if (httpRequest.isSameHttpMethod(HttpMethod.GET)) {
            return doGet(httpRequest);
        }
        return doPost(httpRequest);
    }

    protected abstract ResponseEntity doGet(HttpRequest httpRequest);

    protected abstract ResponseEntity doPost(HttpRequest httpRequest);

}
