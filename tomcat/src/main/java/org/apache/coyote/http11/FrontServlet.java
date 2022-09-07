package org.apache.coyote.http11;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.controller.Controller;
import org.apache.coyote.http11.controller.LoginController;

public class FrontServlet {

    private final Map<String, Controller> controllers;

    public FrontServlet() {
        controllers = new HashMap<>();
        controllers.put("/login", new LoginController());
    }

    public void service(final HttpRequest httpRequest, final HttpResponse httpResponse)
            throws URISyntaxException, IOException {
        final String url = httpRequest.getUrl();

        if ("/".equals(url)) {
            httpResponse.setStatus(HttpStatus.OK);
            httpResponse.setBody("Hello world!");
            return;
        }

        if (controllers.containsKey(url)) {
            final Controller controller = controllers.get(url);
            controller.process(httpRequest, httpResponse);
            renderView(httpResponse);
            return;
        }
        renderView(url, httpResponse);
    }

    private void renderView(final HttpResponse httpResponse) throws IOException, URISyntaxException {
        final String viewName = httpResponse.getViewName();
        final ViewResolver viewResolver = new ViewResolver(viewName + ".html");
        final ViewInfo viewInfo = viewResolver.render();

        httpResponse.setBody(viewInfo.getViewContent(), viewInfo.getContentType(), viewInfo.getContentLength());
    }

    private void renderView(final String url, final HttpResponse httpResponse) throws IOException, URISyntaxException {
        final ViewResolver viewResolver = new ViewResolver(url);
        final ViewInfo viewInfo = viewResolver.render();

        httpResponse.setBody(viewInfo.getViewContent(), viewInfo.getContentType(), viewInfo.getContentLength());
    }
}
