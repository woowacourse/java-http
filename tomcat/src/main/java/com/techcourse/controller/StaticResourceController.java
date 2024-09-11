package com.techcourse.controller;

import java.io.IOException;
import org.apache.coyote.http11.HttpProtocol;
import org.apache.coyote.http11.HttpRequestHandler;
import org.apache.coyote.http11.request.HttpServletRequest;
import org.apache.coyote.http11.request.line.Method;
import org.apache.coyote.http11.response.HttpServletResponse;
import org.apache.util.FileUtils;

public class StaticResourceController implements HttpRequestHandler {

    private static final String STATIC_RESOURCE_PATH = "static";
    private static final Method SUPPORTING_METHOD = Method.GET;
    private static final HttpProtocol SUPPORTING_PROTOCOL = HttpProtocol.HTTP_11;

    @Override
    public boolean supports(HttpServletRequest request) {
        return request.methodEquals(SUPPORTING_METHOD) &&
                request.protocolEquals(SUPPORTING_PROTOCOL) &&
                !request.isUriHome() &&
                resourceAvailable(request.getUriPath());
    }

    @Override
    public HttpServletResponse handle(HttpServletRequest request) throws IOException {
        String fileName = request.getUriPath();
        String fileContent = FileUtils.readFile(fileName);
        return HttpServletResponse.ok(fileContent, FileUtils.getFileExtension(fileName));
    }

    private boolean resourceAvailable(String fileName) {
        try {
            String filePath = getClass().getClassLoader().getResource(STATIC_RESOURCE_PATH + fileName).getFile();
        } catch (NullPointerException e) {
            return false;
        }
        return true;
    }
}
