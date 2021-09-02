package nextstep.jwp.http.request;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class RequestLineTest {

    @Test
    void parsing() {
        RequestLine requestLine = RequestLine.of("GET /invalid.html HTTP/1.1");

        assertThat(requestLine.method()).isEqualTo(HttpMethod.GET);
        assertThat(requestLine.sourcePath()).isEqualTo(SourcePath.of("/invalid.html"));
    }

    @Test
    void parsingWithQueryString() {
        RequestLine requestLine = RequestLine
                .of("POST /invalid.html?account=invalidAccount&password=password HTTP/1.1");

        assertThat(requestLine.method()).isEqualTo(HttpMethod.POST);
        assertThat(requestLine.sourcePath()).isEqualTo(SourcePath.of("/invalid.html"));
        assertThat(requestLine.queryParams()).isEqualTo(QueryParams.of("account=invalidAccount&password=password"));
    }
}
