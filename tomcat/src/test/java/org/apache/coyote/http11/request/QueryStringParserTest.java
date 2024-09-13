package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class QueryStringParserTest {

    @DisplayName("성공 : 쿼리 스트링인지 판단할 수 있다")
    @Test
    void isQueryStringSuccess() {
        String queryString = "http://localhost:8080/login?account=gugu&password=password";
        QueryStringParser parser = new QueryStringParser();

        assertThat(parser.isQueryString(queryString)).isTrue();
    }

    @DisplayName("실패 : 쿼리 스트링인지 판단할 수 있다")
    @Test
    void isQueryStringFail() {
        String noneQueryString = "http://localhost:8080/login.html";
        QueryStringParser parser = new QueryStringParser();

        assertThat(parser.isQueryString(noneQueryString)).isFalse();
    }

    @DisplayName("쿼리 스트링에서 Uri을 파싱할 수 있다.")
    @Test
    void parseUri() {
        String url = "http://localhost:8080/login";
        String queryString = url + "?account=gugu&password=password";
        QueryStringParser parser = new QueryStringParser();

        assertThat(parser.parseUri(queryString)).isEqualTo(url);
    }

    @DisplayName("쿼리 스트링의 각 쿼리 요소를 파싱할 수 있다.")
    @Test
    void parseParameters() {
        String key = "account";
        String value = "gugu";
        String queryString = "http://localhost:8080/login?" + key + "=" + value;
        QueryStringParser parser = new QueryStringParser();

        Map<String, String> map = parser.parseParameters(queryString);

        assertThat(map.get(key)).isEqualTo(value);
    }
}
