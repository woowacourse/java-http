package nextstep.jwp.http.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.http.ContentType;
import nextstep.jwp.http.HttpCookie;
import nextstep.jwp.http.HttpMethod;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class HttpRequestTest {

    @Test
    void requestLine의_포맷길이가_맞지_않는_경우_예외발생() {
        String requestLine = "GET /index";
        HttpRequestHeaders httpRequestHeaders = HttpRequestHeaders.from(List.of("Content-Length: 40"));
        HttpRequestBody httpRequestBody = HttpRequestBody.empty();

        assertThatThrownBy(() -> HttpRequest.of(requestLine, httpRequestHeaders, httpRequestBody))
                .isInstanceOf(UncheckedServletException.class)
                .hasMessage("요청의 포맷이 잘못되었습니다.");
    }

    @Test
    void request_line에서_HttpRequest_정보를_반환할_수_있다() {
        String requestLine = "GET /index.html?password=1234 HTTP/1.1";
        HttpRequestHeaders httpRequestHeaders = HttpRequestHeaders.from(List.of("Content-Length: 40"));
        HttpRequestBody httpRequestBody = HttpRequestBody.empty();
        HttpRequest expected = new HttpRequest(HttpMethod.GET, "/index.html", RequestParams.from("password=1234"),
                ContentType.TEXT_HTML, httpRequestHeaders, HttpCookie.empty(), httpRequestBody);
        HttpRequest actual = HttpRequest.of(requestLine, httpRequestHeaders, httpRequestBody);

        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @CsvSource(value = {"/,true", "/root,false"})
    void root_path인지_확인할_수_있다(final String path, final boolean expected) {
        HttpRequest httpRequest = new HttpRequest(HttpMethod.GET, path, RequestParams.from(""), ContentType.TEXT_HTML,
                HttpRequestHeaders.from(List.of("Content-Length: 40")), HttpCookie.empty(), HttpRequestBody.empty());
        boolean actual = httpRequest.isRootPath();

        assertThat(actual).isEqualTo(expected);
    }
}
