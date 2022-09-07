package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class HttpRequestTest {

    @Test
    void request를_파싱한다() {
        // given
        StartLine startLine = new StartLine("GET /eden HTTP/1.1 ");
        RequestHeaders requestHeaders = RequestHeaders.of(List.of("Content-Length: 10"));
        RequestBody requestBody = RequestBody.of("name=eden&nickName=king");

        // when
        HttpRequest httpRequest = new HttpRequest(startLine, requestHeaders, requestBody);

        // then
        Assertions.assertAll(
                () -> assertThat(httpRequest.getHttpMethod()).isEqualTo(HttpMethod.GET),
                () -> assertThat(httpRequest.getPath()).isNotNull(),
                () -> assertThat(httpRequest.getQueryParameters().isEmpty()).isTrue()
        );
    }

    @Test
    void request_path를_확인할_수_있다() {
        StartLine startLine = new StartLine("GET /eden HTTP/1.1 ");
        RequestHeaders requestHeaders = RequestHeaders.of(List.of("Content-Length: 10"));
        RequestBody requestBody = RequestBody.of("name=eden&nickName=king");

        // when
        HttpRequest httpRequest = new HttpRequest(startLine, requestHeaders, requestBody);

        // then
        assertThat(httpRequest.checkRequestPath("/eden")).isTrue();
    }
}
