package org.apache.catalina.servlet;

import org.apache.catalina.exception.UncheckedServletException;
import org.apache.coyote.http.vo.HttpRequest;
import org.apache.coyote.http.vo.HttpResponse;

public interface Controller {

    void service(final HttpRequest httpRequest, final HttpResponse httpResponse) throws UncheckedServletException;

    boolean isSupported(final HttpRequest httpRequest);
}
