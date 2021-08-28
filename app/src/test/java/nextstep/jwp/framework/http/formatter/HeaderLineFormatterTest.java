package nextstep.jwp.framework.http.formatter;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.jwp.framework.http.HttpHeader;
import nextstep.jwp.framework.http.HttpHeaders;
import nextstep.jwp.framework.http.HttpResponse;

import static org.assertj.core.api.Assertions.assertThat;

public class HeaderLineFormatterTest {

    @DisplayName("HttpHeader 1개를 HTTP 형식의 String 으로 변환 테스트")
    @Test
    void transformTest() {

        // given
        final HttpHeaders httpHeaders = new HttpHeaders().addHeader(HttpHeader.HOST, "localhost:8080");

        final HttpResponse httpResponse = new HttpResponse.Builder().httpHeaders(httpHeaders).build();
        final HeaderLineFormatter headerLineFormatter = new HeaderLineFormatter(httpResponse);

        // when
        final String httpStatusLine = headerLineFormatter.transform();

        // then
        assertThat(httpStatusLine).isEqualTo("Host: localhost:8080 \r\n");
    }
}
