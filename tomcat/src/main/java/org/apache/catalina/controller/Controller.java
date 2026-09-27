package org.apache.catalina.controller;

import org.apache.coyote.http11.request.HttpRequest;

public interface Controller {
    String service(HttpRequest httpRequest) throws Exception;
}
