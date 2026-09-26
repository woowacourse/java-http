package org.apache.coyote.http11.request.requestline;

import org.apache.coyote.http11.exception.BadRequestException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class RequestLineTest {
    @Test
    void 정상_요청_라인을_파싱한다() {
        final RequestLine line = RequestLine.from("GET /index.html?a=1 HTTP/1.1");

        assertThat(line.getMethod()).isEqualTo(HttpMethod.GET);
        assertThat(line.getPath()).isEqualTo(RequestPath.from("/index.html"));
        assertThat(line.getQueryParameter("a")).hasValue("1");
        assertThat(line.getVersion()).isEqualTo(HttpVersion.HTTP_1_1);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "GET  /index.html HTTP/1.1",     // 이중 공백
            " GET /index.html HTTP/1.1",     // 앞 공백
            "GET\t/index.html HTTP/1.1",     // 탭
            "GET /index.html",               // 버전 없음
            "GET /a b HTTP/1.1",             // 경로에 공백
    })
    void 형식이_잘못된_요청_라인은_거부한다(final String raw) {
        assertThatThrownBy(() -> RequestLine.from(raw))
                .isInstanceOf(BadRequestException.class);
    }
}