package org.apache.catalina.routing;

import org.apache.catalina.controller.Controller;
import org.apache.coyote.http11.HttpRequest;

public interface RequestMapping {
    Controller getController(HttpRequest request);
}
