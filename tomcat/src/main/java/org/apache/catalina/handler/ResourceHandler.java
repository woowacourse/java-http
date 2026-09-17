package org.apache.catalina.handler;

import java.io.IOException;
import org.apache.coyote.http.HttpServletRequest;
import org.apache.coyote.http.HttpServletResponse;

public interface ResourceHandler {

    boolean canHandle(HttpServletRequest request);

    HttpServletResponse handle(HttpServletRequest request) throws IOException;
}
