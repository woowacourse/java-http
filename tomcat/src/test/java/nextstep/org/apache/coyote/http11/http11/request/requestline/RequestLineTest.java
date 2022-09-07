package nextstep.org.apache.coyote.http11.http11.request.requestline;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.apache.coyote.domain.request.requestline.HttpMethod;
import org.apache.coyote.domain.request.requestline.HttpVersion;
import org.apache.coyote.domain.request.requestline.RequestLine;
import org.junit.jupiter.api.Test;

public class RequestLineTest {

    @Test
    void createRequestLine() {
        // given
        String startLine = "GET /login?account=gugu&password=password HTTP/1.0";
        // when
        RequestLine requestLine = RequestLine.from(startLine);
        // then
        assertAll(
                () -> assertThat(requestLine.getHttpMethod()).isEqualTo(HttpMethod.GET),
                () -> assertThat(requestLine.getPath().getPath()).isEqualTo("/login"),
                () -> assertThat(requestLine.getPath().getQueryParam().getQueryValue("account")).isEqualTo("gugu"),
                () -> assertThat(requestLine.getPath().getQueryParam().getQueryValue("password")).isEqualTo("password"),
                () -> assertThat(requestLine.getHttpVersion()).isEqualTo(HttpVersion.HTTP_10)
        );
    }
}
