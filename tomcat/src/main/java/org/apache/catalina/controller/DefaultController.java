package org.apache.catalina.controller;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultController implements Controller {

    private static final List<String> CANDIDATE_FILES = List.of("index.html", "home.html");
    private static final String FALLBACK_MESSAGE = "Hello world!";

    @Override
    public HttpResponse service(final HttpRequest request) throws Exception {
        for (String fileName : CANDIDATE_FILES) {
            if (fileExists(fileName)) {
                return StaticFileResponseBuilder.buildStaticFileResponse(fileName, request.version());
            }
        }
        
        return buildFallbackResponse(request.version());
    }
    
    private boolean fileExists(String fileName) {
        return this.getClass().getClassLoader().getResource("static/" + fileName) != null;
    }
    
    private HttpResponse buildFallbackResponse(String httpVersion) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "text/html;charset=utf-8");
        
        return new HttpResponse(
                httpVersion,
                HttpStatus.OK.getCode(),
                HttpStatus.OK.getReasonPhrase(),
                headers,
                FALLBACK_MESSAGE
        );
    }
}
