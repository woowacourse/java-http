package org.apache.coyote;

import org.apache.catalina.servlet.Controller;
import org.apache.coyote.http11.request.HttpRequest;

public interface Adapter {

    Controller getController(HttpRequest request);

    void addController(String path, Controller controller);
}
