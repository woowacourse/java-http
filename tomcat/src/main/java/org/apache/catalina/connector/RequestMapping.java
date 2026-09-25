package org.apache.catalina.connector;

import org.apache.coyote.http11.HttpRequest;

public interface RequestMapping {
    Controller getController(HttpRequest request);
}
