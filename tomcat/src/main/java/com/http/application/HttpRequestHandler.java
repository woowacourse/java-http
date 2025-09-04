package com.http.application;

import com.http.domain.HttpRequest;
import com.http.domain.HttpResponse;
import java.io.IOException;
import java.util.LinkedHashMap;

public class HttpRequestHandler {

    public static HttpResponse handle(HttpRequest request) throws IOException {
        final String startLine = "HTTP/1.1 200 OK ";

        // 본문 작업하는 코드 구현
        final byte[] responseBody = StaticResourceHandler.getResponseBody(request);

        HttpResponse response = new HttpResponse(startLine, new LinkedHashMap<>(), responseBody);

        HeaderResolver.handle(request, response);


        return response;
    }
}
