package nextstep.jwp.http.common;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class HttpUriTest {

    @DisplayName("정적 경로이면 참을 반환한다.")
    @ParameterizedTest
    @ValueSource(strings = {"/login.html", "/login.html?user=bear&password=password"})
    void isStaticFilePath(String path) {
        HttpUri httpUri = new HttpUri(path);
        assertThat(httpUri.isStaticFilePath()).isTrue();
    }

    @DisplayName("Query String을 제거한 경로를 반환한다.")
    @ParameterizedTest
    @ValueSource(strings = {"/login.html", "/login.html?user=bear&password=password"})
    void removeQueryString(String path) {
        String expected = "/login.html";
        HttpUri httpUri = new HttpUri(path);
        assertThat(httpUri.removedQueryStringPath()).isEqualTo(expected);
    }

    @DisplayName("Query String을 추출한다.")
    @Test
    void extractQueryString() {
        String path = "/login.html?user=bear&password=password";
        HttpUri httpUri = new HttpUri(path);
        assertThat(httpUri.extractQueryString()).isEqualTo("user=bear&password=password");
    }

    @DisplayName("Query String을 가지고 있지 않은 경로에서 Query String을 추출한다.")
    @Test
    void extractQueryStringFromNonQueryString() {
        String path = "/login.html";
        HttpUri httpUri = new HttpUri(path);
        assertThatThrownBy(httpUri::extractQueryString)
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("Query Params를 추출한다.")
    @Test
    void extractQueryParams() {
        // given
        Map<String, String> expected = Map.of("user", "bear", "password", "password");
        String path = "/login.html?user=bear&password=password";
        HttpUri httpUri = new HttpUri(path);

        // when, then
        assertThat(httpUri.extractQueryParams()).containsAllEntriesOf(expected);
    }

    @DisplayName("Query String을 가지고 있지 않은 경로에서 Query Params를 추출한다.")
    @Test
    void extractQueryParamsFromNonQueryString() {
        String path = "/login.html";
        HttpUri httpUri = new HttpUri(path);
        assertThatThrownBy(httpUri::extractQueryParams)
                .isInstanceOf(IllegalStateException.class);
    }
}
