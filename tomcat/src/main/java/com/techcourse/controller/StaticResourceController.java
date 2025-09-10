package com.techcourse.controller;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.apache.coyote.http11.HttpHeaders;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;

public class StaticResourceController extends AbstractController {
    
    private static final String STATIC_RESOURCE_PATH = "static";
    private static final String QUERY_PARAM_STARTER = "?";
    private static final String HTML_EXTENSION = ".html";
    private static final String INDEX_HTML = "/index.html";
    
    @Override
    protected HttpResponse doGet(HttpRequest request) throws Exception {
        return process(request);
    }
    
    @Override
    protected HttpResponse doPost(HttpRequest request) {
        return new HttpResponse(
                "HTTP/1.1",
                HttpStatus.METHOD_NOT_ALLOWED,
                HttpHeaders.empty(),
                ""
        );
    }
    
    private HttpResponse process(HttpRequest request) throws IOException {
        String requestUri = request.getRequestUri();
        String resourcePath = requestUri.equals("/") ? INDEX_HTML : requestUri;
        String finalResourcePath = resolveResourcePath(resourcePath);
        
        try (InputStream inputStream = StaticResourceController.class.getClassLoader().getResourceAsStream(finalResourcePath)) {
            if (inputStream == null) {
                return send404Page();
            }
            byte[] responseBody = inputStream.readAllBytes();
            HttpHeaders headers = HttpHeaders.fromFile(finalResourcePath)
                .add("Content-Length", String.valueOf(responseBody.length));
            
            return new HttpResponse("HTTP/1.1", HttpStatus.OK, headers, 
                new String(responseBody, StandardCharsets.UTF_8));
        }
    }
    
    private String resolveResourcePath(String requestUri) {
        int queryIndex = requestUri.indexOf(QUERY_PARAM_STARTER);
        if (queryIndex != -1) {
            requestUri = requestUri.substring(0, queryIndex);
        }
        
        if (hasNoExtension(requestUri)) {
            return STATIC_RESOURCE_PATH + requestUri + HTML_EXTENSION;
        }
        return STATIC_RESOURCE_PATH + requestUri;
    }
    
    
    private boolean hasNoExtension(String resource) {
        int lastDotIndex = resource.lastIndexOf(".");
        return lastDotIndex == -1 || lastDotIndex == 0 || lastDotIndex == resource.length() - 1;
    }
    
    private HttpResponse send404Page() throws IOException {
        try (InputStream inputStream = StaticResourceController.class.getClassLoader().getResourceAsStream("static/404.html")) {
            if (inputStream == null) {
                return new HttpResponse("HTTP/1.1", HttpStatus.NOT_FOUND, HttpHeaders.empty(), "");
            }
            byte[] responseBody = inputStream.readAllBytes();
            HttpHeaders headers = HttpHeaders.html()
                .add("Content-Length", String.valueOf(responseBody.length));
            
            return new HttpResponse("HTTP/1.1", HttpStatus.NOT_FOUND, headers,
                new String(responseBody, StandardCharsets.UTF_8));
        }
    }
}
