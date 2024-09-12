package com.techcourse.controller;

import java.io.IOException;
import org.apache.coyote.http11.Servlet;
import org.apache.coyote.http11.request.HttpServletRequest;
import org.apache.coyote.http11.response.HttpServletResponse;
import org.apache.util.FileUtils;

public class StaticResourceServlet implements Servlet {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String fileName = request.getUriPath();
        String fileContent = FileUtils.readFile(fileName);
        response.ok(fileContent, FileUtils.getFileExtension(fileName));
    }
}
