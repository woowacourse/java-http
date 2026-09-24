package org.apache.catalina.controller;

import org.apache.coyote.http11.HttpRequest;

public interface ControllerMapping {

    Controller getController(HttpRequest request);
}
