package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.model.request.HttpRequest;
import org.apache.coyote.http11.model.response.Http11Response;

public interface Controller {
    void service(HttpRequest request, Http11Response response) throws Exception;
}
