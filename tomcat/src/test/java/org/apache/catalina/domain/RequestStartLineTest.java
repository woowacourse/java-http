package org.apache.catalina.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.http.enums.HttpMethod;
import com.techcourse.exception.BadRequestException;
import java.util.List;
import org.apache.catalina.domain.request.RequestStartLine;
import org.junit.jupiter.api.Test;

class RequestStartLineTest {

    @Test
    void 정상적인_스타트라인_파싱() {
        // given
        List<String> requestLines = List.of("GET /index.html HTTP/1.1");

        // when
        RequestStartLine result = RequestStartLine.from(requestLines);

        // then
        assertThat(result.method()).isEqualTo(HttpMethod.GET);
        assertThat(result.path()).isEqualTo("/index.html");
        assertThat(result.version()).isEqualTo("HTTP/1.1");
    }

    @Test
    void 쿼리_파라미터가_있는_경로_파싱() {
        // given
        List<String> requestLines = List.of("GET /login?account=admin&password=123 HTTP/1.1");

        // when
        RequestStartLine result = RequestStartLine.from(requestLines);

        // then
        assertThat(result.method()).isEqualTo(HttpMethod.GET);
        assertThat(result.path()).isEqualTo("/login");
        assertThat(result.version()).isEqualTo("HTTP/1.1");
    }

    @Test
    void POST_요청_파싱() {
        // given
        List<String> requestLines = List.of("POST /api/users HTTP/1.1");

        // when
        RequestStartLine result = RequestStartLine.from(requestLines);

        // then
        assertThat(result.method()).isEqualTo(HttpMethod.POST);
        assertThat(result.path()).isEqualTo("/api/users");
        assertThat(result.version()).isEqualTo("HTTP/1.1");
    }

    @Test
    void null_입력시_예외_발생() {
        // given

        // when & then
        assertThatThrownBy(() -> {
            RequestStartLine.from(null);
        }).isInstanceOf(BadRequestException.class);
    }

    @Test
    void 빈_문자열_입력시_예외_발생() {
        // given
        List<String> requestLines = List.of("");

        // when & then
        assertThatThrownBy(() -> {
            RequestStartLine.from(requestLines);
        }).isInstanceOf(BadRequestException.class);
    }

    @Test
    void 공백만_있는_문자열_입력시_예외_발생() {
        // given
        List<String> requestLines = List.of("   ");

        // when & then
        assertThatThrownBy(() -> {
            RequestStartLine.from(requestLines);
        }).isInstanceOf(BadRequestException.class);
    }

    @Test
    void 잘못된_형식의_스타트라인_예외_발생() {
        // given
        List<String> requestLines = List.of("GET /index.html");

        // when & then
        assertThatThrownBy(() -> {
            RequestStartLine.from(requestLines);
        }).isInstanceOf(BadRequestException.class);
    }

    @Test
    void 너무_많은_토큰이_있는_경우_예외_발생() {
        // given
        List<String> requestLines = List.of("GET /index.html HTTP/1.1 extra");

        // when & then
        assertThatThrownBy(() -> {
            RequestStartLine.from(requestLines);
        }).isInstanceOf(BadRequestException.class);
    }

    @Test
    void path_값이_없거나_슬래시만_있으면_index_html_로_연결() {
        // given
        List<String> requestLines1 = List.of("GET / HTTP/1.1");
        List<String> requestLines2 = List.of("GET  HTTP/1.1");

        // when
        RequestStartLine result1 = RequestStartLine.from(requestLines1);
        RequestStartLine result2 = RequestStartLine.from(requestLines2);

        // then
        assertThat(result1.path()).isEqualTo("/index.html");
        assertThat(result2.path()).isEqualTo("/index.html");
    }
}
