package org.apache.catalina.controller;

import com.techcourse.exception.UncheckedServletException;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;

public class DefaultController extends AbstractController {

    private static final String PATH_PREFIX = "/";
    private static final String EMPTY_PATH = "/";
    private static final String INDEX_PATH = "/index.html";
    private static final String PERIOD = ".";
    private static final String HTML_SUFFIX = ".html";

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        String path = request.getPath();

        if (path.equals(EMPTY_PATH)) {
            response.setRedirect(INDEX_PATH);
            return;
        }

        String fileName = parseFileName(path);
        response.setStaticResource(fileName);
    }

    private String parseFileName(String path) {
        if (!path.startsWith(PATH_PREFIX)) {
            throw new UncheckedServletException("요청 URI는 %s로 시작해야 합니다.".formatted(PATH_PREFIX));
        }

        String fileName = path.substring(path.indexOf(PATH_PREFIX) + PATH_PREFIX.length());
        if (!fileName.contains(PERIOD)) {
            fileName += HTML_SUFFIX;
        }

        return fileName;
    }
}
