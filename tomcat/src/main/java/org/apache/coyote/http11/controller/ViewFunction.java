package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.request.Request;
import org.apache.coyote.http11.response.Response;

@FunctionalInterface
public interface ViewFunction {
    Response getResponse();
}
