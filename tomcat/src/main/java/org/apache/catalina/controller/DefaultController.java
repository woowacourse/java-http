package org.apache.catalina.controller;

import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;

public class DefaultController extends AbstractController {

    private static final String PATH_PREFIX = "/";
    private static final String EMPTY_PATH = "/";
    private static final String INDEX_PATH = "/index.html";

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        String path = request.getPath();

        if (path.equals(EMPTY_PATH)) {
            response.setRedirect(INDEX_PATH);
            return;
        }

        if (path.startsWith(PATH_PREFIX)) {
            response.setStaticResource(path.substring(path.indexOf(PATH_PREFIX) + PATH_PREFIX.length()));
        }
    }
}
