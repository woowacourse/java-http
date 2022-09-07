package org.apache.coyote.http11.servlet;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import nextstep.jwp.controller.Controller;
import nextstep.jwp.controller.LoginController;
import nextstep.jwp.controller.RegisterController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;

public class FrontServlet {

    private final Map<String, Controller> controllers;

    public FrontServlet() {
        controllers = new HashMap<>();
        controllers.put("/login", new LoginController());
        controllers.put("/register", new RegisterController());
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
        final Optional<String> viewName = httpResponse.getViewName();
        if (viewName.isEmpty()) {
            return;
        }

        final ViewResolver viewResolver = new ViewResolver(viewName.get() + ".html");
        final ViewInfo viewInfo = viewResolver.render();
        httpResponse.setBody(viewInfo.getViewContent(), viewInfo.getContentType(), viewInfo.getContentLength());
    }

    private void renderView(final String url, final HttpResponse httpResponse) throws IOException, URISyntaxException {
        final ViewResolver viewResolver = new ViewResolver(url);
        final ViewInfo viewInfo = viewResolver.render();

        httpResponse.setBody(viewInfo.getViewContent(), viewInfo.getContentType(), viewInfo.getContentLength());
    }
}
