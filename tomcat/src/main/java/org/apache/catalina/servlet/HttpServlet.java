package org.apache.catalina.servlet;

import org.apache.coyote.http.vo.HttpRequest;
import org.apache.coyote.http.vo.HttpResponse;

public interface HttpServlet {

    HttpResponse service(final HttpRequest httpRequest);

    boolean isSupported(final HttpRequest httpRequest);
}
