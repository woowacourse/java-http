package org.was.controller;

import static java.util.Map.entry;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.apache.coyote.http11.HttpStatusCode;
import org.junit.jupiter.api.Test;

class ResponseResultTest {

    @Test
    void 상태코드와_헤더로_결과를_생성() {
        // given, when
        String headerAttribute = "Set-Cookie";
        String headerKeyAndValue = "ted=1234";

        ResponseResult actual = ResponseResult
                .status(HttpStatusCode.OK)
                .header(headerAttribute, headerKeyAndValue)
                .build();

        // then
        assertAll(
                () -> assertThat(actual.getStatusCode()).isEqualTo(HttpStatusCode.OK),
                () -> assertThat(actual.getHeaders()).containsOnly(entry(headerAttribute, headerKeyAndValue))
        );
    }

    @Test
    void 상태코드와_헤더와_본문으로_결과를_생성() {
        // given, when
        String headerAttribute = "Set-Cookie";
        String headerKeyAndValue = "ted=1234";
        String body = "hello world";

        ResponseResult actual = ResponseResult
                .status(HttpStatusCode.OK)
                .header(headerAttribute, headerKeyAndValue)
                .body(body);

        // then
        assertAll(
                () -> assertThat(actual.getStatusCode()).isEqualTo(HttpStatusCode.OK),
                () -> assertThat(actual.getHeaders()).containsOnly(entry(headerAttribute, headerKeyAndValue)),
                () -> assertThat(actual.getBody()).isEqualTo(body)
        );
    }

    @Test
    void 상태코드와_헤더와_응답경로로_결과를_생성() {
        // given, when
        String headerAttribute = "Set-Cookie";
        String headerKeyAndValue = "ted=1234";
        String path = "/login.html";

        ResponseResult actual = ResponseResult
                .status(HttpStatusCode.OK)
                .header(headerAttribute, headerKeyAndValue)
                .path(path);

        // then
        assertAll(
                () -> assertThat(actual.getStatusCode()).isEqualTo(HttpStatusCode.OK),
                () -> assertThat(actual.getHeaders()).containsOnly(entry(headerAttribute, headerKeyAndValue)),
                () -> assertThat(actual.getPath()).isEqualTo(path)
        );
    }
}
