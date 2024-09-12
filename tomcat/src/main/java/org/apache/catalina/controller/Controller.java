package org.apache.catalina.controller;

import org.apache.coyote.http11.Http11Request;
import org.apache.coyote.http11.Http11Response;

public interface Controller {

    void service(Http11Request request, Http11Response response) throws Exception;
}

