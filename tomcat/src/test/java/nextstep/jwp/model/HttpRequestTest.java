package nextstep.jwp.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Map;
import nextstep.jwp.exception.UncheckedServletException;
import org.junit.jupiter.api.Test;

class HttpRequestTest {

    @Test
    void requestLine의_포맷길이가_맞지_않는_경우_예외발생() {
        String requestLine = "GET /index";
        assertThatThrownBy(() -> HttpRequest.from(requestLine))
                .isInstanceOf(UncheckedServletException.class)
                .hasMessage("요청의 포맷이 잘못되었습니다.");
    }

    @Test
    void query_parameter가_없는_경우_빈값이_들어간다() {
        String requestLine = "GET /index HTTP/1.1";
        HttpRequest actual = HttpRequest.from(requestLine);

        assertThat(actual.getQueryParams()).isEmpty();
    }

    @Test
    void request_line에서_HttpRequest_정보를_반환할_수_있다() {
        String requestLine = "GET /index.html?password=1234 HTTP/1.1";
        HttpRequest expected = new HttpRequest("/index.html?password=1234", "/index.html", Map.of("password", "1234"),
                ContentType.TEXT_HTML, HttpMethod.GET);
        HttpRequest actual = HttpRequest.from(requestLine);

        assertThat(actual).isEqualTo(expected);
    }
}
