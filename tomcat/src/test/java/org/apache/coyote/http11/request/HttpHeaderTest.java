package org.apache.coyote.http11.request;

import java.util.LinkedList;
import java.util.Queue;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpHeaderTest {

    @Test
    @DisplayName("정적 팩토리 메소드는 입력 받은 값을 파싱하여 headers에 저장한다.")
    void of() {
        // given
        final Queue<String> rawHeader = new LinkedList<>();
        rawHeader.add("name: eve");

        // when
        final HttpHeader httpHeader = HttpHeader.of(rawHeader);

        // then
        Assertions.assertThat(httpHeader.getHeader("name")).isEqualTo("eve");
    }
}