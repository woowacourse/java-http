package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.apache.coyote.http11.Http11ResponseHeader.Builder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class Http11ResponseHeaderTest {

    @DisplayName("Builder를 사용해서 header를 추가한다.")
    @Test
    void addHeader() {
        Builder builder = Http11ResponseHeader.builder();

        Http11ResponseHeader responseHeader = builder.addHeader("Content-Type", List.of("text/html")).build();

        assertThat(responseHeader.getHeader(Http11HeaderName.CONTENT_TYPE.getName())).isEqualTo("text/html");
        assertThat(responseHeader.getAllHeaders()).isEqualTo("Content-Type: text/html \r\n");
    }
}
