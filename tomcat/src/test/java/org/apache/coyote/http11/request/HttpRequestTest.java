package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.IOException;
import org.apache.coyote.http11.HttpHeader;
import org.apache.coyote.http11.request.startLine.HttpMethod;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpRequestTest {

    @DisplayName("HttpMessage를 읽어 HttpRequest 객체를 생성한다.")
    @Test
    void successReadTest() throws IOException {
        RequestReader requestReader = new FakeRequestReader();

        HttpRequest request = HttpRequest.read(requestReader);

        assertAll(
                () -> assertThat(request.getUri()).isEqualTo("/login"),
                () -> assertThat(request.getHttpMethod()).isEqualTo(HttpMethod.POST),
                () -> assertThat(request.getRequestHeaders().get(HttpHeader.CONTENT_TYPE.getName())).isPresent(),
                () -> assertThat(request.getRequestHeaders().get(HttpHeader.CONTENT_LENGTH.getName())).isPresent(),
                () -> assertThat(request.getRequestBody().get("account")).isEqualTo("gugu"),
                () -> assertThat(request.getRequestBody().get("password")).isEqualTo("password")
        );
    }
}
