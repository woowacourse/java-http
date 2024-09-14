package com.techcourse.servlet;

import org.apache.catalina.servlet.HttpServlet;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.response.HttpResponse;
import org.apache.util.FileUtils;

public class DefaultServlet extends HttpServlet {

    private static final String STATIC_RESOURCE_PATH = "static";

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        String fileName = request.getUriPath();
        String fileContent = FileUtils.readFile(STATIC_RESOURCE_PATH + fileName);
        response.ok(fileContent, FileUtils.getFileExtension(fileName));
    }
}
