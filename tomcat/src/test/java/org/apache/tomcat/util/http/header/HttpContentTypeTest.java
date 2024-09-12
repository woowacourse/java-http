package org.apache.tomcat.util.http.header;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpContentTypeTest {

    @DisplayName("Content-Type에 맞게 인코딩한 결과를 알 수 있다.")
    @Test
    void encoding() {
        String actual = HttpContentType.encoding("text/html");
        String expected = "text/html;charset=utf-8";
        assertThat(actual).isEqualTo(expected);
    }
}
