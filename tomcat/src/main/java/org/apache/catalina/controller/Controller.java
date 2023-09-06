package org.apache.catalina.controller;

import java.io.IOException;

import org.apache.coyote.http11.exception.MethodNotAllowedException;
import org.apache.coyote.http11.request.Request;
import org.apache.coyote.http11.response.Response;

public interface Controller {
    Response service(Request request) throws IOException, MethodNotAllowedException;
}
