package org.apache.coyote.http11.request.header;

import org.apache.coyote.http11.common.header.HeaderProperty;
import org.apache.coyote.http11.request.headers.RequestHeaders;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.net.http.HttpHeaders;

import static org.assertj.core.api.Assertions.assertThat;

class RequestHeadersTest {

    @Test
    @DisplayName("문자열로 주어진 요청 헤더 각 요소의 name과 value를 Map의 형태로 저장한다.")
    void stringToRequestHeader() {
        // given
        final String givenRequestHeader = String.join("\r\n",
                "Host: localhost:8080 ",
                "Connection: keep-alive "
        );

        // when
        final RequestHeaders actual = RequestHeaders.from(givenRequestHeader);

        // then
        SoftAssertions.assertSoftly(softAssertions -> {
            softAssertions.assertThat(actual.getRequestHeader()).hasSize(2);
            softAssertions.assertThat(actual.getRequestHeader().get("Host")).isEqualTo("localhost:8080");
            softAssertions.assertThat(actual.getRequestHeader().get("Connection")).isEqualTo("keep-alive");
        });
    }

    @Test
    @DisplayName("RequestHeaders의 Content-Length가 null이면 true를 반환한다.")
    void isContentLengthNull_true() {
        // given
        final String headerString = "aaa: bbb";
        final RequestHeaders requestHeaders = RequestHeaders.from(headerString);

        // when
        final boolean actual = requestHeaders.isContentLengthNull();

        // then
        assertThat(actual).isTrue();
    }

    @Test
    @DisplayName("RequestHeaders에 Content-Length각 존재한다면 false를 반환한다.")
    void isContentLengthNull_false() {
        // given
        final String headerString = "Content-Length: 10";
        final RequestHeaders requestHeaders = RequestHeaders.from(headerString);

        // when
        final boolean actual = requestHeaders.isContentLengthNull();

        // then
        assertThat(actual).isFalse();
    }

    @Test
    @DisplayName("RequestHeaders의 Content-Length 값을 반환하는데, 존재하지 않는다면 0을 반환한다.")
    void getContentLengthZero() {
        // given
        final String headerString = "aaa: bbb";
        final RequestHeaders requestHeaders = RequestHeaders.from(headerString);

        // when
        final int actual = requestHeaders.getContentLength();

        // then
        assertThat(actual).isZero();
    }

    @Test
    @DisplayName("RequestHeaders의 Content-Length 값을 반환한다.")
    void getContentLengthInteger() {
        // given
        final int expect = 10;
        final String headerString = "Content-Length: " + expect;
        final RequestHeaders requestHeaders = RequestHeaders.from(headerString);

        // when
        final int actual = requestHeaders.getContentLength();

        // then
        assertThat(actual).isEqualTo(10);
    }

    @Test
    @DisplayName("RequestHeader의 속성값으로 대응하는 값을 찾을 수 있다.")
    void search() {
        // given
        final String expect = "10";
        final String headerString = "Content-Length: " + expect;
        final RequestHeaders requestHeaders = RequestHeaders.from(headerString);

        // when
        final String actual = requestHeaders.search(HeaderProperty.CONTENT_LENGTH);

        assertThat(actual).isEqualTo(expect);
    }

    @Test
    @DisplayName("RequestHeaders에 주어진 속성이 존재하면 true를 반환한다.")
    void containsKey_true() {
        // given
        final String expect = "10";
        final String headerString = "Content-Length: " + expect;
        final RequestHeaders requestHeaders = RequestHeaders.from(headerString);

        // when
        final boolean actual = requestHeaders.containsKey(HeaderProperty.CONTENT_LENGTH);

        // then
        assertThat(actual).isTrue();
    }

    @Test
    @DisplayName("RequestHeaders에 주어진 속성이 존재하지 않으면 false를 반환한다.")
    void containsKey_false() {
        // given
        final String headerString = "aaa: bbb";
        final RequestHeaders requestHeaders = RequestHeaders.from(headerString);

        // when
        final boolean actual = requestHeaders.containsKey(HeaderProperty.CONTENT_LENGTH);

        // then
        assertThat(actual).isFalse();
    }
}
