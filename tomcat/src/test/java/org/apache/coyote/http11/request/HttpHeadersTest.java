package org.apache.coyote.http11.request;

import static org.junit.jupiter.api.Assertions.*;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import support.FakeRequests;

class HttpHeadersTest {

    @Test
    @DisplayName("{키:값} 이외의 형식일 때 예외가 발생한다.")
    void from() {
        Assertions.assertThatThrownBy(() -> HttpHeaders.from(FakeRequests.invalidHeaderRequest)).isInstanceOf(
                IllegalArgumentException.class);
    }
}
