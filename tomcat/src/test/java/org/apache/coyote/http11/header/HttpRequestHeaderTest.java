package org.apache.coyote.http11.header;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpRequestHeaderTest {

    @DisplayName("contentLength를 읽을 수 있다")
    @Test
    void contentLength() throws IOException {
        String header = "Host: localhost:8080\n"
                + "Connection: keep-alive\n"
                + "Content-Length: 80\n"
                + "Content-Type: application/x-www-form-urlencoded\n"
                + "Accept: */*\n"
                + "\n";
        InputStream inputStream = new ByteArrayInputStream(header.getBytes());
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        HttpRequestHeader requestHeader = HttpRequestHeader.readRequestHeader(reader);

        assertThat(requestHeader.contentLength()).isEqualTo(80);
    }

    @DisplayName("쿠키의 존재 여부를 알 수 있다")
    @Test
    void hasCookie() throws IOException {
        String cookieExists = "Host: localhost:8080\n"
                + "Connection: keep-alive\n"
                + "Accept: */*\n"
                + "Cookie: yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46\n"
                + "\n";

        String cookieNoneExists = "Host: localhost:8080\n"
                + "Connection: keep-alive\n"
                + "Accept: */*\n"
                + "\n";

        InputStream cookieInputSteram = new ByteArrayInputStream(cookieExists.getBytes());
        InputStream noneCookieInputSteram = new ByteArrayInputStream(cookieNoneExists.getBytes());
        BufferedReader cookieReader = new BufferedReader(new InputStreamReader(cookieInputSteram));
        BufferedReader noneCookieReader = new BufferedReader(new InputStreamReader(noneCookieInputSteram));

        HttpRequestHeader cookieHeader = HttpRequestHeader.readRequestHeader(cookieReader);
        HttpRequestHeader noneCookieHeader = HttpRequestHeader.readRequestHeader(noneCookieReader);

        assertAll(
                () -> assertThat(cookieHeader.hasCookie()).isTrue(),
                () -> assertThat(noneCookieHeader.hasCookie()).isFalse()
        );
    }
}
