package org.apache.coyote.http11.frontcontroller;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;
import org.apache.coyote.http11.handler.ApiHandler.ApiHandlerResponse;
import org.apache.coyote.http11.httpmessage.ContentType;
import org.apache.coyote.http11.httpmessage.request.HttpRequest;
import org.apache.coyote.http11.httpmessage.response.HttpResponse;
import org.apache.coyote.http11.httpmessage.response.HttpStatus;
import org.apache.coyote.http11.view.ModelAndView;
import org.junit.jupiter.api.Test;

class FrontControllerTest {

    @Test
    void file_요청의_httpRequest를_받으면_httpResponse를_반환한다() throws IOException {
        // given
        String requestMessage = 요청_메시지("GET / HTTP/1.1 ", "");
        HttpRequest httpRequest = httpRequest_생성(requestMessage);

        FrontController frontController = new FrontController();
        // when
        HttpResponse httpResponse = frontController.doDispatch(httpRequest);

        // then
        ModelAndView modelAndView = ModelAndView.of(
                ApiHandlerResponse.of(HttpStatus.OK, new LinkedHashMap<>(), "Hello world!", ContentType.HTML));

        HttpResponse expectedHttpResponse = HttpResponse.of(modelAndView);

        // then
        assertThat(httpResponse).usingRecursiveComparison()
                .isEqualTo(expectedHttpResponse);
    }

    @Test
    void api_요청의_httpRequest를_받으면_httpResponse를_반환한다() throws IOException {
        // given
        String body = "account=gugu&password=password&email=hkkang@woowahan.com";
        String requestMessage = 요청_메시지("POST /register HTTP/1.1 ", body);
        HttpRequest httpRequest = httpRequest_생성(requestMessage);

        FrontController frontController = new FrontController();

        // when
        HttpResponse httpResponse = frontController.doDispatch(httpRequest);

        // then
        LinkedHashMap<String, String> expectedHeaders = new LinkedHashMap<>();
        expectedHeaders.put("Location", "/index.html ");
        ModelAndView modelAndView = ModelAndView.of(
                ApiHandlerResponse.of(HttpStatus.FOUND, expectedHeaders, "", ContentType.HTML));

        HttpResponse expectedHttpResponse = HttpResponse.of(modelAndView);

        assertThat(httpResponse).usingRecursiveComparison()
                .isEqualTo(expectedHttpResponse);
    }

    private static String 요청_메시지(String requestLine, String body) {
        return String.join("\r\n",
                requestLine,
                "Host: localhost:8080 ",
                "Accept: text/html;q=0.1 ",
                "Connection: keep-alive",
                "Content-Length: " + body.getBytes().length,
                "",
                body);
    }

    private static HttpRequest httpRequest_생성(String requestMessage) throws IOException {
        HttpRequest httpRequest;
        try (InputStream inputStream = new ByteArrayInputStream(requestMessage.getBytes());
             BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            httpRequest = HttpRequest.of(bufferedReader);
        }
        return httpRequest;
    }
}
