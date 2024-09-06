package org.apache.coyote.http11;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import org.apache.coyote.http11.request.HttpRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpRequestTest {

    @DisplayName("주어진 요청을 HttpRequest로 바꾼다.")
    @Test
    void extractRequestBody() throws IOException {
        // given
        String requestBody = "account=validUser&password=correctPassword";
        final String request = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: " + requestBody.length(),
                "",
                requestBody);
        BufferedReader bufferedReader = new BufferedReader(new StringReader(request));

        // when
        HttpRequest httpRequest = new HttpRequest(bufferedReader);

        // then
        assertAll(
                () -> assertThat(httpRequest.getHttpMethod()).isEqualTo("POST"),
                () -> assertThat(httpRequest.getURI()).isEqualTo("/login"),
                () -> assertThat(httpRequest.getBody()).isNotNull()
        );
    }
}
