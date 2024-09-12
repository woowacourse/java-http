package org.apache.coyote.handler.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.apache.http.request.HttpRequest;
import org.apache.http.request.RequestLine;
import org.apache.http.response.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class NotFoundHandlerTest {

    @Test
    @DisplayName("모든 요청 처리: 404 응답 반환")
    void handle() {
        RequestLine requestLine = new RequestLine("GET", "/index.html", "HTTP/1.1");
        HttpRequest httpRequest = new HttpRequest(requestLine, null, null);

        HttpResponse httpResponse = NotFoundHandler.getInstance().handle(httpRequest);
        HttpResponse actual = HttpResponse.builder().notFoundBuild();
        assertEquals(httpResponse, actual);
    }
}
