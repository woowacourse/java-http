package org.apache.catalina.core.servlet;

import static org.apache.catalina.core.servlet.HttpServletResponse.staticResource;
import static org.apache.coyote.http11.common.MimeType.HTML;

import org.apache.catalina.core.util.ResourceReader;
import org.apache.catalina.core.util.StaticResource;
import org.apache.coyote.http11.request.Request;

public class DefaultServlet extends HttpServlet {

    private static final String DEFAULT_MAPPING_PATTERN = "/";
    private static final StaticResource DEFAULT_RESPONSE = new StaticResource("Hello world!", HTML.toString());

    public DefaultServlet() {
        super(DEFAULT_MAPPING_PATTERN);
    }

    @Override
    public void service(final Request request, final HttpServletResponse response) throws Exception {
        if ("/".equals(request.getUri())) {
            response.set(staticResource(DEFAULT_RESPONSE));
            return;
        }

        final var resource = ResourceReader.read(request.getUri());
        response.set(staticResource(resource));
    }

}
