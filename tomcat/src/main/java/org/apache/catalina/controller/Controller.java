package org.apache.catalina.controller;

import org.apache.coyote.http11.dto.request.HttpRequest;

public interface Controller {

    ControllerResult service(HttpRequest request) throws Exception;
}
