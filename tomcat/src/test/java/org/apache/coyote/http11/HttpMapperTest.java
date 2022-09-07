package org.apache.coyote.http11;

import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.HttpRequestFactory;
import org.apache.coyote.http11.response.Body;
import org.apache.coyote.http11.response.Header;
import org.apache.coyote.http11.response.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpMapperTest {

    @DisplayName("로그인이 실패하면 401.html로 리다이렉트한다.")
    @Test
    void createResponse_loginFail() throws IOException {
        //given
        final String string = String.join("\r\n",
                "POST /login?account=gugu&passord=가짜 HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");
        InputStream inputStream = new ByteArrayInputStream(string.getBytes());
        HttpRequest httpRequest = HttpRequestFactory.parse(inputStream);

        //when
        HttpResponse response = HttpMapper.createResponse(httpRequest);
        Header responseHeader = response.getHeader();
        Body responseBody = response.getBody();

        //then
        assertAll(
                () -> responseHeader.getRequestLine().contains(HttpStatus.FOUND.code),
                () -> responseHeader.getHeaders().contains("Location: 401.html")
        );
    }

    @DisplayName("로그인이 성공하면 index.html로 리다이렉트한다.")
    @Test
    void createResponse_loginSuccess() throws IOException {
        //given
        final String string = String.join("\r\n",
                "POST /login?account=gugu&passord=password HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");
        InputStream inputStream = new ByteArrayInputStream(string.getBytes());
        HttpRequest httpRequest = HttpRequestFactory.parse(inputStream);

        //when
        HttpResponse response = HttpMapper.createResponse(httpRequest);
        Header responseHeader = response.getHeader();
        Body responseBody = response.getBody();

        //then
        assertAll(
                () -> responseHeader.getRequestLine().contains(HttpStatus.FOUND.code),
                () -> responseHeader.getHeaders().contains("Location: index.html")
        );
    }
}
