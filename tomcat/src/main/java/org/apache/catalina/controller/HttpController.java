package org.apache.catalina.controller;

import java.io.IOException;
import java.util.Collections;

import org.apache.coyote.http11.exception.MethodNotAllowedException;
import org.apache.coyote.http11.request.Request;
import org.apache.coyote.http11.response.Response;

public abstract class HttpController implements Controller {
    protected Response doPost(Request request) throws IOException {
        throw new MethodNotAllowedException(Collections.emptyList());
    }

    protected Response doGet(Request request) throws IOException {
        throw new MethodNotAllowedException(Collections.emptyList());
    }
}
