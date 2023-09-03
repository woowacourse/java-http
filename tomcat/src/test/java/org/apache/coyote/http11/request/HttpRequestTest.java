package org.apache.coyote.http11.request;

import org.apache.coyote.http11.request.body.RequestBody;
import org.apache.coyote.http11.request.headers.RequestHeaders;
import org.apache.coyote.http11.request.requestLine.RequestLine;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import static org.assertj.core.api.Assertions.assertThat;

class HttpRequestTest {

    @Test
    @DisplayName("문자열로 주어진 Http 요청을 HttpRequest로 변환한다.")
    void stringToHttpRequest() throws IOException {
        // given
        final String givenRequestLine = "GET /index.html HTTP/1.1 ";
        final String givenRequestHeaders = String.join("\r\n",
                "Host: localhost:8080 ",
                "Connection: keep-alive "
        );
        final String givenRequestBody = "body";
        final String givenHttpRequest= String.join("\r\n",
                givenRequestLine,
                givenRequestHeaders,
                "",
                givenRequestBody
        );

        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(givenHttpRequest.getBytes())));

        final RequestLine expectRequestLine = RequestLine.from(givenRequestLine);
        final RequestHeaders expectRequestHeaders = RequestHeaders.from(givenRequestHeaders);
        final RequestBody expectRequestBody = RequestBody.from(givenRequestBody);

        // when
        final HttpRequest actual = HttpRequest.from(bufferedReader);

        // then
        SoftAssertions.assertSoftly(softAssertions -> {
            softAssertions.assertThat(actual.getRequestLine()).isEqualTo(expectRequestLine);
            softAssertions.assertThat(actual.getRequestHeader()).isEqualTo(expectRequestHeaders);
            softAssertions.assertThat(actual.getRequestBody()).isEqualTo(expectRequestBody);
        });
    }
}
