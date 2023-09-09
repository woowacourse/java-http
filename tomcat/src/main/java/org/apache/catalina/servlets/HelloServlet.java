package org.apache.catalina.servlets;

import nextstep.mvc.ResponseWriter;
import org.apache.catalina.servlets.config.RequestMapping;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;

@RequestMapping({"/", ""})
public class HelloServlet extends HttpServlet {

    @Override
    protected void doGet(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        ResponseWriter.writeWithData(httpResponse, HttpStatus.OK, "Hello world!");
    }
}
