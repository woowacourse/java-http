package org.apache.coyote.http11.servlet;

import java.io.IOException;
import java.util.List;
import org.apache.coyote.http11.common.ContentType;
import org.apache.coyote.http11.common.HttpMethod;
import org.apache.coyote.http11.common.request.HttpRequest;
import org.apache.coyote.http11.common.response.HttpResponse;
import org.apache.coyote.http11.common.response.StatusCode;

public class HelloWorldServlet extends Servlet {

    private static final List<HttpMethod> METHODS = List.of(HttpMethod.GET);
    public static final String HELLO_WORLD = "Hello world!";

    public HelloWorldServlet() {
        super(METHODS);
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws IOException {
        String body = HELLO_WORLD;

        response.setStatusCode(StatusCode.OK);
        response.setContentType(ContentType.TEXT_PLAIN);
        response.setContentLength(body.getBytes().length);
        response.setBody(body);
    }
}
