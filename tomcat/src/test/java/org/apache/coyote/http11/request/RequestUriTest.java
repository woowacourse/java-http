package org.apache.coyote.http11.request;

import org.apache.coyote.http11.request.line.RequestUri;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RequestUriTest {

    @Test
    @DisplayName("uri를 path와 queryParams로 분리한다.")
    void splitPathAndQueryParams() {
        // given
        String uri = "/login?account=gugu&password=password";

        // when
        RequestUri requestUri = new RequestUri(uri);

        // then
        assertThat(requestUri.getPath()).isEqualTo("/login");
        assertThat(requestUri.getQueryParams().getValue("account")).isEqualTo("gugu");
        assertThat(requestUri.getQueryParams().getValue("password")).isEqualTo("password");
    }

    @Test
    @DisplayName("query string이 없으면 path만 갖고 queryParams는 비어있다.")
    void uriWithoutQuery() {
        // given
        String uri = "/register";

        // when
        RequestUri requestUri = new RequestUri(uri);

        // then
        assertThat(requestUri.getPath()).isEqualTo("/register");
        assertThat(requestUri.getQueryParams().getValue("account")).isNull();
    }

    @Test
    @DisplayName("percent-encoding된 query 값은 디코딩해서 조회한다.")
    void decodePercentEncodedValue() {
        // given
        String uri = "/register?email=new-user%40example.com";

        // when
        RequestUri requestUri = new RequestUri(uri);

        // then
        assertThat(requestUri.getQueryParams().getValue("email")).isEqualTo("new-user@example.com");
    }

    @Test
    @DisplayName("값에 인코딩된 구분자(&)가 있어도 파라미터가 분리되지 않는다.")
    void encodedDelimiterInValueDoesNotSplitParams() {
        // given
        String uri = "/search?keyword=%26tea&page=1";

        // when
        RequestUri requestUri = new RequestUri(uri);

        // then
        assertThat(requestUri.getQueryParams().getValue("keyword")).isEqualTo("&tea");
        assertThat(requestUri.getQueryParams().getValue("tea")).isNull();
        assertThat(requestUri.getQueryParams().getValue("page")).isEqualTo("1");
    }

    @Test
    @DisplayName("URI 문법에 맞지 않으면 예외가 발생한다.")
    void invalidUriThrows() {
        // given
        List<String> invalidUris = List.of(
                "/login page",            // path에 공백
                "/login?account=%zz"      // 잘못된 percent-encoding
        );

        for (String uri : invalidUris) {
            // when & then
            assertThatThrownBy(() -> new RequestUri(uri))
                    .as(uri)
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

}
