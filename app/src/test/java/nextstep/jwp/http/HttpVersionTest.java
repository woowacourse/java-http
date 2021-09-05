package nextstep.jwp.http;

import nextstep.jwp.http.exception.HttpVersionException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class HttpVersionTest {

    @DisplayName("HTTP Version을 체크한다.")
    @Test
    void checkHttpVersion() {
        assertThatThrownBy(() -> {
            HttpVersion.checkHttpVersion("HTTP/1.1.1");
        }).isInstanceOf(HttpVersionException.class);

    }
}