package org.apache.coyote.http11.frontcontroller;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.coyote.http11.httpmessage.ContentType;
import org.apache.coyote.http11.httpmessage.request.Headers;
import org.apache.coyote.http11.httpmessage.request.Http11Version;
import org.apache.coyote.http11.httpmessage.request.HttpRequest;
import org.apache.coyote.http11.httpmessage.response.HttpResponse;
import org.apache.coyote.http11.httpmessage.response.HttpStatus;
import org.apache.coyote.http11.httpmessage.response.StatusLine;
import org.junit.jupiter.api.Test;

class FrontControllerTest {

    @Test
    void file_요청의_httpRequest를_받으면_httpResponse_내부의_값을_세팅한다() throws IOException {
        // given
        String requestMessage = 요청_메시지("GET / HTTP/1.1 ", "");
        HttpRequest httpRequest = httpRequest_생성(requestMessage);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        HttpResponse httpResponse = new HttpResponse(outputStream);

        FrontController frontController = new FrontController();

        // when
         frontController.doDispatch(httpRequest, httpResponse);

        // then
        String expectedBody = "Hello world!";

        assertThat(httpResponse).extracting("statusLine", "headers", "responseBody")
                .containsExactly(new StatusLine(Http11Version.HTTP_11_VERSION, HttpStatus.OK)
                        , getDefaultHeaders(expectedBody)
                        , expectedBody
                );
        outputStream.close();
    }

    @Test
    void api_요청의_httpRequest를_받으면_httpResponse_내부의_값을_세팅한다() throws IOException {
        // given
        String body = "account=gugu&password=password&email=hkkang@woowahan.com";
        String requestMessage = 요청_메시지("POST /register HTTP/1.1 ", body);
        HttpRequest httpRequest = httpRequest_생성(requestMessage);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        HttpResponse httpResponse = new HttpResponse(outputStream);

        FrontController frontController = new FrontController();

        // when
        frontController.doDispatch(httpRequest, httpResponse);

        // then
        Headers expectedHeaders = new Headers(new LinkedHashMap<>());
        expectedHeaders.putAll(Map.of("Location", "/index.html "));
        expectedHeaders.putAll(getDefaultHeaders("").getHeaders());

        assertThat(httpResponse).usingRecursiveComparison()
                .isEqualTo(new HttpResponse(outputStream)
                        .setStatusLine(new StatusLine(Http11Version.HTTP_11_VERSION, HttpStatus.FOUND))
                        .setHeaders(expectedHeaders)
                        .setResponseBody("")
                );
        outputStream.close();
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

    private static Headers getDefaultHeaders(String expectedBody) {
        LinkedHashMap<String, Object> headers = new LinkedHashMap<>();
        headers.putAll(Map.of("Content-Type", ContentType.HTML.getValue() + ";charset=utf-8 ",
                "Content-Length", expectedBody.getBytes().length + " "));
        return new Headers(headers);
    }
}
