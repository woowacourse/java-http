package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpRequestTest {

    @DisplayName("filSource를 반환한다.")
    @Test
    void getFileSource() throws IOException {
        String request = "GET /index.html HTTP/1.1\n" +
                "Host: localhost:8080\n" +
                "Connection: keep-alive\n" +
                "";
        InputStream inputStream = new ByteArrayInputStream(request.getBytes());
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        HttpRequest httpRequest = HttpRequest.from(bufferedReader);
        String expected = "/index.html";

        String actual = httpRequest.getPath();

        assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("FormData를 포함한 요청에서 filSource를 반환한다.")
    @Test
    void getFileSource_IncludingFormdata() throws IOException {
        String request = "GET /login HTTP/1.1\n" +
                "Host: localhost:8080\n" +
                "Connection: keep-alive\n" +
                "\n" +
                "account=gugu&password=password";
        InputStream inputStream = new ByteArrayInputStream(request.getBytes());
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        HttpRequest httpRequest = HttpRequest.from(bufferedReader);
        String expected = "/login";

        String actual = httpRequest.getPath();

        assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("formData을 포함한 요청에서 formData 값을 반환한다.")
    @Test
    void getQueryParamValueOf() throws IOException {
        String request = "POST /login HTTP/1.1\n"
                + "Host: localhost:8080\n"
                + "Connection: keep-alive\n"
                + "Content-Length: 80\n"
                + "Content-Type: application/x-www-form-urlencoded\n"
                + "Accept: */*\n"
                + "\n"
                + "account=gugu&password=password";
        InputStream inputStream = new ByteArrayInputStream(request.getBytes());
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        HttpRequest httpRequest = HttpRequest.from(bufferedReader);

        String accountValue = httpRequest.getParamValueOf("account");
        String passwordValue = httpRequest.getParamValueOf("password");

        assertAll(
                () -> assertThat(accountValue).isEqualTo("gugu"),
                () -> assertThat(passwordValue).isEqualTo("password")
        );
    }
}