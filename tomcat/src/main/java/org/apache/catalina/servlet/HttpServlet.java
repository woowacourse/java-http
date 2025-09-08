package org.apache.catalina.servlet;

import com.http.enums.HttpMethod;
import com.http.enums.HttpStatus;
import java.io.IOException;
import org.apache.catalina.domain.request.HttpRequest;
import org.apache.catalina.domain.response.HttpResponse;

public interface HttpServlet {

    default void service(HttpRequest request, HttpResponse response) throws IOException {
        final HttpMethod method = request.requestStartLine().method();

        switch (method) {
            case GET -> doGet(request, response);
            case POST -> doPost(request, response);
            default -> response.sendError(HttpStatus.METHOD_NOT_ALLOWED, "지원하지 않는 메서드입니다.");
        }
    }

    void doGet(HttpRequest request, HttpResponse response) throws IOException;

    void doPost(HttpRequest request, HttpResponse response) throws IOException;
}
