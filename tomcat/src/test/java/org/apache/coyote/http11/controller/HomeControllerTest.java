package org.apache.coyote.http11.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.BufferedReader;
import java.io.StringReader;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.exception.HttpRequestException;
import org.apache.coyote.http11.response.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HomeControllerTest {

    @Test
    @DisplayName("GET 메소드에 해당하는 로직을 처리해서 HttpResponse를 반환한다.")
    void getService() throws Exception {
        // given
        String startLine = "GET / HTTP/1.1 ";
        String rawRequest = String.join("\r\n",
                "Content-Type: text/html",
                "",
                ""
        );
        StringReader stringReader = new StringReader(rawRequest);
        BufferedReader br = new BufferedReader(stringReader);
        HttpRequest request = HttpRequest.from(br, startLine);

        HomeController homeController = new HomeController();

        String expectedResponse = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 12 ",
                "",
                "Hello world!"
        );

        // when
        HttpResponse response = homeController.service(request);
        String actualResponse = new String(response.convertToBytes());

        // then
        assertThat(actualResponse).isEqualTo(expectedResponse);
    }

    @Test
    @DisplayName("POST 메소드 요청이 오면 예외를 반환한다.")
    void throwsWhenPostService() throws Exception {
        // given
        String startLine = "POST / HTTP/1.1 ";
        String rawRequest = String.join("\r\n",
                "Content-Type: text/html",
                "",
                ""
        );
        StringReader stringReader = new StringReader(rawRequest);
        BufferedReader br = new BufferedReader(stringReader);
        HttpRequest request = HttpRequest.from(br, startLine);

        HomeController homeController = new HomeController();

        // when & then
        assertThatThrownBy(() -> homeController.service(request))
                .isInstanceOf(HttpRequestException.MethodNotAllowed.class)
                .hasMessage("허용되지 않는 HTTP Method 입니다.");
    }
}
