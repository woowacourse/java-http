package org.apache.catalina;

import org.apache.coyote.http11.HttpRequest;

public interface ControllerMapping {

    Controller getController(HttpRequest request);
}
