package nextstep.jwp.http.common;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.junit.jupiter.api.Test;

class ParameterExtractorTest {

    @Test
    void extract() {
        final Map<String, String> actual = ParameterExtractor.extract("test=1&test2=qq");

        Map<String, String> expected = new HashMap<>();
        expected.put("test", "1");
        expected.put("test2", "qq");

        assertThat(actual)
            .usingRecursiveComparison()
            .isEqualTo(expected);
    }
}