package org.apache.coyote.response;

import org.apache.coyote.fixture.HttpRequestFixture;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.util.HttpStatus;
import org.apache.coyote.util.ResourceFinder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.OutputStream;

import static org.junit.jupiter.api.Assertions.assertTrue;

class HttpResponseTest {

    private final HttpResponse httpResponse;

    public HttpResponseTest() {
        this.httpResponse = new HttpResponse(OutputStream.nullOutputStream());
    }

    @Test
    @DisplayName("Error를 보내는 경우 해당하는 Status에 일치하는 페이지로 redirect 한다.")
    void sendError() {
        httpResponse.sendError(HttpStatus.NOT_FOUND);

        assertTrue(new String(httpResponse.combineResponseToBytes()).contains("Location: 404.html"));
    }

    @Test
    @DisplayName("요청한 Location으로 redirect 한다.")
    void sendRedirect() {
        httpResponse.sendRedirect("/index.html");

        assertTrue(new String(httpResponse.combineResponseToBytes()).contains("Location: /index.html"));
    }

    @Test
    @DisplayName("정적 파일을 반환한다.")
    void sendStaticResourceResponse() {
        HttpRequest httpRequest = HttpRequestFixture.GET_STATIC_MAIN_PATH_REQUEST;

        httpResponse.sendStaticResourceResponse(httpRequest, HttpStatus.OK);
        String resource = ResourceFinder.findBy("/index.html");

        assertTrue(new String(httpResponse.combineResponseToBytes()).contains(resource));
    }
}
