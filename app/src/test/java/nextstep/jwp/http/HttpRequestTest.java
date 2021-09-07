package nextstep.jwp.http;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import nextstep.jwp.http.entity.HttpBody;
import nextstep.jwp.http.entity.HttpCookie;
import nextstep.jwp.http.entity.HttpHeaders;
import nextstep.jwp.http.entity.HttpMethod;
import nextstep.jwp.http.entity.HttpUri;
import nextstep.jwp.http.entity.HttpVersion;
import nextstep.jwp.http.entity.RequestLine;
import org.junit.jupiter.api.Test;

class HttpRequestTest {
    @Test
    void creationWithEmptyHeader() {
        RequestLine requestLine = RequestLine.of("GET /login HTTP/1.1");
        HttpHeaders httpHeaders = new HttpHeaders();
        HttpBody httpBody = HttpBody.empty();

        HttpRequest request = HttpRequest.of(requestLine, httpHeaders, httpBody);

        assertThat(request.method()).isEqualTo(HttpMethod.GET);
        assertThat(request.uri()).isEqualTo(HttpUri.of("/login"));
        assertThat(request.httpVersion()).isEqualTo(HttpVersion.HTTP_1_1);
        assertThat(request.headers()).isEqualTo(httpHeaders);
        assertThat(request.body()).isEqualTo(httpBody);
        assertThat(request.httpCookie()).isEqualTo(HttpCookie.empty());
    }

    @Test
    void creationWithCookie() {
        RequestLine requestLine = RequestLine.of("GET /login HTTP/1.1");
        HttpHeaders httpHeaders = new HttpHeaders(Map.of("Cookie", "JSESSIONID=testId"));
        HttpBody httpBody = HttpBody.empty();

        HttpRequest request = HttpRequest.of(requestLine, httpHeaders, httpBody);

        assertThat(request.method()).isEqualTo(HttpMethod.GET);
        assertThat(request.uri()).isEqualTo(HttpUri.of("/login"));
        assertThat(request.httpVersion()).isEqualTo(HttpVersion.HTTP_1_1);
        assertThat(request.headers()).isEqualTo(httpHeaders);
        assertThat(request.body()).isEqualTo(httpBody);
        assertThat(request.httpCookie()).isEqualTo(HttpCookie.of("JSESSIONID=testId"));
    }
}
