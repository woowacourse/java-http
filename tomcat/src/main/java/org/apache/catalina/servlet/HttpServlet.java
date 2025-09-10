package org.apache.catalina.servlet;

import com.spring.http.enums.HttpMethod;
import com.spring.http.enums.HttpStatus;
import java.io.IOException;
import com.spring.http.request.HttpRequest;
import com.spring.http.response.HttpResponse;

public interface HttpServlet {

    default void service(HttpRequest request, HttpResponse response) throws IOException {
        final HttpMethod method = request.requestStartLine().method();

        switch (method) {
            case GET -> doGet(request, response);
            case POST -> doPost(request, response);
            default -> response.sendError(HttpStatus.METHOD_NOT_ALLOWED, "지원하지 않는 메서드입니다.");
        }
    }

    default void doGet(HttpRequest request, HttpResponse response) throws IOException {
        response.sendError(HttpStatus.METHOD_NOT_ALLOWED, "GET 메서드를 지원하지 않습니다.");
    }

    default void doPost(HttpRequest request, HttpResponse response) throws IOException {
        response.sendError(HttpStatus.METHOD_NOT_ALLOWED, "GET 메서드를 지원하지 않습니다.");
    }
}
