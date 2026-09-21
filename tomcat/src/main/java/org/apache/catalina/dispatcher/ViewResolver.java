package org.apache.catalina.dispatcher;

import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.headers.ContentType;
import org.apache.coyote.http11.response.line.HttpStatus;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class ViewResolver {

    private static final String REDIRECT_PREFIX = "redirect:";
    private static final String STATIC_ROOT = "static";
    private static final String NOT_FOUND_VIEW = "/404.html";

    public void resolve(String viewName, HttpResponse response) throws IOException {
        if (viewName.startsWith(REDIRECT_PREFIX)) {
            response.sendRedirect(viewName.substring(REDIRECT_PREFIX.length()));
            return;
        }
        URL resource = findResource(viewName);
        if (resource == null) {
            response.setStatus(HttpStatus.NOT_FOUND);
            render(NOT_FOUND_VIEW, findResource(NOT_FOUND_VIEW), response);
            return;
        }
        render(viewName, resource, response);
    }

    private URL findResource(String viewName) {
        return getClass().getClassLoader().getResource(STATIC_ROOT + viewName);
    }

    private void render(String viewName, URL resource, HttpResponse response) throws IOException {
        try (InputStream inputStream = resource.openStream()) {
            String content = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            response.setBody(ContentType.from(viewName), content);
        }
    }

}
