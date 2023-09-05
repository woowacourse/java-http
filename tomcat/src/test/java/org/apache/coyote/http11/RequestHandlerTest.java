package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import org.apache.coyote.http11.request.Request;
import org.apache.coyote.http11.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RequestHandlerTest {

    @Test
    @DisplayName("로그인에 성공하면 상태코드 302응답을 반환한다.")
    void handleLoginPage() throws IOException {
        final String responseBody = "account=gugu&password=password";
        final String contentLength = "Content-Length: " + responseBody.getBytes().length;

        //given
        final String request = String.join("\r\n",
                "POST /login HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive",
                contentLength,
                "",
                responseBody);
        final BufferedReader reader = new BufferedReader(new StringReader(request));
        final Request convertedRequest = Request.convert(reader);
        final RequestHandler requestHandler = new RequestHandler(convertedRequest);

        //when
        final Response response = requestHandler.generateResponse();

        //then
        final String expected = String.join("\r\n",
                "HTTP/1.1 302 FOUND ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 0 ",
                "Location: /index.html ",
                "Set-Cookie: JSESSIONID="
        );
        assertThat(response.parse()).contains(expected);
    }
}
