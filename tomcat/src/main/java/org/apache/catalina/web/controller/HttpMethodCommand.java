package org.apache.catalina.web.controller;

import org.apache.coyote.http11.request.Http11Request;
import org.apache.coyote.http11.response.Http11Response;

@FunctionalInterface
public interface HttpMethodCommand {

    String execute(Http11Request request, Http11Response response);
}
