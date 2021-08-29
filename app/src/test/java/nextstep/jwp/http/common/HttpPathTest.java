package nextstep.jwp.http.common;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class HttpPathTest {

    @DisplayName("HTML 경로이면 참을 반환한다.")
    @ParameterizedTest
    @ValueSource(strings = {"/login.html", "/login.html?user=bear&password=password"})
    void isHtmlPath(String path) {
        HttpPath httpPath = new HttpPath(path);
        assertThat(httpPath.isHtmlPath()).isTrue();
    }

    @DisplayName("css 경로이면 참을 반환한다.")
    @Test
    void isCssPath() {
        HttpPath httpPath = new HttpPath("/style.css");
        assertThat(httpPath.isCssPath()).isTrue();
    }

    @DisplayName("javascript 경로이면 참을 반환한다.")
    @Test
    void isJavaScriptPath() {
        HttpPath httpPath = new HttpPath("/scripts.js");
        assertThat(httpPath.isJavaScriptPath()).isTrue();
    }

    @DisplayName("redirect 경로이면 참을 반환한다.")
    @Test
    void isRedirectPath() {
        HttpPath httpPath = new HttpPath("redirect:/login.html");
        assertThat(httpPath.isHtmlPath()).isTrue();
    }

    @DisplayName("redirect: 접두사를 제거한 경로룰 반환한다.")
    @Test
    void removeRedirectPrefix() {
        String expected = "/login.html";
        HttpPath httpPath = new HttpPath("redirect:/login.html");
        assertThat(httpPath.removeRedirectPrefix()).isEqualTo(expected);
    }

    @DisplayName("Query String을 제거한 경로를 반환한다.")
    @ParameterizedTest
    @ValueSource(strings = {"/login.html", "/login.html?user=bear&password=password"})
    void removeQueryString(String path) {
        String expected = "/login.html";
        HttpPath httpPath = new HttpPath(path);
        assertThat(httpPath.removeQueryString()).isEqualTo(expected);
    }

    @DisplayName("Query String을 가지고 있으면 참을 반환한다. (?의 여부로 판단)")
    @ParameterizedTest
    @ValueSource(strings = {"/login.html?", "/login.html?user=bear&password=password"})
    void hasQueryString(String path) {
        HttpPath httpPath = new HttpPath(path);
        assertThat(httpPath.hasQueryString()).isTrue();
    }

    @DisplayName("Query String을 추출한다.")
    @Test
    void extractQueryString() {
        String path = "/login.html?user=bear&password=password";
        HttpPath httpPath = new HttpPath(path);
        assertThat(httpPath.extractQueryString()).isEqualTo("user=bear&password=password");
    }

    @DisplayName("Query String을 가지고 있지 않은 경로에서 Query String을 추출한다.")
    @Test
    void extractQueryStringFromNonQueryString() {
        String path = "/login.html";
        HttpPath httpPath = new HttpPath(path);
        assertThatThrownBy(httpPath::extractQueryString)
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("Query Params를 추출한다.")
    @Test
    void extractQueryParams() {
        // given
        Map<String, String> expected = Map.of("user", "bear", "password", "password");
        String path = "/login.html?user=bear&password=password";
        HttpPath httpPath = new HttpPath(path);

        // when, then
        assertThat(httpPath.extractQueryParams()).containsAllEntriesOf(expected);
    }

    @DisplayName("Query String을 가지고 있지 않은 경로에서 Query Params를 추출한다.")
    @Test
    void extractQueryParamsFromNonQueryString() {
        String path = "/login.html";
        HttpPath httpPath = new HttpPath(path);
        assertThatThrownBy(httpPath::extractQueryParams)
                .isInstanceOf(IllegalStateException.class);
    }
}