package com.techcourse.controller;

import com.techcourse.utility.StaticFileUtility;
import java.util.List;
import org.apache.catalina.core.controller.AbstractController;
import org.apache.tomcat.util.http.request.HttpRequest;
import org.apache.tomcat.util.http.response.HttpResponse;
import org.apache.tomcat.util.http.value.ContentType;
import org.apache.tomcat.util.http.value.HttpHeader;
import org.apache.tomcat.util.http.value.StatusCode;

public class StaticFileController extends AbstractController {

    private static final String DEFAULT_FILE_EXTENSION = ".html";

    public StaticFileController() {
        super("");
    }

    @Override
    public boolean canProcessable(HttpRequest request) {
        if (request.getUri().isEmpty() || request.getUri().equals("/")) {
            return false;
        }
        String uri = addDefaultExtension(request.getUri());
        return StaticFileUtility.isExistFile(uri);
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        if (request.getUri().isEmpty() || request.getUri().equals("/")) {
            return;
        }
        String uri = addDefaultExtension(request.getUri());
        String content = StaticFileUtility.readFile(uri);
        ContentType contentType = StaticFileUtility.getFileExtension(uri);
        response.setStatusCode(StatusCode.OK);
        response.setHeader(HttpHeader.CONTENT_TYPE.getValue(), contentType.getValue());
        response.setBody(content);
    }

    private String addDefaultExtension(String uri) {
        List<String> uriPart = List.of(uri.split("/"));
        if (uriPart.getLast().contains(".")) {
            return uri;
        }
        return uri + DEFAULT_FILE_EXTENSION;
    }
}
