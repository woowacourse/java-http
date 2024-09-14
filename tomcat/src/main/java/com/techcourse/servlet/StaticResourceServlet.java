package com.techcourse.servlet;

import java.io.IOException;
import org.apache.catalina.servlet.HttpServlet;
import org.apache.coyote.http11.request.HttpServletRequest;
import org.apache.coyote.http11.response.HttpServletResponse;
import org.apache.util.FileUtils;

public class StaticResourceServlet extends HttpServlet {

    private static final String STATIC_RESOURCE_PATH = "static";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String fileName = request.getUriPath();
        String fileContent = FileUtils.readFile(STATIC_RESOURCE_PATH + fileName);
        response.ok(fileContent, FileUtils.getFileExtension(fileName));
    }
}
