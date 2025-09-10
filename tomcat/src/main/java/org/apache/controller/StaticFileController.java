package org.apache.controller;

import java.util.List;
import org.apache.http.request.HttpRequest;
import org.apache.http.response.HttpResponse;
import org.apache.http.value.ContentType;
import org.apache.http.value.HttpHeader;
import org.apache.http.value.StatusCode;
import org.apache.reader.StaticFileUtility;

public class StaticFileController implements Controller {

    private static final String DEFAULT_FILE_EXTENSION = ".html";

    @Override
    public boolean isProcessableRequest(HttpRequest request) {
        if (request.getUri().isEmpty() || request.getUri().equals("/")) {
            return false;
        }
        String uri = addDefaultExtension(request.getUri());
        return StaticFileUtility.isExistFile(uri);
    }

    @Override
    public void processRequest(HttpRequest request, HttpResponse response) {
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
