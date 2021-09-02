package nextstep.jwp.framework.http.request.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class QueryParameterExtractorTest {

    @DisplayName("쿼리스트링 형태를 Map<String, String>으로 추출한다")
    @Test
    void extract() {
        final Map<String, String> extracted = QueryParameterExtractor.extract("key1=value1&key2=value2");
        assertThat(extracted).containsEntry("key1", "value1").containsEntry("key2", "value2");
    }
}