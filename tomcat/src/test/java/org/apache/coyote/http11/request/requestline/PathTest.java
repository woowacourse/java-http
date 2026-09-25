package org.apache.coyote.http11.request.requestline;

import org.apache.coyote.http11.exception.BadRequestException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.nio.file.Path;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PathTest {
    @ParameterizedTest
    @ValueSource(strings = {
            "/../etc/passwd",
            "/%2E%2E/etc/passwd",
            "/.%2e/etc/passwd",
            "/..%2Fetc/passwd",
            "/static/../../etc/passwd",
            "/..%5C..%5Cwindows",   // %5C = 역슬래시
            "/index.html%00.png",
            "index.html",
            "",
    })
    void 위험하거나_잘못된_경로는_거부한다(final String raw) {
        assertThatThrownBy(() -> RequestPath.from(raw))
                .isInstanceOf(BadRequestException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "/",
            "/index.html",
            "/files/v1..2.zip",   // '..'가 포함됐지만 세그먼트는 아님
            "/c++/guide",
            "/./index.html",
    })
    void 정상_경로는_허용한다(final String raw) {
        assertThatCode(() -> RequestPath.from(raw)).doesNotThrowAnyException();
    }

    @Test
    void 플러스는_공백으로_바뀌지_않는다() {
        assertThat(RequestPath.from("/c%2B%2B/a+b").getValue()).isEqualTo("/c++/a+b");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "/admin%2Fusers",
            "/admin%2fusers",       // 소문자
            "/a%2F..%2Fb",
    })
    void 인코딩된_슬래시는_거부한다(final String raw) {
        assertThatThrownBy(() -> RequestPath.from(raw))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void 이중_인코딩된_슬래시는_리터럴로_남는다() {
        assertThat(RequestPath.from("/a%252Fb").getValue()).isEqualTo("/a%2Fb");
    }

    @Test
    void 쿼리_값의_인코딩된_슬래시는_허용한다() {
        final RequestUri uri = RequestUri.from("/login?redirect=%2Fmypage");
        assertThat(uri.getQueryParameter("redirect")).hasValue("/mypage");
    }

    @Test
    void 같은_경로는_Map의_같은_키로_동작한다() {
        final Map<RequestPath, String> map = Map.of(RequestPath.from("/login"), "login");

        assertThat(map.get(RequestPath.from("/login"))).isEqualTo("login");
    }

    @Test
    void 인코딩이_달라도_디코딩_결과가_같으면_같은_경로다() {
        assertThat(RequestPath.from("/%69ndex.html")).isEqualTo(RequestPath.from("/index.html"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"/a%0Ab", "/a%0D%0Ab", "/a%09b", "/a%7Fb"})
    void 제어_문자는_거부한다(final String raw) {
        assertThatThrownBy(() -> RequestPath.from(raw))
                .isInstanceOf(BadRequestException.class);
    }
}