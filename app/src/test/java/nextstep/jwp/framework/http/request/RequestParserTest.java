package nextstep.jwp.framework.http.request;

import nextstep.jwp.framework.http.common.ProtocolVersion;
import nextstep.jwp.framework.http.request.details.HttpMethod;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;

class RequestParserTest {

    private static final String REQUEST =
            "POST /example HTTP/1.1\r\n" +
            "User-Agent: Mozilla/4.0 (compatible; MSIE5.01; Windows NT)\r\n" +
            "Host: www.tutorialspoint.com\r\n" +
            "Content-Type: application/x-www-form-urlencoded\r\n" +
            "Content-Length: 23\r\n" +
            "Accept-Language: en-us\r\n" +
            "Accept-Encoding: gzip, deflate\r\n" +
            "Connection: Keep-Alive\r\n" +
            "\r\n" +
            "key1=value1&key2=value2";

    @DisplayName("RequestParser로 HttpRequest를 추출할 수 있다.")
    @Test
    void extractHttpRequest() throws IOException {
        // given
        final ByteArrayInputStream inputStream = new ByteArrayInputStream(REQUEST.getBytes());

        // when
        final RequestParser requestParser = RequestParser.of(inputStream);
        final HttpRequest httpRequest = requestParser.extractHttpRequest();

        assertThat(httpRequest.getHttpMethod()).isEqualTo(HttpMethod.POST);
        assertThat(httpRequest.getRequestUrl().getUrl()).isEqualTo("/example");
        assertThat(httpRequest.getRequestUrl().getQueryParam()).isEqualTo(null);
        assertThat(httpRequest.getProtocolVersion()).isEqualTo(ProtocolVersion.defaultVersion());
        assertThat(httpRequest.getRequestHttpHeader().getRequestHttpHeaderMap()).containsEntry("Host", "www.tutorialspoint.com");
        assertThat(httpRequest.getRequestHttpHeader().getRequestHttpHeaderMap()).containsEntry("Connection", "Keep-Alive");
        assertThat(httpRequest.getRequestHttpHeader().getRequestHttpHeaderMap()).containsEntry("Content-Length", "23");
        assertThat(httpRequest.getRequestBody().find("key1")).isEqualTo("value1");
        assertThat(httpRequest.getRequestBody().find("key2")).isEqualTo("value2");
    }
}