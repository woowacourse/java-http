package nextstep.jwp.http.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.junit.jupiter.api.Test;

class ParamExtractorTest {

    @Test
    void extractParams() {
        String query = "password=";
        Map<String, String> map = ParamExtractor.extractParams(query);
        assertThat(map.get("password")).isBlank();
    }
}