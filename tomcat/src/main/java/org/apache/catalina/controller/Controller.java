package org.apache.catalina.controller;

import org.apache.coyote.http11.request.Http11Request;
import org.apache.coyote.http11.response.Http11Response;

public interface Controller {

    String handle(Http11Request request, Http11Response response);
}
