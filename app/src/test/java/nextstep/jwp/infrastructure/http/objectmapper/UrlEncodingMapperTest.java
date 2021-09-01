package nextstep.jwp.infrastructure.http.objectmapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UrlEncodingMapperTest {

    @DisplayName("UrlEncoding 파싱 테스트")
    @Test
    void parse() {
        final String query = "a=b&b=c&d=&e=1";
        final UrlEncodingMapper urlEncodingMapper = new UrlEncodingMapper();

        final Map<String, String> expected = new HashMap<>();
        expected.put("a", "b");
        expected.put("b", "c");
        expected.put("d", "");
        expected.put("e", "1");

        final Map<String, String> actual = urlEncodingMapper.parse(query);
        assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("UrlEncoding 형식이 아닐 경우 예외 처리")
    @Test
    void invalidUrlEncodingFormat() {
        final String query = "a=&=b&b=c&d=&e=1";
        final UrlEncodingMapper urlEncodingMapper = new UrlEncodingMapper();

        assertThatIllegalArgumentException().isThrownBy(() -> urlEncodingMapper.parse(query));
    }
}