package nextstep.jwp.framework.http;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class HttpHeadersTest {

    @Test
    @DisplayName("Queue 가 비어있을 때 poll 을 하면 무엇을 반환하는지 확인 테스트")
    void pollIfNullTest() {

        // given
        HttpHeaders httpHeaders = new HttpHeaders();

        // when
        final HttpHeader header = httpHeaders.poll();

        // then
        assertThat(header).isNull();
    }
}
