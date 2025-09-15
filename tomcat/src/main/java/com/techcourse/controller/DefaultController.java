package com.techcourse.controller;

import org.apache.coyote.http11.StaticFileHandler;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.MimeType;

import java.io.IOException;
import java.util.Objects;

public class DefaultController extends AbstractController {

    private static final StaticFileHandler staticFileHandler = new StaticFileHandler();

    @Override
    protected HttpResponse doGet(HttpRequest request) {
        try {
            return generateHttpResponse(request.getPath());
        } catch (IOException e) {
            return HttpResponse.notFound();
        }
    }

    private String resolveFilePath(final String requestPath) {
        String htmlPath = requestPath + ".html";
        if (staticFileHandler.exists(htmlPath)) {
            return htmlPath;
        }
        return requestPath;
    }

    private HttpResponse generateHttpResponse(final String requestPath) throws IOException {
        if (Objects.equals("/", requestPath)) {
            return HttpResponse.ok("Hello world!", MimeType.TEXT_PLAIN);
        }
        
        return createFileResponse(requestPath);
    }

    private HttpResponse createFileResponse(final String requestPath) throws IOException {
        String resolvedPath = resolveFilePath(requestPath);
        
        if (staticFileHandler.exists(resolvedPath)) {
            String fileContent = staticFileHandler.readFile(resolvedPath);
            MimeType mimeType = staticFileHandler.getContentType(resolvedPath);
            return HttpResponse.ok(fileContent, mimeType);
        } else {
            return HttpResponse.notFound();
        }
    }
}