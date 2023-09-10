package org.apache.catalina.servlet.handler;

import static org.apache.coyote.http11.common.MimeType.HTML;
import static org.apache.coyote.http11.response.Response.staticResource;

import org.apache.catalina.servlet.util.ResourceReader;
import org.apache.catalina.servlet.util.StaticResource;
import org.apache.coyote.http11.request.Request;
import org.apache.coyote.http11.response.Response.ServletResponse;

public class StaticResourceRequestHandler implements RequestHandler {

    private static final StaticResource DEFAULT_RESPONSE = new StaticResource("Hello world!", HTML.toString());

    @Override
    public void service(final Request request, final ServletResponse response) throws Exception {
        if ("/".equals(request.getUri())) {
            response.set(staticResource(DEFAULT_RESPONSE));
            return;
        }

        final var resource = ResourceReader.read(request.getUri());
        response.set(staticResource(resource));
    }

}
