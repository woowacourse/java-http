package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;
import nextstep.jwp.controller.ResponseEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpResponseTest {

    @DisplayName("기본값을 가진 객체를 생성한다.")
    @Test
    void createBasicResponse() {
        // given
        // when
        final HttpResponse basicResponse = HttpResponse.createBasicResponse();

        // then
        assertAll(
            () -> assertEquals("HTTP/1.1 200 OK", basicResponse.getResponseLine().toString()),
            () -> assertEquals("", basicResponse.getBody())
        );
    }

    @DisplayName("헤더에 속성을 추가한다.")
    @Test
    void addAttribute() {
        // given
        final HttpResponse basicResponse = HttpResponse.createBasicResponse();
        final HttpHeaders responseEntityHeaders = new HttpHeaders(Map.of(
            HttpHeaderName.CONTENT_TYPE, "text/html"
        ));
        final ResponseEntity responseEntity = new ResponseEntity(HttpResponseStatusLine.OK(), responseEntityHeaders,
            "body");

        // when
        basicResponse.addAttributes(responseEntity);

        // then
        assertAll(
            () -> assertThat(basicResponse.getHeaders().getHeaderValue(HttpHeaderName.CONTENT_TYPE)).isEqualTo(
                "text/html"),
            () -> assertThat(basicResponse.getHeaders().getHeaderValue(HttpHeaderName.CONTENT_LENGTH)).isEqualTo("4"),
            () -> assertThat(basicResponse.getResponseLine().toString()).isEqualTo("HTTP/1.1 200 OK"),
            () -> assertThat(basicResponse.getBody()).isEqualTo("body")
        );
    }

    @DisplayName("리다이렉트 응답을 생성한다.")
    @Test
    void sendRedirect() {
        // given
        final HttpResponse basicResponse = HttpResponse.createBasicResponse();

        // when
        basicResponse.sendRedirect(StaticPages.INDEX_PAGE);

        // then
        assertAll(
            () -> assertThat(basicResponse.getHeaders().getHeaderValue(HttpHeaderName.LOCATION)).isEqualTo(
                StaticPages.INDEX_PAGE.getFileName()),
            () -> assertThat(basicResponse.getResponseLine().toString()).isEqualTo("HTTP/1.1 302 Found")
        );
    }
}
