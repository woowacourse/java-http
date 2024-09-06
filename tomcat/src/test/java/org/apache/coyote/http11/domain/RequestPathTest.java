package org.apache.coyote.http11.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.apache.coyote.http11.request.domain.RequestPath;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RequestPathTest {

    @Test
    @DisplayName("경로가 Null인 경우 에러를 발생한다.")
    void getPath() {
        assertThrows(NullPointerException.class, () -> new RequestPath(null));
    }

    @Test
    @DisplayName("경로에서 Query만 뽑아온다.")
    void getQueryString() {
        RequestPath requestPath = new RequestPath("/login?account=gugu&password=password");

        assertEquals(requestPath.getQueryString(), "account=gugu&password=password");
    }

    @Test
    @DisplayName("경로가 / 일 경우 true를 반환한다.")
    void isDefaultPath() {
        RequestPath requestPath = new RequestPath("/");

        assertTrue(requestPath.isDefaultPath());
    }

    @Test
    @DisplayName("파일 확장자를 가져온다.")
    void findExtension() {
        RequestPath requestPath = new RequestPath("/index.html");

        assertEquals(requestPath.findExtension(), "html");
    }

    @Test
    @DisplayName("정적 파일 리소스의 경로를 붙여 가져온다.")
    void toResourcePath() {
        RequestPath requestPath = new RequestPath("/index.html");

        assertEquals(requestPath.toResourcePath(), "static/index.html");
    }
}
