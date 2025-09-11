package org.apache.catalina.controller;

import org.apache.coyote.http11.request.Http11Request;
import org.apache.coyote.http11.response.Http11Response;

public interface Controller {

    Http11Response control(final Http11Request request);
}
