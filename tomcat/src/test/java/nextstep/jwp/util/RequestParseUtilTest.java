package nextstep.jwp.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import java.util.Map.Entry;
import org.junit.jupiter.api.Test;

class RequestParseUtilTest {

    @Test
    void 헤더에서_헤더정보를_반환한다() {
        String header = "Host: localhost:8080";
        Entry<String, String> expected = Map.entry("Host", "localhost:8080");
        Entry<String, String> actual = RequestParseUtil.getRequstHeader(header);

        assertThat(actual).isEqualTo(expected);
    }
}
