package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.request.HttpRequest;

public interface RequestMapping {

    Controller getController(HttpRequest request);
}
