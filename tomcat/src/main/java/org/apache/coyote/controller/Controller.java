package org.apache.coyote.controller;

import java.io.IOException;
import org.apache.coyote.request.HttpRequest;

public interface Controller {

    String service(HttpRequest httpRequest) throws IOException;

    boolean isRest();
}
