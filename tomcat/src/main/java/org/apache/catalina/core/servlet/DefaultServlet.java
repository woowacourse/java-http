package org.apache.catalina.core.servlet;

import static org.apache.catalina.core.servlet.HttpServletResponse.staticResource;
import static org.apache.coyote.http11.common.MimeType.HTML;

import org.apache.catalina.core.util.ResourceReader;
import org.apache.catalina.core.util.StaticResource;

public class DefaultServlet extends HttpServlet {

    private static final String DEFAULT_MAPPING_PATTERN = "/";
    private static final StaticResource DEFAULT_RESPONSE = new StaticResource("Hello world!", HTML.toString());

    public DefaultServlet() {
        super(DEFAULT_MAPPING_PATTERN);
    }

    @Override
    public void service(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse)
            throws Exception {
        if ("/".equals(httpServletRequest.getUri())) {
            httpServletResponse.set(staticResource(DEFAULT_RESPONSE));
            return;
        }

        final var resource = ResourceReader.read(httpServletRequest.getUri());
        httpServletResponse.set(staticResource(resource));
    }

}
