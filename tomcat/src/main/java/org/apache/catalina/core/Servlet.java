package org.apache.catalina.core;

import org.apache.coyote.support.Request;
import org.apache.coyote.support.Response;

public interface Servlet {

    void init();

    void service(Request request, Response response);
}
