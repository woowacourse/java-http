package org.apache.catalina.controller;

import org.apache.catalina.exception.CatalinaException;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;

public class DefaultController extends AbstractController {

    private static final String PATH_PREFIX = "/";
    private static final String ROOT_PATH = "/";
    private static final String INDEX_PATH = "/index.html";
    private static final String PERIOD = ".";

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        String path = request.getPath();
        if (path.equals(ROOT_PATH)) {
            response.setRedirect(INDEX_PATH);
            return;
        }

        String fileName = parseFileName(path);
        if (fileName.contains(PERIOD)) {
            response.setStaticResource(fileName);
            return;
        }

        response.setNotFound();
    }

    private String parseFileName(String path) {
        if (!path.startsWith(PATH_PREFIX)) {
            throw new CatalinaException("요청 URI는 %s로 시작해야 합니다.".formatted(PATH_PREFIX));
        }

        return path.substring(path.indexOf(PATH_PREFIX) + PATH_PREFIX.length());
    }
}
