package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("요청라인 테스트")
class RequestLineTest {

    @DisplayName("요청 라인 파싱에 성공한다.")
    @Test
    void parseRequestLine() {
        // given
        String requestLine = "GET /index.html HTTP/1.1 ";

        // when
        RequestLine line = RequestLine.from(requestLine);

        // then
        assertAll(
                () -> assertThat(line.getMethod()).isEqualTo(HttpMethod.GET),
                () -> assertThat(line.getUrl()).isEqualTo("/index.html"),
                () -> assertThat(line.getVersion()).isEqualTo("HTTP/1.1")
        );
    }

    @DisplayName("쿼리 파라미터 파싱에 성공한다.")
    @Test
    void parseQueryParameter() {
        // given
        String path = "/search";
        String queryKey1 = "name";
        String queryValue1 = "Chocochip";
        String queryKey2 = "age";
        String queryValue2 = "27";

        String queryString = String.join("&",
                queryKey1 + "=" + queryValue1,
                queryKey2 + "=" + queryValue2
        );

        String requestLine = String.join(" ",
                "GET",
                path + "?" + queryString,
                "HTTP/1.1"
        );

        // when
        RequestLine line = RequestLine.from(requestLine);

        // then
        Map<String, String> queryMap = line.getQueryParams();
        assertThat(line.getPath()).isEqualTo(path);
        assertThat(queryMap).containsEntry(queryKey1, queryValue1);
        assertThat(queryMap).containsEntry(queryKey2, queryValue2);
    }
}
