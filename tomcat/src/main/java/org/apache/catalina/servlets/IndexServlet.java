package org.apache.catalina.servlets;

import nextstep.mvc.ResponseWriter;
import org.apache.catalina.servlets.config.RequestMapping;
import org.apache.catalina.servlets.exception.MethodNotAllowedException;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;

@RequestMapping("/index")
public class IndexServlet extends HttpServlet {

    @Override
    protected void doGet(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        ResponseWriter.view(httpResponse, HttpStatus.OK, httpRequest.getUri());
    }

    @Override
    protected void doPost(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        throw new MethodNotAllowedException();
    }
}
