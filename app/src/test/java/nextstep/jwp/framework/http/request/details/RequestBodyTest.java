package nextstep.jwp.framework.http.request.details;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RequestBodyTest {

    @DisplayName("RequestBody에 쿼리스트링 포맷으로 요청이 들어온다면 매핑된다")
    @Test
    void requestBodyAsQueryStringFormat() {
        final RequestBody requestBody = RequestBody.asQueryString("key1=value1&key2=value2");
        assertThat(requestBody.find("key1")).isEqualTo("value1");
        assertThat(requestBody.find("key2")).isEqualTo("value2");
    }
}
