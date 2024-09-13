package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RequestURIFactoryTest {

    @Test
    @DisplayName("RequestURI를 정상 생성한다.")
    void testCreateValidURI() {
        String uriString = "https://example.com/login.html?key=value";

        RequestURI requestURI = RequestURIFactory.create(uriString);

        assertAll(
                () -> assertThat(requestURI.getPathWithExtension()).isEqualTo("/login.html"),
                () -> assertThat(requestURI.getExtension()).isEqualTo("html"),
                () -> assertThat(requestURI.getQueryString()).containsExactlyInAnyOrderEntriesOf(Map.of("key", "value"))
        );
    }

    @Test
    @DisplayName("잘못된 URI 형식일 경우 예외가 발생한다.")
    void testCreateInvalidURI() {
        String invalidUriString = "https://example.com/invalid uri";

        assertThatThrownBy(() -> RequestURIFactory.create(invalidUriString))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("잘못된 URI 형식입니다.");
    }

    @Test
    @DisplayName("쿼리 문자열이 없는 경우 빈 Map을 반환한다.")
    void testParseQueryStringNoQuery() {
        String uriString = "https://example.com/path/to/resource.html";
        RequestURI requestURI = RequestURIFactory.create(uriString);

        assertThat(requestURI.getQueryString()).isEmpty();
    }
}
