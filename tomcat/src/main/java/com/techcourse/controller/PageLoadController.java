package com.techcourse.controller;

import org.apache.catalina.http.ContentType;
import org.apache.catalina.mvc.AbstractController;
import org.apache.catalina.reader.FileReader;
import org.apache.catalina.request.HttpRequest;
import org.apache.catalina.response.HttpResponse;

public class PageLoadController extends AbstractController {

    @Override
    public boolean isMatchesRequest(HttpRequest request) {
        return request.getPath().startsWith("/") && ContentType.isValidFileExtension(request.getPath());
    }

    @Override
    public void doGet(HttpRequest request, HttpResponse response) {
        response.setBody(FileReader.loadFileContent(request.getPath()));
    }
}
