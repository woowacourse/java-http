package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.apache.coyote.http11.message.HttpVersion;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class RequestGeneralTest {

    @Test
    void createRequestGeneralWithStartLine() {
        // given
        String startLine = "GET /hello?a=1&b=2&c=3 HTTP/1.1";
        // when
        RequestGeneral general = RequestGeneral.parse(startLine);
        // then
        Assertions.assertAll(
                () -> assertThat(general.getMethod()).isEqualTo(RequestMethod.GET),
                () -> assertThat(general.getPath().getPath()).isEqualTo("/hello"),
                () -> assertThat(general.getPath().getQueryParameters())
                        .contains(Map.entry("a", "1"), Map.entry("b", "2"), Map.entry("c", "3")),
                () -> assertThat(general.getHttpVersion()).isEqualTo(HttpVersion.HTTP11)
        );
    }
}
