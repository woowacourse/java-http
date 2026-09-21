package org.apache.http.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.apache.http.HttpMethod;
import org.junit.jupiter.api.Test;

class HttpRequestParserTest {

    private final HttpRequestParser parser = new HttpRequestParser();

    @Test
    void 요청_라인과_헤더와_파라미터와_쿠키를_파싱한다() {
        // given
        String input = String.join("\r\n",
                "POST /login?redirect=%2Findex.html HTTP/1.1",
                "Host: localhost:8080",
                "Cookie: JSESSIONID=session-id; theme=dark=mode",
                "Content-Type: application/x-www-form-urlencoded",
                "",
                "account=gugu&password=p%40ssword"
        );

        // when
        HttpTomcatRequest request = parser.parse(HttpTomcatRequest.class, input);

        // then
        assertThat(request.getHttpMethod()).isEqualTo(HttpMethod.POST);
        assertThat(request.getUrl()).isEqualTo("/login");
        assertThat(request.getProtocol()).isEqualTo("HTTP/1.1");
        assertThat(request.getHeader("host")).isEqualTo("localhost:8080");
        assertThat(request.getQueryParams())
                .containsEntry("redirect", "/index.html");
        assertThat(request.getBody("account")).isEqualTo("gugu");
        assertThat(request.getBody("password")).isEqualTo("p@ssword");
        assertThat(request.getCookie("JSESSIONID")).isEqualTo("session-id");
        assertThat(request.getCookie("theme")).isEqualTo("dark=mode");
    }

    @Test
    void 선택적인_요청_정보가_없으면_빈_값으로_파싱한다() {
        // given
        String input = "GET / HTTP/1.1\r\n\r\n";

        // when
        HttpTomcatRequest request = parser.parse(HttpTomcatRequest.class, input);

        // then
        assertThat(request.getQueryParams()).isEmpty();
        assertThat(request.getBodys()).isEmpty();
        assertThat(request.getCookies()).isEmpty();
        assertThat(request.getSession(false)).isNull();
    }

    @Test
    void 올바르지_않은_요청_라인이면_예외가_발생한다() {
        // given
        String input = "GET /\r\n\r\n";

        // when & then
        assertThatThrownBy(() -> parser.parse(HttpTomcatRequest.class, input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("올바르지 않은 요청 라인");
    }
}
