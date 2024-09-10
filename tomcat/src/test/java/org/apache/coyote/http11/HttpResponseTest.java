package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.catalina.http.HttpResponse;
import org.apache.catalina.http.body.HttpResponseBody;
import org.apache.catalina.http.header.HttpHeader;
import org.apache.catalina.http.header.HttpHeaders;
import org.apache.catalina.http.startline.HttpResponseLine;
import org.apache.catalina.http.startline.HttpStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class HttpResponseTest {

    @Test
    void getBytes() {
    }

    @DisplayName("응답에 헤더를 추가한다.")
    @Test
    void addHeader() {
        // given
        HttpHeaders header = new HttpHeaders();
        HttpResponse response = new HttpResponse(
                new HttpResponseLine("HTTP/1.1"),
                header,
                new HttpResponseBody()
        );

        // when
        response.addHeader(HttpHeader.CONTENT_TYPE, "text/plain");

        // then
        assertThat(header.get(HttpHeader.CONTENT_TYPE)).isEqualTo("text/plain");
    }

    @DisplayName("응답의 상태 코드를 설정한다.")
    @Test
    void setStatus() {
        // given
        HttpResponseLine responseLine = new HttpResponseLine("HTTP/1.1");
        HttpResponse response = new HttpResponse(
                responseLine,
                new HttpHeaders(),
                new HttpResponseBody()
        );

        // when
        response.setStatus(HttpStatus.UNAUTHORIZED);

        // then
        assertThat(responseLine.stringify()).isEqualTo("HTTP/1.1 401 Unauthorized");
    }

    @DisplayName("응답의 body를 설정한다.")
    @Test
    void setBody() {
        // given
        HttpHeaders header = new HttpHeaders();
        HttpResponseBody responseBody = new HttpResponseBody();
        HttpResponse response = new HttpResponse(
                new HttpResponseLine("HTTP/1.1"),
                header,
                responseBody
        );

        // when
        String body = "it is body";
        response.setBody(body);

        // then
        assertThat(responseBody.getValue()).isEqualTo(body);
        assertThat(header.get(HttpHeader.CONTENT_LENGTH)).isEqualTo(String.valueOf(body.length()));
    }

    @DisplayName("응답 쿠키에 세션을 추가한다.")
    @Test
    void addSessionToCookies() {
        // given
        HttpHeaders header = new HttpHeaders();
        HttpResponse response = new HttpResponse(
                new HttpResponseLine("HTTP/1.1"),
                header,
                new HttpResponseBody()
        );

        // when
        response.addSessionToCookies("session");

        // then
        assertThat(header.getSessionFromCookie()).get().isEqualTo("session");
    }

    @DisplayName("응답이 반환하기 유효하면 true를 반환한다.")
    @Test
    void isValid_true() {
        // given
        HttpResponseLine responseLine = new HttpResponseLine("HTTP/1.1");
        HttpResponse response = new HttpResponse(
                responseLine,
                new HttpHeaders(),
                new HttpResponseBody()
        );

        // when&then
        assertThat(response.isValid()).isTrue();
    }

    @DisplayName("응답이 반환하기 유효하지 않으면 false를 반환한다.")
    @Test
    void isValid_false() {
        // given
        HttpResponseLine responseLine = new HttpResponseLine("HTTP/1.1");
        responseLine.setStatus(HttpStatus.UNAUTHORIZED);
        HttpResponse response = new HttpResponse(
                responseLine,
                new HttpHeaders(),
                new HttpResponseBody()
        );

        // when&then
        assertThat(response.isValid()).isTrue();
    }
}
