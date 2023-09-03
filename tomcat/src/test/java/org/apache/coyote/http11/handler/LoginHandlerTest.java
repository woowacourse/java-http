package org.apache.coyote.http11.handler;

import org.apache.coyote.http11.http.HttpRequest;
import org.apache.coyote.http11.http.ResponseEntity;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class LoginHandlerTest {

    private final LoginHandler loginHandler = new LoginHandler();

    @Test
    void 로그인에_성공하면_indexhtml_로_리다이렉션() throws IOException {
        String validAccount = "gugu";
        String validPassword = "password";
        final String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 80 ",
                "Content-Type: application/x-www-form-urlencoded ",
                "Accept: */* ",
                "",
                "account=" + validAccount + "&password=" + validPassword);

        BufferedReader input = RequestParser.requestToInput(httpRequest);
        ResponseEntity responseEntity = loginHandler.handle(HttpRequest.from(input));
        String response = responseEntity.generateResponseMessage();

        assertThat(response).contains(
                "Location: index.html",
                "HTTP/1.1 302 "
        );
    }

    @Test
    void 로그인에_실패하면_401html_로_리다이렉션() throws IOException {
        String invalidAccount = "leo";
        String invalidPassword = "password1234";
        final String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 80 ",
                "Content-Type: application/x-www-form-urlencoded ",
                "Accept: */* ",
                "",
                "account=" + invalidAccount + "&password=" + invalidPassword);

        BufferedReader input = RequestParser.requestToInput(httpRequest);
        ResponseEntity responseEntity = loginHandler.handle(HttpRequest.from(input));
        String response = responseEntity.generateResponseMessage();

        assertThat(response).contains(
                "Location: 401.html",
                "HTTP/1.1 302 "
        );
    }

}
