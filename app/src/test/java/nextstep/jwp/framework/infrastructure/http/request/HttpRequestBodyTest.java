package nextstep.jwp.framework.infrastructure.http.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("HttpRequestBody 단위 테스트")
class HttpRequestBodyTest {

    @DisplayName("getContentAsAttributes 메서드는")
    @Nested
    class Describe_getContentAsAttributes {

        @DisplayName("Key-Value 형식으로 들어온 Content를")
        @Nested
        class Context_content_kev_value_form {

            @DisplayName("Map으로 변환한다.")
            @Test
            void it_returns_map() {
                // given
                String content = "name=kevin&pass=123";
                HttpRequestBody httpRequestBody = new HttpRequestBody(content);

                // when
                Map<String, String> attributes = httpRequestBody.getContentAsAttributes();

                // then
                assertThat(attributes).containsEntry("name", "kevin")
                    .containsEntry("pass", "123");
            }
        }
    }
}
