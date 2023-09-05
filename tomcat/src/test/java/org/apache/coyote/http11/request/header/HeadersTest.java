package org.apache.coyote.http11.request.header;

import org.apache.coyote.http11.response.header.Header;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class HeadersTest {

    @DisplayName("요청을 파싱해서 헤더를 생성한다.")
    @Test
    void create_headers_using_request() {
        // given
        List<String> request = List.of("Accept: text/javascript,*/*;q=0.1 ");

        // when
        Headers headers = Headers.from(request);

        // then
        assertThat(headers.getHeaderValue(Header.Accept).get())
                .isEqualTo("text/javascript,*/*;q=0.1 ");
    }
}
