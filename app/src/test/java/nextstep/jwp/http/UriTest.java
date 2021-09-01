package nextstep.jwp.http;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("UriTest")
class UriTest {

    @ParameterizedTest
    @ValueSource(strings = {"/index.html", "/css/styles.css", "/js/scripts.js"})
    @DisplayName("html, css, js 라면 true 를 반환한다.")
    void isUriFileTrue(String value) {
        Uri uri = Uri.valueOf(value);

        assertThat(uri.isUriFile()).isTrue();
    }

    @Test
    @DisplayName("위의 조건에 해당하지 않는다면 false 를 반환한다.")
    void isUriFileFalse() {
        Uri uri = Uri.valueOf("/login");

        assertThat(uri.isUriFile()).isFalse();
    }

    @Test
    @DisplayName("쿼리스트링을 넣어도 uri는 분리가 된다.")
    void getResourceUri() {
        Uri uri = Uri.valueOf("/login?account=mungto&password=password");

        assertThat(uri.getResourceUri()).isEqualTo("/login");
    }

    @Test
    @DisplayName("쿼리스트링을 넣으면 따로 가져올 수 있다.")
    void getQueryString() {
        Uri uri = Uri.valueOf("/login?account=mungto&password=password");

        assertThat(uri.getQuery("account")).isEqualTo("mungto");
        assertThat(uri.getQuery("password")).isEqualTo("password");
    }

    @Test
    @DisplayName("없는 쿼리스트링을 꺼내면 Null 을 반환한다.")
    void getQueryStringNull() {
        Uri uri = Uri.valueOf("/login?account=mungto&password=password");

        assertThat(uri.getQuery("test")).isNull();
    }
}