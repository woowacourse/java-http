package org.apache.coyote.http11.session;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class HttpCookieTest {

    @Test
    void 쿠키_파싱_테스트() {
        String cookies = "JSESSIONID=f51889e2-275a-471e-b5ed-9c794180c85f";
        HttpCookie httpCookie = HttpCookie.from(cookies);

        Optional<String> jSessionId = httpCookie.findJSessionId();

        assertAll(
                () -> assertThat(jSessionId).isNotEmpty(),
                () -> assertThat(jSessionId.get()).isEqualTo("f51889e2-275a-471e-b5ed-9c794180c85f")
        );
    }
}
